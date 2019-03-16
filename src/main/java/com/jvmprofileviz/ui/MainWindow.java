package com.jvmprofileviz.ui;

import com.jvmprofileviz.graph.GraphData;
import com.jvmprofileviz.graph.LeafFinder;
import com.jvmprofileviz.graph.VertexInfo;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JFrame {
    private final GraphDeleteButtonPair buttons = new GraphDeleteButtonPair();
    private GraphData graph;
    private ProfileTableModel profileTableModel = new ProfileTableModel();
    private JTable table;

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

    public void loadFile() throws IOException, InterruptedException {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        Thread.sleep(100);
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            graph = new GraphData(selectedFile.getAbsolutePath());
            LeafFinder leafFinder = new LeafFinder(graph);
            VertexInfo[] leafs = leafFinder.run();
            profileTableModel.loadData(leafs);
            table = new JTable(profileTableModel);

            JScrollPane scrollPane = new JScrollPane(table);
            this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        }
    }

    private void centeredFrame(){
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - this.getWidth()) / 2;
        int iCoordY = (objDimension.height - this.getHeight()) / 2;
        this.setLocation(iCoordX, iCoordY);
    }
}
