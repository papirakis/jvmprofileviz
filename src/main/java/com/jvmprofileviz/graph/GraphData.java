package com.jvmprofileviz.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Size;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.*;
import java.util.HashMap;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphData {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, VertexInfo> graph;

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public GraphData() {
        graph = new HashMap<String, VertexInfo>();
    }

    public GraphData(String filePath) throws IOException {
        this.graph = loadFromFile(filePath);
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
            vertexInfo = new VertexInfo(key);
            graph.put(key, vertexInfo);
        } else {
            vertexInfo = graph.get(key);
        }

        return vertexInfo;
    }

    private String serialize() throws JsonProcessingException {
        return mapper.writeValueAsString(graph);
    }

    public void writeToFile(String fileName) throws IOException {
        FileWriter writer = null;

        try {
            writer = new FileWriter(new File(fileName));
            writer.write(this.serialize());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static HashMap<String, VertexInfo> loadFromFile(String path) throws IOException {
        FileReader reader = null;
        BufferedReader buffered = null;

        try {
            StringBuilder builder = new StringBuilder();
            reader = new FileReader(new File(path));
            buffered = new BufferedReader(reader);

            String line;

            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }

            String content = builder.toString();

            TypeReference<HashMap<String,VertexInfo>> typeRef
                    = new TypeReference<HashMap<String,VertexInfo>>() {};
            return (HashMap<String, VertexInfo>) mapper.readValue(content, typeRef);
        } finally {
            if (buffered != null) {
                buffered.close();
            }
        }
    }

    public void writeSvgGraphFile(String path) throws IOException {
        MutableGraph g = mutGraph("profile").setDirected(true);
        double maxVisits = getMaxVisits();

        for (String key : graph.keySet()) {
            VertexInfo vertex =  graph.get(key);

            // Skip if not meaningful.
            if (vertex.getTotalVisits() < maxVisits / 2) {
                continue;
            }

            MutableNode node = mutNode(key);
            double numVisits = vertex.getTotalVisits();
            double size = numVisits / maxVisits;
            node.add(Size.std().size(size, size));

            for (String edge : vertex.getEdges().keySet()) {
                Double visits = new Double(vertex.getEdges().get(edge));

                visits /= maxVisits;
                Link link = mutNode(edge).linkTo().add(Label.of(visits.toString()));
                node.addLink(link);
            }

            g.add(node);
        }

        Graphviz.fromGraph(g).width(900).render(Format.SVG).toFile(new File(path));
    }

    private double getMaxVisits() {
        int max = 0;
        for (String key : graph.keySet()) {
            VertexInfo vertex = graph.get(key);

            if (max < vertex.getTotalVisits()) {
                max = vertex.getTotalVisits();
            }
        }

        return max;
    }

    HashMap<String, VertexInfo> getGraph() {
        return graph;
    }
}
