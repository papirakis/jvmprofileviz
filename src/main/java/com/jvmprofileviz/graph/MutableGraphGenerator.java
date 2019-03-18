package com.jvmprofileviz.graph;

import com.jvmprofileviz.stacktrace.IdManager;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.util.HashMap;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

class MutableGraphGenerator {
    private final HashMap<Integer, VertexInfo> graphData;
    private final IdManager manager;

    public MutableGraphGenerator(GraphData graphData, IdManager manager) {
        this.graphData = graphData.getGraph();
        this.manager = manager;
    }

    public MutableGraph generate(Set<Integer> roots, Set<Integer> leafs, Long maxVisits) {
        MutableGraph g = mutGraph("profile").setDirected(true);

        for (Integer key : graphData.keySet()) {
            VertexInfo vertex =  graphData.get(key);
            MutableNode node = mutNode(manager.getMethodNameById(key));

            if (leafs.contains(key)) {
                node.add(Color.RED, Style.lineWidth(4));
            } else if (roots.contains(key)) {
                node.add(Color.BLUE, Style.lineWidth(4));
            }

            for (Integer edge : vertex.getEdges().keySet()) {
                if (!graphData.containsKey(edge)) {
                    continue;
                }

                Double percentage = GraphData.getPercentageOfCpu(maxVisits, vertex.getEdges().get(edge));

                Link link = mutNode(manager.getMethodNameById(edge)).linkTo().add(Label.of(percentage.toString()));
                node.addLink(link);
            }

            g.add(node);
        }

        return g;
    }
}
