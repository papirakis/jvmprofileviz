package com.jvmprofileviz.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.HashMap;

public class GraphData {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, VertexInfo> graph = new HashMap<String, VertexInfo>();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public void addVisit(String from, String to) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit(to);
    }

    public void addVisit(String from) {
        VertexInfo fromVertexInfo = getVertex(from);
        fromVertexInfo.addVisit();
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
        return mapper.writeValueAsString(graph);
    }
}
