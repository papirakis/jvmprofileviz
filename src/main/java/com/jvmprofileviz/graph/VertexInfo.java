package com.jvmprofileviz.graph;

import java.util.HashMap;

public class VertexInfo {
    private final HashMap<Integer, Long> edges = new HashMap<Integer, Long>();
    private Integer id;
    private long totalVisits;

    public VertexInfo() { }

    public VertexInfo(Integer id) {
        this.id = id;
    }

    public void addVisit(Integer to, long times) {
        this.totalVisits += times;
        addEdge(to, times);
    }

    public void addVisit(long times) {
        this.totalVisits += times;
    }

    public HashMap<Integer, Long> getEdges() {
        return edges;
    }

    public Long getTotalVisits() {
        return totalVisits;
    }

    public void reduceVisits(Long numVisits) {
        this.totalVisits -= numVisits;
    }

    public void clearVisits() {
        this.totalVisits = 0L;
    }

    void removeEdge(String edgeName) {
        if (edges.containsKey(edgeName)) {
            reduceVisits(edges.get(edgeName));
            edges.remove(edgeName);
        }
    }

    private void addEdge(Integer to, long times) {
        Long totalVisits = 0L;

        if (edges.containsKey(to)) {
            totalVisits = edges.get(to);
        }

        totalVisits += times;
        edges.put(to, totalVisits);
    }
}
