package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.VertexInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileTableModel extends AbstractTableModel {
    public static final int NUMBER_OF_COLUMNS = 3;

    private ArrayList<VertexInfo> vertices;
    private ArrayList<Boolean> selected = new ArrayList<Boolean>();

    public void loadData(VertexInfo[] data) {
        vertices = new ArrayList<VertexInfo>();
        vertices.addAll(Arrays.asList(data));

        for (int i = 0; i < data.length; i++ ) {
            selected.add(false);
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

        return vertexInfo.getTotalVisits();
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
            return Integer.class;
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
