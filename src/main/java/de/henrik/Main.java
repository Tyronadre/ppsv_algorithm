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
            node {
                fill-color: rgb(0, 255, 100);
            }
            node.topic {
                text-offset: -20px, 0px;
                text-alignment: at-left;
            }

            node.group {
                text-offset: 20px, 0px;
                text-alignment: at-right;
            }
            node.standout {
                fill-color: #FF0000;
            }
            edge {
                size: 1px;
            }
            edge.accepted {
                fill-color: rgb(240,240,0);
            }
                        
            edge.standout {
                size: 4px;
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
//        var viewer = graph.display();
//        viewer.disableAutoLayout();

        // --- GENERAL BUTTONS --- //
        JButton jButton = new JButton("Hide Collection 1");
        JButton jButton1 = new JButton("Hide Collection 2");
        jButton.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 1) {
                    if (jButton.getText().equals("Hide Collection 1"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton.getText().equals("Hide Collection 1")) jButton.setText("Show Collection 1");
            else jButton.setText("Hide Collection 1");
        });
        jButton1.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 2) {
                    if (jButton1.getText().equals("Hide Collection 2"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton1.getText().equals("Hide Collection 2")) jButton1.setText("Show Collection 2");
            else jButton1.setText("Hide Collection 2");
        });
        JButton jButton2 = new JButton("Hide Collection 3");
        jButton2.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 3) {
                    if (jButton2.getText().equals("Hide Collection 3"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (jButton2.getText().equals("Hide Collection 3")) jButton2.setText("Show Collection 3");
            else jButton2.setText("Hide Collection 3");
        });

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
                topic.clearApplications();
            }
            Util.repaintGraph(graph);
            provider.applicationsProvider.getApplicationList().forEach(System.out::println);
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

        JButton algoSlow = new JButton("Fast Algorithm");
        algoSlow.addActionListener(e -> {
            if (algoSlow.getText().equals("Slow Algorithm")) {
                algoSlow.setText("Fast Algorithm");
                if (algorithm[0] != null) algorithm[0].setSlow(false);
            } else {
                algoSlow.setText("Slow Algorithm");
                if (algorithm[0] != null) algorithm[0].setSlow(true);
            }
        });

        JButton verbose = new JButton("Verbose");
        verbose.addActionListener(e -> {
            if (verbose.getText().equals("Verbose")) {
                verbose.setText("Not Verbose");
                if (algorithm[0] != null) algorithm[0].setVerbose(true);
            } else {
                verbose.setText("Verbose");
                if (algorithm[0] != null) algorithm[0].setVerbose(false);
            }
        });

        JButton algo1 = new JButton("Start RandomIterationAlgorithm");
        algo1.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                return;
            }
            var pause = algoPause.getText().equals("Resume Algorithm");
            algorithm[0] = new RandomIterationAlgorithm(0L, provider, graph);
            if (pause) algorithm[0].pause();
            algorithm[0].setSlow(!algoSlow.getText().equals("Slow Algorithm"));
            algorithm[0].setVerbose(!verbose.getText().equals("Verbose"));

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
            algorithm[0].setSlow(!algoSlow.getText().equals("Slow Algorithm"));
            algorithm[0].setVerbose(!verbose.getText().equals("Verbose"));

            thread[0] = new Thread(algorithm[0]);
            thread[0].start();
        });

        JButton algo3 = new JButton("Start GreedyCycleAlgorithm (WIP)");
        algo3.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                return;
            }
            var pause = algoPause.getText().equals("Resume Algorithm");
            algorithm[0] = new GreedyCycleAlgorithm(0L, provider, graph);
            if (pause) algorithm[0].pause();
            algorithm[0].setSlow(!algoSlow.getText().equals("Slow Algorithm"));
            algorithm[0].setVerbose(!verbose.getText().equals("Verbose"));

            thread[0] = new Thread(algorithm[0]);
            thread[0].start();
        });

        JButton algo4 = new JButton("Start Start HyllandZeckenhause (SINGLE ONLY)");
        algo4.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                return;
            }
            var pause = algoPause.getText().equals("Resume Algorithm");
            algorithm[0] = new SingleOnly(0L, provider, graph);
            if (pause) algorithm[0].pause();
            algorithm[0].setSlow(!algoSlow.getText().equals("Slow Algorithm"));
            algorithm[0].setVerbose(!verbose.getText().equals("Verbose"));

            thread[0] = new Thread(algorithm[0]);
            thread[0].start();
        });


        JFrame frame = new JFrame("Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 200);
        JPanel buttonPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        buttonPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        buttonPanel.add(jButton, c);
        c.gridy++;
        buttonPanel.add(jButton1, c);
        c.gridy++;
        buttonPanel.add(jButton2, c);
        c.gridy++;
        buttonPanel.add(clear, c);
        c.gridy++;
        buttonPanel.add(checkErrors, c);
        c.gridy++;
        buttonPanel.add(score, c);

        c.gridx = 1;
        c.gridy = 0;
        buttonPanel.add(algo1, c);
        c.gridy++;
        buttonPanel.add(algo2, c);
        c.gridy++;
        buttonPanel.add(algo3, c);
        c.gridy++;
        buttonPanel.add(algo4, c);
        c.gridy++;
        buttonPanel.add(algoStep, c);
        c.gridy++;
        buttonPanel.add(algoPause, c);
        c.gridy++;
        buttonPanel.add(algoSlow, c);
        c.gridy++;
        buttonPanel.add(verbose, c);


        frame.add(buttonPanel);
        frame.setVisible(true);
    }
}