package com.jvmprofileviz.graph;

import java.util.HashMap;

public class VertexInfo {
    private final HashMap<String, Long> edges = new HashMap<String, Long>();
    private String name;
    private int totalVisits;

    public VertexInfo() { }

    public VertexInfo(String name) {
        this.name = name;
    }

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

    public String getName() { return name; }

    private void addEdge(String to) {
        Long totalVisits = 0L;

        if (edges.containsKey(to)) {
            totalVisits = edges.get(to);
        }

        totalVisits++;
        edges.put(to, totalVisits);
    }
}
