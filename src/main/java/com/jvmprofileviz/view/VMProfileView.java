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
package com.jvmprofileviz.view;

import com.jvmprofileviz.monitor.VMInfo;
import com.jvmprofileviz.openjdk.tools.LocalVirtualMachine;
import com.jvmprofileviz.profiler.CPUSampler;

/**
 * CPU sampling-based profiler view which shows methods with top CPU usage.
 *
 * @author paru
 *
 */
public class VMProfileView  {
    private static final int MIN_WIDTH = 80;

    private boolean shouldExit_ = false;

    private CPUSampler cpuSampler_;

    private VMInfo vmInfo_;

    public VMProfileView(int vmid, Integer width) throws Exception {
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        vmInfo_ = VMInfo.processNewVM(localVirtualMachine, vmid);
        cpuSampler_ = new CPUSampler(vmInfo_);
    }

    public void sleep(long millis) throws Exception {
        long cur = System.currentTimeMillis();
        cpuSampler_.update();
        while (cur + millis > System.currentTimeMillis()) {
            cpuSampler_.update();
            sleep(100);
        }
    }

    public boolean shouldExit() {
        return shouldExit_;
    }

    /**
     * Requests the disposal of this view - it should be called again.
     * TODO: refactor / remove this functional, use proper exception handling instead.
     */
    public void exit() {
        shouldExit_ = true;
    }
}
