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
package com.jvmprofileviz.profiler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jvmprofileviz.monitor.VMInfo;
import com.jvmprofileviz.openjdk.tools.LocalVirtualMachine;

public class VMProfiler {
    private CPUSampler cpuSampler;

    private VMInfo vmInfo;

    public VMProfiler(int vmid, Integer width) throws Exception {
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        vmInfo = VMInfo.processNewVM(localVirtualMachine, vmid);
        cpuSampler = new CPUSampler(vmInfo);
    }

    public void processIterationAndThenSleep(long millis) throws Exception {
        long cur = System.currentTimeMillis();
        cpuSampler.update();
        while (cur + millis > System.currentTimeMillis()) {
            cpuSampler.update();
            Thread.sleep(100);
        }
    }

    public String getSerializedGraph() throws JsonProcessingException {
        return cpuSampler.getSerializedGraph();
    }
}
