package com.jvmprofileviz.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class RootFinder {
    private final HashMap<String, VertexInfo> graph;
    private final HashSet<String> notReferencedYet = new HashSet<String>();

    public RootFinder(GraphData graphData) {
        graph = graphData.getGraph();

        for (String key : graphData.getGraph().keySet()) {
            notReferencedYet.add(key);
        }
    }

    public List<String> findRoots() {
        for (String key : graph.keySet()) {
            for (String vetexName : graph.get(key).getEdges().keySet()) {
                if (notReferencedYet.contains(key)) {
                    notReferencedYet.remove(vetexName);
                }
            }
        }

        return new ArrayList<String>(notReferencedYet);
    }
}
