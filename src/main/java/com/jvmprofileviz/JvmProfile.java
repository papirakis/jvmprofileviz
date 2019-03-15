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

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

import com.jvmprofileviz.graph.GraphData;
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

        parser.acceptsAll(Arrays.asList(new String[] { "i", "input" }),
                "The input file from a previous profiling session.").withRequiredArg().ofType(String.class);

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

        String inputFile = null;

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

        if (a.hasArgument("input")) {
            inputFile = (String) a.valueOf("input");
        }

        if (pid == null) {
            if (inputFile == null) {
                System.err.println("With no PID specified, you need to provide an input file name.");
                System.err.println();
                printHelp(parser);
            }

            if (outputFile == null) {
                System.err.println("With no PID specified, you need to provide an output file name.");
                System.err.println();
                printHelp(parser);
            }
        } else {
            if (inputFile != null) {
                System.err.println("Without a PID specified, you cannot provide an input file name.");
                System.err.println();
                printHelp(parser);
            }

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
            profiler.getGraphData().writeToFile(outputFile);
        } else {
            // Display the graph with graphviz.
            GraphData graph = new GraphData(inputFile);
            graph.writeSvgGraphFile(outputFile);
        }
    }

    public static void printHelp(OptionParser parser) throws IOException {
        System.err.println("jvmtop - java monitoring for the command-line");
        System.err.println("Usage: jvmtop.sh [options...] [PID]");
        System.err.println();
        parser.printHelpOn(System.err);
        System.exit(0);
    }

    protected void run(VMProfiler profiler) throws Exception {
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

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
