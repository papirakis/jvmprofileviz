package com.jvmprofileviz.graph;

import java.util.HashSet;

public class Vertex {
    private final HashSet<String> edges = new HashSet<String>();
    private int numVisits;

    public void addVisit() {
        this.numVisits++;
    }

    public void addEdge(String to) {
        this.edges.add(to);
    }
}
