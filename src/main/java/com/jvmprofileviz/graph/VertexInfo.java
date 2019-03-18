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
