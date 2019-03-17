package com.jvmprofileviz.stacktrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackTraceStatsBuilder {
    private final IdManager idManager;
    private final HashMap<List<Integer>, Integer> callStacks = new HashMap<List<Integer>, Integer>();

    public StackTraceStatsBuilder() {
        idManager = new IdManager();
    }

    public void addStackTrace(List<String> callStack) {
        ArrayList<Integer> intCallStack = new ArrayList<Integer>();

        for (String method : callStack) {
            int methodId = idManager.getIdForMethodName(method);
            intCallStack.add(methodId);
        }

        if (!callStacks.containsKey(intCallStack)) {
            callStacks.put(intCallStack, 1);
        } else {
            Integer curentCount = callStacks.get(intCallStack);
            curentCount++;
            callStacks.put(intCallStack, curentCount);
        }
    }

    public List<StackTraceInfo> getStackTraces() {
        ArrayList<StackTraceInfo> result = new ArrayList<StackTraceInfo>();

        for (Map.Entry<List<Integer>, Integer> entry : callStacks.entrySet()) {
            StackTraceInfo newEntry = new StackTraceInfo(entry.getKey(), entry.getValue());
            result.add(newEntry);
        }

        return result;
    }

    public StackTraceStats getStats() {
        StackTraceStats result = new StackTraceStats();

        result.methods = idManager.getMapping();
        result.data = getStackTraces();
        return result;
    }
}
