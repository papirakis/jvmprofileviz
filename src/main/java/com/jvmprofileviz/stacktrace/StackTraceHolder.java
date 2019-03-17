package com.jvmprofileviz.stacktrace;

import com.jvmprofileviz.graph.GraphData;
import guru.nidi.graphviz.model.MutableGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class StackTraceHolder {
    private final IdManager idManager;

    private final List<StackTraceInfo> stackTraceInfo;

    public StackTraceHolder(StackTraceStats stats) {
        if (stats == null || stats.data == null || stats.methods == null) {
            throw new IllegalArgumentException("Cannot have null StackTraceStats information");
        }

        idManager = new IdManager(stats.methods);
        stackTraceInfo = new ArrayList<StackTraceInfo>(stats.data);
        stackTraceInfo.removeIf(new Predicate<StackTraceInfo>() {
            @Override
            public boolean test(StackTraceInfo info) {
                return (info == null || info.callStack == null || info.count == null || info.count == 0 || info.callStack.size() == 0);
            }
        });

        if (stackTraceInfo.size() == 0) {
            throw new IllegalArgumentException("No useful profiling stackTraceInfo found.");
        }
    }

    public List<TopOfStackInfo> getTopOfStacks() {
        HashMap<Integer, Integer> topOfStacks = new HashMap<Integer, Integer>();

        for (StackTraceInfo info : stackTraceInfo) {
            int lastIndex = info.callStack.size() - 1;
            Integer top = info.callStack.get(lastIndex);

            if (topOfStacks.containsKey(top)) {
                Integer current = topOfStacks.get(top);
                current += info.count;
                topOfStacks.put(top, current);
            } else {
                topOfStacks.put(top, info.count);
            }
        }

        List<TopOfStackInfo> result = new ArrayList<TopOfStackInfo>();

        for (Integer top : topOfStacks.keySet()) {
            TopOfStackInfo info = new TopOfStackInfo();
            info.methodName = idManager.getMethodNameById(top);
            info.count = topOfStacks.get(top);
            result.add(info);
        }

        return result;
    }

    public void removeWithTopOfStack(List<String> toRemove) {
        final HashSet<Integer> toRemoveHash = new HashSet<Integer>();

        for (String item : toRemove) {
            toRemoveHash.add(idManager.getIdForMethodName(item));
        }

        stackTraceInfo.removeIf(new Predicate<StackTraceInfo>() {
            @Override
            public boolean test(StackTraceInfo info) {
                int lastIndex = info.callStack.size() - 1;
                Integer top = info.callStack.get(lastIndex);

                return toRemoveHash.contains(top);
            }
        });
    }

    public MutableGraph generateGraph(long maxVisits) {
        GraphData graphData = new GraphData();

        for (StackTraceInfo info : stackTraceInfo) {
            addStackTraceInfo(graphData, info);
        }

        return graphData.getCompleteGraph(maxVisits, idManager);
    }

    private void addStackTraceInfo(GraphData graphData, StackTraceInfo info) {
        for (int i = 0; i < info.callStack.size(); i++) {
            if (i < info.callStack.size() - 1) {
                graphData.addVisit(info.callStack.get(i), info.callStack.get(i + 1), info.count);
            } else {
                graphData.addVisit(info.callStack.get(i), info.count);
            }
        }
    }
}
