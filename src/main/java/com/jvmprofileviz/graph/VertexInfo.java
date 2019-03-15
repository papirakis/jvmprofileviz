package com.jvmprofileviz.graph;

import java.util.HashMap;

public class VertexInfo {
    private final HashMap<String, Long> edges = new HashMap<String, Long>();
    private int totalCpu;

    public void addCpu(String to, Long cpu) {
        this.totalCpu += cpu;
        addEdge(to, cpu);
    }

    public void addCpu(Long cpu) {
        this.totalCpu += cpu;
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
