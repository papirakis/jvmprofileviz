package com.jvmprofileviz.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class GraphDeleteButtonCombo extends JPanel {
    private final JButton graph = new JButton("show selected graphs");
    private final JButton delete = new JButton("delete selected");
    private final JButton multiGraph = new JButton("display entire graph");

    public GraphDeleteButtonCombo() {
        this.add(graph);
        this.add(delete);
        this.add(multiGraph);
    }

    void addShowGraphActionListener(ActionListener listener) {
        graph.addActionListener(listener);
    }

    void addDeleteSelectedActionListener(ActionListener listener) {
        delete.addActionListener(listener);
    }

    void addMultiGraphActionListener(ActionListener listener) {
        multiGraph.addActionListener(listener);
    }
}
