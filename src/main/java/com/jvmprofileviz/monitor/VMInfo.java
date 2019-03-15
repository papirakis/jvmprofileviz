/**
 * jvmtop - java monitoring for the command-line
 *
 * Copyright (C) 2013 by Patric Rufflar. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.jvmprofileviz.monitor;

import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.rmi.ConnectException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jvmprofileviz.openjdk.tools.ConnectionState;
import com.jvmprofileviz.openjdk.tools.LocalVirtualMachine;
import com.jvmprofileviz.openjdk.tools.ProxyClient;
import com.sun.tools.attach.AttachNotSupportedException;

/**
 * VMInfo retrieves or updates the metrics for a specific remote jvm,
 * using ProxyClient.
 *
 * TODO: refactor this class, seperate:
 * - connect / attach code
 * - updating metrics
 * - model
 *
 * @author paru
 *
 */
public class VMInfo {
    private ProxyClient proxyClient = null;

    private OperatingSystemMXBean osBean;

    private RuntimeMXBean runtimeMXBean;

    private Collection<java.lang.management.GarbageCollectorMXBean> gcMXBeans;

    private long lastGcTime;

    private long lastUpTime = -1;

    private long lastCPUTime = -1;

    private MemoryMXBean memoryMXBean;

    private ThreadMXBean threadMXBean;

    private VMInfoState state_ = VMInfoState.INIT;

    private int updateErrorCount_ = 0;

    private Map<String, String> systemProperties_;

    /**
     * @throws RuntimeException
     */
    public VMInfo(ProxyClient proxyClient) throws Exception {
        super();
        this.proxyClient = proxyClient;
        state_ = VMInfoState.ATTACHED;
        update();
    }

    /**
     * TODO: refactor to constructor?
     *
     * @param localvm
     * @param vmid
     * @return
     */
    public static VMInfo processNewVM(LocalVirtualMachine localvm, int vmid) {

        try {
            if (localvm == null || !localvm.isAttachable()) {
                Logger.getLogger("jvmtop").log(Level.FINE,
                        "jvm is not attachable (PID=" + vmid + ")");
                return VMInfo.createDeadVM(localvm);
            }
            return attachToVM(localvm, vmid);
        } catch (Exception e) {
            Logger.getLogger("jvmtop").log(Level.FINE,
                    "error during attach (PID=" + vmid + ")", e);
            return VMInfo.createDeadVM(localvm);
        }
    }

    /**
     * Creates a new VMInfo which is attached to a given LocalVirtualMachine
     *
     * @param localvm
     * @param vmid
     * @return
     * @throws AttachNotSupportedException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Exception
     */
    private static VMInfo attachToVM(LocalVirtualMachine localvm, int vmid)
            throws AttachNotSupportedException, IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, Exception {
        //VirtualMachine vm = VirtualMachine.attach("" + vmid);
        try {

            ProxyClient proxyClient = ProxyClient.getProxyClient(localvm);
            proxyClient.connect();
            if (proxyClient.getConnectionState() == ConnectionState.DISCONNECTED) {
                Logger.getLogger("jvmtop").log(Level.FINE,
                        "connection refused (PID=" + vmid + ")");
                return createDeadVM(localvm);
            }
            return new VMInfo(proxyClient);
        } catch (ConnectException rmiE) {
            if (rmiE.getMessage().contains("refused")) {
                Logger.getLogger("jvmtop").log(Level.FINE,
                        "connection refused (PID=" + vmid + ")", rmiE);
                return createDeadVM(localvm, VMInfoState.CONNECTION_REFUSED);
            }
            rmiE.printStackTrace(System.err);
        } catch (IOException e) {
            if ((e.getCause() != null
                    && e.getCause() instanceof AttachNotSupportedException)
                    || e.getMessage().contains("Permission denied")) {
                Logger.getLogger("jvmtop").log(Level.FINE,
                        "could not attach (PID=" + vmid + ")", e);
                return createDeadVM(localvm, VMInfoState.CONNECTION_REFUSED);
            }
            e.printStackTrace(System.err);
        } catch (Exception e) {
            Logger.getLogger("jvmtop").log(Level.WARNING,
                    "could not attach (PID=" + vmid + ")", e);
        }
        return createDeadVM(localvm);
    }

    private VMInfo() {

    }

    /**
     * Creates a dead VMInfo, representing a jvm which cannot be attached or other monitoring issues occurred.
     *
     * @param localVm
     * @return
     */
    public static VMInfo createDeadVM(LocalVirtualMachine localVm) {
        return createDeadVM(localVm, VMInfoState.ERROR_DURING_ATTACH);
    }

    /**
     * Creates a dead VMInfo, representing a jvm in a given state
     * which cannot be attached or other monitoring issues occurred.
     *
     * @param localVm
     * @return
     */
    public static VMInfo createDeadVM(LocalVirtualMachine localVm,
                                      VMInfoState state) {
        VMInfo vmInfo = new VMInfo();
        vmInfo.state_ = state;
        return vmInfo;
    }

    /**
     * Updates all jvm metrics to the most recent remote values
     *
     * @throws Exception
     */
    public void update() throws Exception {
        if (state_ == VMInfoState.ERROR_DURING_ATTACH
                || state_ == VMInfoState.DETACHED
                || state_ == VMInfoState.CONNECTION_REFUSED) {
            return;
        }

        if (proxyClient.isDead()) {
            state_ = VMInfoState.DETACHED;
            return;
        }

        try {
            proxyClient.flush();

            osBean = proxyClient.getSunOperatingSystemMXBean();
            runtimeMXBean = proxyClient.getRuntimeMXBean();
            gcMXBeans = proxyClient.getGarbageCollectorMXBeans();
            memoryMXBean = proxyClient.getMemoryMXBean();
            threadMXBean = proxyClient.getThreadMXBean();

            //TODO: fetch jvm-constant data only once
            systemProperties_ = runtimeMXBean.getSystemProperties();
            updateInternal();
        } catch (Throwable e) {
            Logger.getLogger("jvmtop").log(Level.FINE, "error during update", e);
            updateErrorCount_++;
            if (updateErrorCount_ > 10) {
                state_ = VMInfoState.DETACHED;
            } else {
                state_ = VMInfoState.ATTACHED_UPDATE_ERROR;
            }
        }
    }

    /**
     * calculates internal delta metrics
     *
     * @throws Exception
     */
    private void updateInternal() throws Exception {
        long uptime = runtimeMXBean.getUptime();

        long cpuTime = proxyClient.getProcessCpuTime();
        //long cpuTime = osBean.getProcessCpuTime();
        long gcTime = sumGCTimes();
        lastUpTime = uptime;
        lastCPUTime = cpuTime;
        lastGcTime = gcTime;
    }

    /**
     * calculates a "load", given on two deltas
     *
     * @param deltaUptime
     * @param deltaTime
     * @return
     */
    private double calcLoad(double deltaUptime, double deltaTime) {
        if (deltaTime <= 0 || deltaUptime == 0) {
            return 0.0;
        }
        return Math.min(99.0,
                deltaTime / (deltaUptime * osBean.getAvailableProcessors()));
    }

    /**
     * Returns the sum of all GC times
     *
     * @return
     */
    private long sumGCTimes() {
        long sum = 0;
        for (java.lang.management.GarbageCollectorMXBean mxBean : gcMXBeans) {
            sum += mxBean.getCollectionTime();
        }
        return sum;
    }

    /**
     * Returns the sum of all GC invocations
     *
     * @return
     */
    private long sumGCCount() {
        long sum = 0;
        for (java.lang.management.GarbageCollectorMXBean mxBean : gcMXBeans) {
            sum += mxBean.getCollectionCount();
        }
        return sum;
    }

    /**
     * @return the proxyClient
     */
    public ProxyClient getProxyClient() {
        return proxyClient;
    }

    public ThreadMXBean getThreadMXBean() {
        return threadMXBean;
    }

    /**
     * Extracts the jvmtop "short version" out of different properties
     * TODO: should this be refactored?
     *
     * @return
     */
    private String extractShortVer() {
        String vmVer = systemProperties_.get("java.runtime.version");

        String vmVendor = systemProperties_.get("java.vendor");

        Pattern pattern = Pattern.compile("[0-9]\\.([0-9])\\.0_([0-9]+)-.*");
        Matcher matcher = pattern.matcher(vmVer);
        if (matcher.matches()) {
            return vmVendor.charAt(0) + matcher.group(1) + "U" + matcher.group(2);
        } else {
            pattern = Pattern.compile(".*-(.*)_.*");
            matcher = pattern.matcher(vmVer);
            if (matcher.matches()) {
                return vmVendor.charAt(0) + matcher.group(1).substring(2, 6);
            }
            return vmVer;
        }
    }
}
