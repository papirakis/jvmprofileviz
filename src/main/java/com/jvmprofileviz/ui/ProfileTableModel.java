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
package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.GraphData;
import com.jvmprofileviz.stacktrace.TopOfStackInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProfileTableModel extends AbstractTableModel {
    public static final int NUMBER_OF_COLUMNS = 3;

    private ArrayList<TopOfStackInfo> vertices;
    private ArrayList<Boolean> selected;
    private long total = 0;

    public void loadData(List<TopOfStackInfo> data) {
        total = 0;
        vertices = new ArrayList<TopOfStackInfo>(data);
        selected = new ArrayList<Boolean>();

        for (int i = 0; i < data.size(); i++ ) {
            selected.add(false);
            total += vertices.get(i).count;
        }

        vertices.sort(new Comparator<TopOfStackInfo>() {
            @Override
            public int compare(TopOfStackInfo o1, TopOfStackInfo o2) {
                return o2.count - o1.count;
            }
        });
    }

    @Override
    public int getRowCount() {
        return vertices.size();
    }

    @Override
    public int getColumnCount() {
        return NUMBER_OF_COLUMNS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            return selected.get(rowIndex);
        }

        TopOfStackInfo vertexInfo = vertices.get(rowIndex);

        if (columnIndex == 0) {
            return vertexInfo.methodName;
        }

        return new Double(getPercentageOfCpu((long)vertexInfo.count));
    }

    private double getPercentageOfCpu(Long numVisits) {
        return GraphData.getPercentageOfCpu(total, numVisits);
    }

    public long getTotalSamples() {
        return total;
    }

    public List<String> getSelected() {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i)) {
                result.add(vertices.get(i).methodName);
            }
        }

        return result;
    }

    public void selectAll() {
        for (int i = 0; i < selected.size(); i++) {
            selected.set(i, true);
        }
    }

    public void unselectAll() {
        for (int i = 0; i < selected.size(); i++) {
            selected.set(i, false);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            return true;
        }

        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 2) {
            selected.set(row, (Boolean)value);
        }
    }

    @Override
    public Class getColumnClass(int c) {
        if (c == 0) {
            return String.class;
        }

        if (c == 1) {
            return Double.class;
        }

        return Boolean.class;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "Method name";
        }

        if (col == 1) {
            return "Number of samples";
        }

        return "Selected";
    }
}
