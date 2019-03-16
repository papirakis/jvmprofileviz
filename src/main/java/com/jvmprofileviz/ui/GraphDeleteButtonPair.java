package com.jvmprofileviz.ui;

import javax.swing.*;

public class GraphDeleteButtonPair extends JPanel {
    private final JButton graph = new JButton("show graphs");
    private final JButton delete = new JButton("delete selected");

    public GraphDeleteButtonPair() {
        this.add(graph);
        this.add(delete);
    }
}
