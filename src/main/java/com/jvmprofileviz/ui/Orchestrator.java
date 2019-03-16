package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.GraphData;
import com.jvmprofileviz.graph.VertexInfo;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

class Orchestrator {
    private final JTable table;
    private final ProfileTableModel tableModel = new ProfileTableModel();
    private final GraphData graph;

    Orchestrator(File selectedFile) throws IOException {
        graph = new GraphData(selectedFile.getAbsolutePath());
        VertexInfo[] leafs = graph.findLeafs();
        tableModel.loadData(leafs);
        table = new JTable(tableModel);
    }

    public ProfileTableModel getProfileTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public void showGraphClicked() {
        System.out.println("showGraphClicked");
    }

    public void deleteClicked() {
        System.out.println("deleteClicked");
    }
}
