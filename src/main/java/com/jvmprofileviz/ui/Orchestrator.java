package com.jvmprofileviz.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jvmprofileviz.stacktrace.StackTraceHolder;
import com.jvmprofileviz.stacktrace.StackTraceStats;
import com.jvmprofileviz.stacktrace.TopOfStackInfo;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

class Orchestrator {
    private final JTable table;
    private final ProfileTableModel tableModel = new ProfileTableModel();
    private final JFrame mainWindow;
    private final StackTraceHolder stackTraceHolder;

    Orchestrator(JFrame mainWindow, File selectedFile) throws IOException {
        StackTraceStats stats = loadFromFile(selectedFile.getAbsolutePath());
        stackTraceHolder = new StackTraceHolder(stats);
        loadTableModel();
        table = new JTable(tableModel);
        this.mainWindow = mainWindow;
    }

    public JTable getTable() {
        return table;
    }

    public void showGraphClicked() {
        MutableGraph g = stackTraceHolder.generateSubsetGraph(tableModel.getSelected(), tableModel.getTotalSamples());

        processMutableGraph(g, "multigraph");
    }

    public void deleteClicked() {
        List<String> selected = tableModel.getSelected();

        stackTraceHolder.removeWithTopOfStack(selected);
        loadTableModel();
        tableModel.fireTableDataChanged();
    }

    public void multiGraphClicked() {
        MutableGraph g = stackTraceHolder.generateGraph(tableModel.getTotalSamples());

        processMutableGraph(g, "multigraph");
    }

    private void processMutableGraph(MutableGraph g, String filePrefix) {
        try {
            File temp = File.createTempFile(filePrefix, ".svg");
            System.out.println(temp.getAbsolutePath());
            Graphviz.fromGraph(g).width(900).render(Format.SVG).toFile(temp);

            StringSelection selection = new StringSelection(temp.getAbsolutePath());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            JOptionPane.showMessageDialog(
                    mainWindow,
                    "Result is in:" + temp.getAbsolutePath() + " and the path has been copied to clipboard",
                    "Just Ctrl+v in Chrome address bar",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.err.println("Could not write svg to file.");
            e.printStackTrace(System.err);
        }
    }

    private void loadTableModel() {
        List<TopOfStackInfo> info = stackTraceHolder.getTopOfStacks();
        tableModel.loadData(info);
    }

    private static StackTraceStats loadFromFile(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        FileReader reader = null;
        BufferedReader buffered = null;

        try {
            StringBuilder builder = new StringBuilder();
            reader = new FileReader(new File(path));
            buffered = new BufferedReader(reader);

            String line;

            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }

            String content = builder.toString();
            return mapper.readValue(content, StackTraceStats.class);
        } finally {
            if (buffered != null) {
                buffered.close();
            }
        }
    }
}
