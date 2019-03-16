package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.GraphData;
import com.jvmprofileviz.graph.VertexInfo;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

class Orchestrator {
    private final JTable table;
    private final ProfileTableModel tableModel = new ProfileTableModel();
    private final GraphData graph;

    Orchestrator(File selectedFile) throws IOException {
        graph = new GraphData(selectedFile.getAbsolutePath());
        loadTableModel();
        table = new JTable(tableModel);
    }

    public JTable getTable() {
        return table;
    }

    public void showGraphClicked() {
        System.out.println("showGraphClicked");
    }

    public void deleteClicked() {
        List<String> selected = tableModel.getSelected();

        graph.removeLeafs(selected);
        loadTableModel();
        tableModel.fireTableDataChanged();
        System.out.println("deleteClicked");
    }

    private void loadTableModel() {
        VertexInfo[] leafs = graph.findLeafs();
        tableModel.loadData(leafs);
    }
}
