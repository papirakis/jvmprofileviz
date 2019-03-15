package com.jvmprofileviz.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class Graph {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, VertexInfo> graph = new HashMap<String, VertexInfo>();

    public void addCpu(String from, String to, Long cpu) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit(to, cpu);
    }

    public void addCpu(String from, Long cpu) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit(cpu);
    }

    private VertexInfo getVertex(String key) {
        VertexInfo vertexInfo;

        if (!graph.containsKey(key)) {
            vertexInfo = new VertexInfo();
            graph.put(key, vertexInfo);
        } else {
            vertexInfo = graph.get(key);
        }

        return vertexInfo;
    }

    public String serialize() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
}
