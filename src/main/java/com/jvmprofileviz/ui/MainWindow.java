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
    private final GraphDeleteButtonPair buttons = new GraphDeleteButtonPair();
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
            orchestrator = new Orchestrator(selectedFile);

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
        }
    }

    private void centeredFrame(){
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - this.getWidth()) / 2;
        int iCoordY = (objDimension.height - this.getHeight()) / 2;
        this.setLocation(iCoordX, iCoordY);
    }
}
