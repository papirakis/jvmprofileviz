package com.jvmprofileviz.stacktrace;

import java.util.List;

public class StackTraceInfo {
    public StackTraceInfo() { }

    public StackTraceInfo(List<Integer> callStack, Integer count) {
        this.callStack = callStack;
        this.count = count;
    }

    public List<Integer> callStack;

    public Integer count;
}
