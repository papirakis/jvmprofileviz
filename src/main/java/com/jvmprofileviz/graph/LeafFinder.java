package com.jvmprofileviz.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class LeafFinder {
    private final HashMap<String, VertexInfo> data;

    public LeafFinder(GraphData graph) {
        data = graph.getGraph();
    }

    public VertexInfo[] run() {
        ArrayList<VertexInfo> vertices = new ArrayList<VertexInfo>();

        for (String key : data.keySet()) {
            VertexInfo vertex = data.get(key);
            if (vertex.getEdges().size() == 0) {
                vertices.add(vertex);
            }
        }

        VertexInfo[] result = new VertexInfo[vertices.size()];
        vertices.toArray(result);
        Arrays.sort(result, new StackRank());
        return result;
    }

    private class StackRank implements Comparator<VertexInfo> {
        @Override
        public int compare(VertexInfo o1, VertexInfo o2) {
            return o2.getTotalVisits() - o1.getTotalVisits();
        }
    }
}
