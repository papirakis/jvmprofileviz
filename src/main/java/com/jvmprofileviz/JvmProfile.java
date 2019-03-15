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
import java.util.Date;
import java.util.Locale;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.jvmprofileviz.view.VMProfileView;

/**
 * JvmProfile entry point class.
 *
 * - parses program arguments
 * - selects console view
 * - prints header
 * - main "iteration loop"
 *
 * TODO: refactor to split these tasks
 *
 * @author paru
 *
 */
public class JvmProfile {
    public static final String VERSION = "0.8.0 alpha";

    private Double delay_ = 1.0;

    private Boolean supportsSystemAverage_;

    private java.lang.management.OperatingSystemMXBean localOSBean_;

    private final static String CLEAR_TERMINAL_ANSI_CMD = new String(
            new byte[]{
                    (byte) 0x1b, (byte) 0x5b, (byte) 0x32, (byte) 0x4a, (byte) 0x1b,
                    (byte) 0x5b, (byte) 0x48});

    private int maxIterations_ = -1;

    private static OptionParser createOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList(new String[]{"help", "?", "h"}),
                "shows this help").forHelp();
        parser
                .acceptsAll(Arrays.asList(new String[]{"d", "delay"}),
                        "delay between each output iteration").withRequiredArg()
                .ofType(Double.class);

        parser
                .acceptsAll(Arrays.asList(new String[]{"p", "pid"}),
                        "PID to connect to").withRequiredArg().ofType(Integer.class);

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

        Integer iterations = a.has("once") ? 1 : -1;

        if (a.hasArgument("delay")) {
            delay = (Double) (a.valueOf("delay"));
            if (delay < 0.1d) {
                throw new IllegalArgumentException("Delay cannot be set below 0.1");
            }
        }

        if (a.hasArgument("n")) {
            iterations = (Integer) a.valueOf("n");
        }

        //to support PID as non option argument
        if (a.nonOptionArguments().size() > 0) {
            pid = Integer.valueOf((String) a.nonOptionArguments().get(0));
        }

        if (a.hasArgument("pid")) {
            pid = (Integer) a.valueOf("pid");
        }

        if (a.hasArgument("width")) {
            width = (Integer) a.valueOf("width");
        }

        JvmProfile jvmProfile = new JvmProfile();
        jvmProfile.setDelay(delay);
        jvmProfile.run(new VMProfileView(pid, width));
    }

    protected void run(VMProfileView view) throws Exception {
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(FileDescriptor.out)), false));
            int iterations = 0;
            while (!view.shouldExit()) {
                if (maxIterations_ > 1 || maxIterations_ == -1) {
                    clearTerminal();
                }
                printTopBar();
                System.out.flush();
                iterations++;
                if (iterations >= maxIterations_ && maxIterations_ > 0) {
                    break;
                }
                view.sleep((int) (delay_ * 1000));
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

    /**
     *
     */
    private void clearTerminal() {
        if (System.getProperty("os.name").contains("Windows")) {
            //hack
            System.out
                    .printf("%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n");
        } else if (System.getProperty("jvmtop.altClear") != null) {
            System.out.print('\f');
        } else {
            System.out.print(CLEAR_TERMINAL_ANSI_CMD);
        }
    }

    public JvmProfile() {
        localOSBean_ = ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private void printTopBar() {
        System.out.printf(" JvmProfile %s - %8tT, %6s, %2d cpus, %15.15s", VERSION,
                new Date(), localOSBean_.getArch(),
                localOSBean_.getAvailableProcessors(), localOSBean_.getName() + " "
                        + localOSBean_.getVersion());

        if (supportSystemLoadAverage() && localOSBean_.getSystemLoadAverage() != -1) {
            System.out.printf(", load avg %3.2f%n",
                    localOSBean_.getSystemLoadAverage());
        } else {
            System.out.println();
        }
        System.out.println(" https://github.com/patric-r/jvmtop");
        System.out.println();
    }

    private boolean supportSystemLoadAverage() {
        if (supportsSystemAverage_ == null) {
            try {
                supportsSystemAverage_ = (localOSBean_.getClass().getMethod(
                        "getSystemLoadAverage") != null);
            } catch (Throwable e) {
                supportsSystemAverage_ = false;
            }
        }
        return supportsSystemAverage_;
    }

    public void setDelay(Double delay) {
        delay_ = delay;
    }
}
