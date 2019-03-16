package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.VertexInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileTableModel extends AbstractTableModel {
    public static final int NUMBER_OF_COLUMNS = 3;

    private ArrayList<VertexInfo> vertices;
    private ArrayList<Boolean> selected;
    private int total = 0;

    public void loadData(VertexInfo[] data) {
        total = 0;
        vertices = new ArrayList<VertexInfo>();
        vertices.addAll(Arrays.asList(data));

        selected = new ArrayList<Boolean>();

        for (int i = 0; i < data.length; i++ ) {
            selected.add(false);
            total += vertices.get(i).getTotalVisits();
        }
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

        VertexInfo vertexInfo = vertices.get(rowIndex);

        if (columnIndex == 0) {
            return vertexInfo.getName();
        }

        return new Double(getPercentageOfCpu(vertexInfo.getTotalVisits()));
    }

    public double getPercentageOfCpu(int numVisits) {
        double result = Math.round((double)numVisits / (double)total * 10000.0);
        return result / 100;
    }

    public List<String> getSelected() {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i)) {
                result.add(vertices.get(i).getName());
            }
        }

        return result;
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
