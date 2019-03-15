package com.jvmprofileviz.graph;

import java.util.HashMap;

public class VertexInfo {
    private final HashMap<String, Long> edges = new HashMap<String, Long>();
    private int totalVisits;

    public void addVisit(String to) {
        this.totalVisits++;
        addEdge(to);
    }

    public void addVisit() {
        this.totalVisits++;
    }

    public HashMap<String, Long> getEdges() {
        return edges;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    private void addEdge(String to) {
        Long totalVisits = 0L;

        if (edges.containsKey(to)) {
            totalVisits = edges.get(to);
        }

        totalVisits++;
        edges.put(to, totalVisits);
    }
}
