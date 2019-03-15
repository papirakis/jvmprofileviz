package com.jvmprofileviz.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class Graph {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, Vertex> graph = new HashMap<String, Vertex>();

    public void visitLink(String from, String to) {
        Vertex fromVertex = getVertex(from);
        fromVertex.addVisit();

        Vertex toVertex = getVertex(to);
        toVertex.addVisit();
    }

    private Vertex getVertex(String key) {
        Vertex vertex;

        if (!graph.containsKey(key)) {
            vertex = new Vertex();
            graph.put(key, vertex);
        } else {
            vertex = graph.get(key);
        }

        return vertex;
    }

    public String serialize() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
}
