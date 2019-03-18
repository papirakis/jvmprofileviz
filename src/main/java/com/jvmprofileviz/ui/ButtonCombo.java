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
