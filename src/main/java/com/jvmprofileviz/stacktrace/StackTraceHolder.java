package com.jvmprofileviz.stacktrace;

import com.jvmprofileviz.graph.GraphData;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.util.*;
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

    private Map<Integer, Integer> getTopOfStacksMap() {
        HashMap<Integer, Integer> topOfStacks = new HashMap<Integer, Integer>();

        for (StackTraceInfo info : stackTraceInfo) {
            Integer top = getTopOfStack(info);

            if (topOfStacks.containsKey(top)) {
                Integer current = topOfStacks.get(top);
                current += info.count;
                topOfStacks.put(top, current);
            } else {
                topOfStacks.put(top, info.count);
            }
        }

        return topOfStacks;
    }

    public List<TopOfStackInfo> getTopOfStacks() {
        Map<Integer, Integer> topOfStacks = getTopOfStacksMap();
        List<TopOfStackInfo> result = new ArrayList<TopOfStackInfo>();

        for (Integer top : topOfStacks.keySet()) {
            TopOfStackInfo info = new TopOfStackInfo();
            info.methodName = idManager.getMethodNameById(top);
            info.count = topOfStacks.get(top);
            result.add(info);
        }

        return result;
    }

    private Integer getTopOfStack(StackTraceInfo info) {
        int lastIndex = info.callStack.size() - 1;
        return info.callStack.get(lastIndex);
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
        return generateGraph(stackTraceInfo, maxVisits);
    }

    public MutableGraph generateSubsetGraph(List<String> withTopOfStacks, long maxVisits) {
        List<StackTraceInfo> stackTraces = new ArrayList<StackTraceInfo>(stackTraceInfo);
        final HashSet<Integer> topOfStacks = new HashSet<Integer>();

        for (String methodName : withTopOfStacks) {
            topOfStacks.add(idManager.getIdForMethodName(methodName));
        }

        stackTraces.removeIf(new Predicate<StackTraceInfo>() {
            @Override
            public boolean test(StackTraceInfo stackTraceInfo) {
                Integer top = getTopOfStack(stackTraceInfo);
                return !topOfStacks.contains(top);
            }
        });

        return generateGraph(stackTraces, maxVisits);
    }

    private MutableGraph generateGraph(List<StackTraceInfo> stackTraces, long maxVisits) {
        GraphData graphData = new GraphData();

        for (StackTraceInfo info : stackTraces) {
            addStackTraceInfo(graphData, info);
        }

        Set<Integer> roots = getRoots();
        Map<Integer, Integer> leafsMap = getTopOfStacksMap();
        Set<Integer> leafs = new HashSet<Integer>(leafsMap.keySet());
        return graphData.generateMutableGraph(roots, leafs, maxVisits, idManager);
    }

    private Set<Integer> getRoots() {
        HashSet<Integer> roots = new HashSet<Integer>();

        for (StackTraceInfo info : stackTraceInfo) {
            if (info.callStack.size() >= 1) {
                roots.add(info.callStack.get(0));
            }
        }

        return roots;
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
