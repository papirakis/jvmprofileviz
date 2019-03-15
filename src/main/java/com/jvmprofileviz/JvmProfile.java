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
package com.jvmprofileviz;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Locale;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.jvmprofileviz.profiler.VMProfiler;

/**
 * JvmProfile entry point class.
 *
 * - parses program arguments
 * - selects console profile
 * - prints header
 * - main "iteration loop"
 *
 * TODO: refactor to split these tasks
 *
 * @author paru
 *
 */
public class JvmProfile {
    public static final String VERSION = "0.0.1";

    private Double delay = 1.0;

    private Boolean supportsSystemAverage;

    private java.lang.management.OperatingSystemMXBean localOSBean;

    private int totalSeconds;

    private static OptionParser createOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList(new String[]{ "help", "?", "h" }),
                "shows this help").forHelp();
        parser.acceptsAll(Arrays.asList(new String[]{ "d", "delay" }),
                        "delay between each output iteration").withRequiredArg()
                .ofType(Double.class);

        parser.acceptsAll(Arrays.asList(new String[]{ "p", "pid" }),
                        "PID to connect to").withRequiredArg().ofType(Integer.class);

        parser.acceptsAll(Arrays.asList(new String[] { "t", "time" }),
                "Total time to run the profiler for in seconds. Defaults to 60.").withRequiredArg().ofType(Integer.class);

        return parser;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        OptionParser parser = createOptionParser();
        OptionSet a = parser.parse(args);

        if (a.has("help")) {
            System.out.println("jvmtop - java monitoring for the command-line");
            System.out.println("Usage: jvmtop.sh [options...] [PID]");
            System.out.println("");
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        Integer pid = null;

        Integer width = null;

        double delay = 1.0;

        int totalSeconds = 60;

        Integer iterations = a.has("once") ? 1 : -1;

        if (a.hasArgument("delay")) {
            delay = (Double) (a.valueOf("delay"));
            if (delay < 0.1d) {
                throw new IllegalArgumentException("Delay cannot be set below 0.1");
            }
        }

        //to support PID as non option argument
        if (a.nonOptionArguments().size() > 0) {
            pid = Integer.valueOf((String) a.nonOptionArguments().get(0));
        }

        if (a.hasArgument("pid")) {
            pid = (Integer) a.valueOf("pid");
        }

        if (a.hasArgument("time")) {
            totalSeconds = (Integer) a.valueOf("time");
        }

        JvmProfile jvmProfile = new JvmProfile();
        jvmProfile.setDelay(delay);
        jvmProfile.setTotalSeconds(totalSeconds);
        jvmProfile.run(new VMProfiler(pid, width));
    }

    protected void run(VMProfiler view) throws Exception {
        long startTime = System.currentTimeMillis();

        try {
            System.setOut(new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(FileDescriptor.out)), false));

            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            while (elapsedTime < this.totalSeconds) {
                view.processIterationAndThenSleep((int) (delay * 1000));
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace(System.err);

            System.err.println("");
            System.err.println("ERROR: Some JDK classes cannot be found.");
            System.err
                    .println("       Please check if the JAVA_HOME environment variable has been set to a JDK path.");
            System.err.println("");
        }
    }

    public JvmProfile() {
        localOSBean = ManagementFactory.getOperatingSystemMXBean();
    }

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
