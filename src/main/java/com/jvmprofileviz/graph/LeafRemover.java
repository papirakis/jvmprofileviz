package com.jvmprofileviz.graph;

import java.util.*;

class LeafRemover {
    private HashSet<String> marked = new HashSet<String>();
    private final HashMap<String, VertexInfo> graph;

    public LeafRemover(GraphData graphData) {
        graph = graphData.getGraph();
    }

    public void remove(List<String> roots, List<String> toRemove) {
        HashSet<String> toRemoveHash = new HashSet<String>(toRemove);
        for (String root : roots) {
            doRemove(toRemoveHash, root);
        }

        List<String> zeroVisits = new ArrayList<String>();

        for (String key : graph.keySet()) {
            if (graph.get(key).getTotalVisits() <= 0) {
                zeroVisits.add(key);
            }
        }

        for (String key : zeroVisits) {
            graph.remove(key);
        }
    }

    private boolean doRemove(Set<String> toRemove, String current) {
        if (marked.contains(current)) {
            return false;
        }

        marked.add(current);

        VertexInfo vertex = graph.get(current);

        if (toRemove.contains(vertex.getName())) {
            vertex.reduceVisits(vertex.getTotalVisits());
            return true;
        }

        boolean result = false;
        for (String key : vertex.getEdges().keySet()) {
            boolean thisOne = doRemove(toRemove, key);

            if (thisOne) {
                vertex.reduceVisits(vertex.getEdges().get(key));
                result = true;
            }
        }

        return result;
    }
}
