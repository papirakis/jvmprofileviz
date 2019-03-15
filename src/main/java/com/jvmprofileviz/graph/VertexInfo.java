package com.jvmprofileviz.graph;

import java.util.HashMap;

public class VertexInfo {
    private final HashMap<String, Long> edges = new HashMap<String, Long>();
    private int totalVisits;

    public void addVisit(String to, Long cpu) {
        this.totalVisits++;
        addEdge(to, cpu);
    }

    public void addVisit(Long cpu) {
        this.totalVisits++;
    }

    private void addEdge(String to, Long cpu) {
        Long totalCpu = 0L;

        if (edges.containsKey(to)) {
            totalCpu = edges.get(to);
        }

        totalCpu += cpu;
        edges.put(to, totalCpu);
    }
}
