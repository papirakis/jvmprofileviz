package com.jvmprofileviz.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ButtonCombo extends JPanel {
    private final JButton graph = new JButton("show selected graphs");
    private final JButton delete = new JButton("delete selected");
    private final JButton multiGraph = new JButton("display entire graph");
    private final JButton selectAll = new JButton("select all");
    private final JButton unselectAll = new JButton("unselect all");

    public ButtonCombo() {
        this.add(graph);
        this.add(delete);
        this.add(multiGraph);
        this.add(selectAll);
        this.add(unselectAll);
    }

    public void addShowGraphActionListener(ActionListener listener) {
        graph.addActionListener(listener);
    }

    public void addDeleteSelectedActionListener(ActionListener listener) {
        delete.addActionListener(listener);
    }

    public void addMultiGraphActionListener(ActionListener listener) {
        multiGraph.addActionListener(listener);
    }

    public void addSelectAllActionListener(ActionListener listener) {
        selectAll.addActionListener(listener);
    }

    public void addUnselectAllActionListener(ActionListener listener) {
        unselectAll.addActionListener(listener);
    }
}
