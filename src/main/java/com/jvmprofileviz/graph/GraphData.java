package com.jvmprofileviz.graph;

import com.jvmprofileviz.stacktrace.IdManager;
import guru.nidi.graphviz.model.MutableGraph;

import java.util.HashMap;

public class GraphData {
    private final HashMap<Integer, VertexInfo> graph;

    public GraphData() {
        graph = new HashMap<Integer, VertexInfo>();
    }

    public void addVisit(Integer from, Integer to, long times) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit(to, times);
    }

    public void addVisit(Integer from, long times) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit(times);
    }

    private VertexInfo getVertex(Integer id) {
        VertexInfo vertexInfo;

        if (!graph.containsKey(id)) {
            vertexInfo = new VertexInfo(id);
            graph.put(id, vertexInfo);
        } else {
            vertexInfo = graph.get(id);
        }

        return vertexInfo;
    }

    public MutableGraph getCompleteGraph(Long maxVisits, IdManager idManager) {
        CompleteGraphGenerator generator = new CompleteGraphGenerator(this, idManager);
        return generator.generate(maxVisits);
    }

    HashMap<Integer, VertexInfo> getGraph() {
        return graph;
    }

    public static double getPercentageOfCpu(Long maxVisits, Long numVisits) {
        double result = Math.round((double)numVisits / (double)maxVisits * 10000.0);
        return result / 100;
    }
}
