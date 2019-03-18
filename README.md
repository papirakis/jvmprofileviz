# Documentation #

## Introduction ##

**jvmprofileviz** is a sampling profiler based on **jvmtop** code base. Jvmtop is meant to be used from the command line
and display information about currently running java programs. Also, it limits itself to flat profiling
which only shows a stack rank of methods on top of callstacks. Further, due to limitations of the JVM,
it cannot know for certain which threads are running and which ones are blocked on IO or other kernel
activities.

Jvmprofileviz is different. The main idea is to perform a profiling activity in two steps:

* Collect information about a running process
* Curate, analyze and display the information

Both these steps are meant to be performed using the same tool, but on different machines/environments.
This is particularily useful when running containerized micro-services in the cloud. In this case, you can
copy this software on the container and run it from the command line with no UI. This will create a json file
that contains the profiling information.

Then, on an environment that supports Java UI and an SVG display client (such as Chrome), you can curate the 
data and view it as a directed graph.

## Compatibility ##

jvmprofileviz has been tested using the Oracle JDK, IBM JDK (J9) and OpenJDK under Linux, Solaris and Windows. At least version 6 of the JDK is required.


## Installation ##

To install jvmprofileviz, just extract the downloaded the gzipped tarball in a directory of your choice.

Ensure that the environment variable `JAVA_HOME` is set to a path of a JDK. **A JRE is not sufficient.**

Start jvmprofileviz with the execution of `jvmprofileviz.sh`

### VM overview mode ###

Command-line: `jvmprofileviz.sh --help`

```
jvmprofileviz - java monitoring from inside Docker containers and more!
Usage: jvmprofileviz.sh [options...]

Option                Description
------                -----------
-?, -h, --help        shows this help
-d, --delay <Double>  delay between each output iteration
-o, --out             The output file where the profiling
                        information will be stored.
-p, --pid <Integer>   PID to connect to
-t, --time <Integer>  Total time to run the profiler for in
                        seconds. Defaults to 60.
```

Command-line: `jvmprofileviz.sh -p 47254 -t 5 -o data.json`

This will attach to the java process with ID 47254. Then for 5 seconds, it will collect samples at a
rate of 10 per second and save the output in data.json. *NOTE*: the size of the output file doesn't grow
significantly if you run the profiler for longer periods of time. The default of 60 seconds is recommended.