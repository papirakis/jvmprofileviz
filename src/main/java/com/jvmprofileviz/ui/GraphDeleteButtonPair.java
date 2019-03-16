package com.jvmprofileviz.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class GraphDeleteButtonPair extends JPanel {
    private final JButton graph = new JButton("show graphs");
    private final JButton delete = new JButton("delete selected");

    public GraphDeleteButtonPair() {
        this.add(graph);
        this.add(delete);
    }

    void addShowGraphActionListener(ActionListener listener) {
        graph.addActionListener(listener);
    }

    void addDeleteSelectedActionListener(ActionListener listener) {
        delete.addActionListener(listener);
    }
}
