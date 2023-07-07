package de.henrik;

import de.henrik.algorithm.*;
import de.henrik.data.Application;
import de.henrik.data.Topic;
import de.henrik.generator.Provider;
import org.graphstream.graph.implementations.AdjacencyListGraph;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {
    public static final String styleSheet = """
            node.topic {
                text-offset: -20px, 0px;
                text-alignment: at-left;
            }

            node.group {
                text-offset: 20px, 0px;
                text-alignment: at-right;
            }
            """;

    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.setProperty("org.graphstream.ui", "swing");


        var provider = new Provider();
        var graph = new AdjacencyListGraph("Tutorial 1");
        graph.setAttribute("ui.anti-alias");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.stylesheet", styleSheet);


        provider.fillGraph(graph);
        var viewer = graph.display();
        viewer.disableAutoLayout();

        // --- GENERAL BUTTONS --- //
        JButton jButton = new JButton("Hide Collection 1");
        JButton jButton1 = new JButton("Hide Collection 2");
        jButton.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 1) {
                    if (jButton.getText().equals("Hide Collection 1"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else
                        edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton.getText().equals("Hide Collection 1"))
                jButton.setText("Show Collection 1");
            else
                jButton.setText("Hide Collection 1");
        });
        jButton1.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 2) {
                    if (jButton1.getText().equals("Hide Collection 2"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else
                        edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton1.getText().equals("Hide Collection 2"))
                jButton1.setText("Show Collection 2");
            else
                jButton1.setText("Hide Collection 2");
        });
        JButton jButton2 = new JButton("Hide Collection 3");
        jButton2.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 3) {
                    if (jButton2.getText().equals("Hide Collection 3"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else
                        edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton2.getText().equals("Hide Collection 3"))
                jButton2.setText("Show Collection 3");
            else
                jButton2.setText("Hide Collection 3");
        });

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
                topic.clearApplications();
            }
            Util.repaintGraph(graph, provider);
        });

        JButton checkErrors = new JButton("Check Errors");
        checkErrors.addActionListener(e -> CheckErrors.check(provider));

        JButton score = new JButton("Score");
        score.addActionListener(e -> Score.score(provider));

        // --- ALGORITHMS --- //


        final Algorithm[] algorithm = new Algorithm[1];
        final Thread[] thread = new Thread[1];

        JButton algoPause = new JButton("Pause Algorithm");
        algoPause.addActionListener(e -> {
            if (Objects.equals(algoPause.getText(), "Pause Algorithm")) {
                algoPause.setText("Resume Algorithm");
                if (algorithm[0] == null) return;
                algorithm[0].pause();
            } else {
                algoPause.setText("Pause Algorithm");
                if (algorithm[0] == null) return;
                algorithm[0].resume();
            }
        });
        JButton algoStep = new JButton("Step Algorithm");
        algoStep.addActionListener(e -> {
            if (algorithm[0] == null) return;
            algorithm[0].oneStep();
            algoPause.setText("Resume Algorithm");
        });

        JButton algo1 = new JButton("Start RandomIterationAlgorithm");
        algo1.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                return;
            }
            var pause = algoPause.getText().equals("Resume Algorithm");
            algorithm[0] = new RandomIterationAlgorithm(0L, provider, graph);
            if (pause) algorithm[0].pause();
            thread[0] = new Thread(algorithm[0]);
            thread[0].start();
        });

        JButton algo2 = new JButton("Start HightestPriorityAlgorithm");
        algo2.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                return;
            }
            var pause = algoPause.getText().equals("Resume Algorithm");
            algorithm[0] = new HighestPriorityAlgorithm(0L, provider, graph);
            if (pause) algorithm[0].pause();

            thread[0] = new Thread(algorithm[0]);
            thread[0].start();
        });


        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 200);
        JPanel buttonPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(jButton);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton2);
        buttonPanel.add(clear);
        buttonPanel.add(checkErrors);
        buttonPanel.add(score);

        c.gridy = 1;
        c.gridx = 0;
        buttonPanel.add(algo1, c);
        c.gridx++;
        buttonPanel.add(algo2, c);
        c.gridx++;
        buttonPanel.add(algoStep, c);
        c.gridx++;
        buttonPanel.add(algoPause, c);


        frame.add(buttonPanel);
        frame.setVisible(true);
    }
}