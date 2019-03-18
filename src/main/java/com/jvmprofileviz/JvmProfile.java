/**
 * JvmProfile - java monitoring from inside Docker containers and more!
 *
 * Copyright (C) 2019 by Emmanuel Papirakis. All rights reserved.
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

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jvmprofileviz.stacktrace.StackTraceStats;
import com.jvmprofileviz.ui.MainWindow;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.jvmprofileviz.profiler.VMProfiler;

/**
 * JvmProfile entry point class.
 *
 * - parses program arguments
 * - main "iteration loop"
 *
 * @author Emmanuel Papirakis
 *
 */
public class JvmProfile {
    private Double delay = 0.1;

    private int totalSeconds;

    private static OptionParser createOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList(new String[]{"help", "?", "h"}),
                "shows this help").forHelp();
        parser.acceptsAll(Arrays.asList(new String[]{"d", "delay"}),
                "delay between each output iteration").withRequiredArg()
                .ofType(Double.class);

        parser.acceptsAll(Arrays.asList(new String[]{"p", "pid"}),
                "PID to connect to").withRequiredArg().ofType(Integer.class);

        parser.acceptsAll(Arrays.asList(new String[]{"t", "time"}),
                "Total time to run the profiler for in seconds. Defaults to 60.").withRequiredArg().ofType(Integer.class);

        parser.acceptsAll(Arrays.asList(new String[] { "o", "out" }),
                "The output file where the profiling information will be stored.").withRequiredArg().ofType(String.class);

        return parser;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        OptionParser parser = createOptionParser();
        OptionSet a = parser.parse(args);

        if (a.has("help")) {
            printHelp(parser);
        }

        Integer pid = null;

        String outputFile = null;

        double delay = 1.0;

        int totalSeconds = 60;

        if (a.hasArgument("delay")) {
            delay = (Double) (a.valueOf("delay"));
            if (delay < 0.1d) {
                throw new IllegalArgumentException("Delay cannot be set below 0.1");
            }
        }

        if (a.hasArgument("pid")) {
            pid = (Integer) a.valueOf("pid");
        }

        if (a.hasArgument("time")) {
            totalSeconds = (Integer) a.valueOf("time");
        }

        if (a.hasArgument("out")) {
            outputFile = (String) a.valueOf("out");
        }

        if (pid == null) {
            if (outputFile != null) {
                System.err.println("With no PID specified, you cannot provide an output file name.");
                System.err.println();
                printHelp(parser);
            }
        } else {
            if (outputFile == null) {
                System.err.println("Without no PID specified, you need to provide an output file name.");
                System.err.println();
                printHelp(parser);
            }
        }

        if (pid != null) {
            JvmProfile jvmProfile = new JvmProfile();
            jvmProfile.setDelay(delay);
            jvmProfile.setTotalSeconds(totalSeconds);

            VMProfiler profiler = new VMProfiler(pid);
            jvmProfile.run(profiler);
            writeToFile(profiler.getStats(), outputFile);
        } else {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    MainWindow mw = new MainWindow();
                    try {
                        mw.loadFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mw.setVisible(true);
                }
            });
        }
    }

    public static void printHelp(OptionParser parser) throws IOException {
        System.err.println("jvmprofileviz - java monitoring from inside Docker containers and more!");
        System.err.println("Usage: jvmprofileviz.sh [options...]");
        System.err.println();
        parser.printHelpOn(System.err);
        System.exit(0);
    }

    protected void run(VMProfiler profiler) {
        long startTime = System.currentTimeMillis();

        try {
            System.setOut(new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(FileDescriptor.out)), false));

            long elapsedTime;
            do {
                profiler.processIterationAndThenSleep((int) (delay * 1000));
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            } while (elapsedTime < this.totalSeconds);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace(System.err);

            System.err.println();
            System.err.println("ERROR: Some JDK classes cannot be found.");
            System.err.println("       Please check if the JAVA_HOME environment variable has been set to a JDK path.");
            System.err.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(StackTraceStats stats, String fileName) throws IOException {
        FileWriter writer = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            writer = new FileWriter(new File(fileName));
            writer.write(mapper.writeValueAsString(stats));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
