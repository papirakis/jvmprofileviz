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

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import com.jvmprofileviz.monitor.VMInfo;
import com.jvmprofileviz.stacktrace.StackTraceStatsBuilder;
import com.jvmprofileviz.stacktrace.StackTraceStats;

/**
 * Experimental and very basic sampling-based CPU-Profiler.
 *
 * It uses package excludes to filter common 3rd party libraries which often
 * distort application problems.
 *
 * @author paru
 *
 */
public class CPUSampler {
    private ThreadMXBean threadMxBean = null;

    private final StackTraceStatsBuilder stackTraceStatsBuilder = new StackTraceStatsBuilder();

    /**
     * @param vmInfo
     * @throws Exception
     */
    public CPUSampler(VMInfo vmInfo) {
        super();
        threadMxBean = vmInfo.getThreadMXBean();
    }

    public void update() {
        for (ThreadInfo ti : threadMxBean.dumpAllThreads(false, false)) {

            if (ti.getStackTrace().length > 0
                    && ti.getThreadState() == State.RUNNABLE
            ) {
                StackTraceElement[] stackTrace = ti.getStackTrace();
                ArrayList<String> stackTraceAsStrings = new ArrayList<String>();

                for (int i = stackTrace.length - 1; i >= 0; i--) {
                    StackTraceElement stElement = stackTrace[i];
                    String keyFrom = stElement.getClassName() + "."
                            + stElement.getMethodName();
                    stackTraceAsStrings.add(keyFrom);
                }

                stackTraceStatsBuilder.addStackTrace(stackTraceAsStrings);
            }
        }
    }

    public StackTraceStats getStats() {
        return stackTraceStatsBuilder.getStats();
    }
}

