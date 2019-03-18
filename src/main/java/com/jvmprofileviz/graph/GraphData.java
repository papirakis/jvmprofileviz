/**
 * JvmProfile - java monitoring from inside Docker containers and more!
 *
 * Copyright (C) 2019 by Emmanuel Papirakis. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.jvmprofileviz.graph;

import com.jvmprofileviz.stacktrace.IdManager;
import guru.nidi.graphviz.model.MutableGraph;

import java.util.HashMap;
import java.util.Set;

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

    public MutableGraph generateMutableGraph(Set<Integer> roots, Set<Integer> leafs, Long maxVisits, IdManager idManager) {
        MutableGraphGenerator generator = new MutableGraphGenerator(this, idManager);
        return generator.generate(roots, leafs, maxVisits);
    }

    HashMap<Integer, VertexInfo> getGraph() {
        return graph;
    }

    public static double getPercentageOfCpu(Long maxVisits, Long numVisits) {
        double result = Math.round((double)numVisits / (double)maxVisits * 10000.0);
        return result / 100;
    }
}
