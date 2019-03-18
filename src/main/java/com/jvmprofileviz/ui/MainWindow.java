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
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JFrame {
    private final ButtonCombo buttons = new ButtonCombo();
    private Orchestrator orchestrator;

    public MainWindow() {
        setSize(1280, 768);
        centeredFrame();

        this.setLayout(new BorderLayout());
        this.getContentPane().add(buttons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void loadFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            orchestrator = new Orchestrator(this,  selectedFile);

            JScrollPane scrollPane = new JScrollPane(orchestrator.getTable());
            this.getContentPane().add(scrollPane, BorderLayout.CENTER);

            buttons.addShowGraphActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orchestrator.showGraphClicked();
                }
            });

            buttons.addDeleteSelectedActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orchestrator.deleteClicked();
                }
            });

            buttons.addMultiGraphActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orchestrator.multiGraphClicked();
                }
            });

            buttons.addSelectAllActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orchestrator.selectAllClicked();
                }
            });

            buttons.addUnselectAllActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    orchestrator.unselectAllClicked();
                }
            });
        }
    }

    private void centeredFrame(){
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - this.getWidth()) / 2;
        int iCoordY = (objDimension.height - this.getHeight()) / 2;
        this.setLocation(iCoordX, iCoordY);
    }
}
