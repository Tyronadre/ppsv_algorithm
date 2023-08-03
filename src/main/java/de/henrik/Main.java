package de.henrik;

import de.henrik.algorithm.*;
import de.henrik.data.Application;
import de.henrik.data.Topic;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
                        
            edge.standout2 {
                size: 4px;
                fill-color: rgb(240,0,0);
            }
            """;

    public static int DATASET = -1;
    public static Graph graph = new AdjacencyListGraph("Graph");
    public static Provider provider = new Provider(DATASET);

    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.setProperty("org.graphstream.ui", "swing");
        graph.setAttribute("ui.anti-alias");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.stylesheet", styleSheet);

        // SETUP CONTROL PANEL
        JFrame frame = new JFrame("Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 350);
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        createGeneralButtons(buttonPanel, c);
        createAlgorithmButtons(buttonPanel, c);


        frame.add(buttonPanel);
        frame.setVisible(true);

        provider.fillGraph();

        var viewer = graph.display();
        viewer.disableAutoLayout();
    }

    private static JButton createAlgorithmButton(String name, Algorithm algorithm) {
        JButton algo = new JButton(name);
        algo.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                System.out.println("An algorithm is already running");
                return;
            }
            new Thread(algorithm).start();
        });
        return algo;
    }

    private static void createAlgorithmButtons(JPanel buttonPanel, GridBagConstraints c) {


        //Algos
        JButton algo1 = createAlgorithmButton("RandomIterationAlgorithm", new RandomIterationAlgorithm(0L));
        JButton algo2 = createAlgorithmButton("HightestPriorityAlgorithm", new HighestPriorityAlgorithm(0L));
        JButton algo3 = createAlgorithmButton("GreedyCycleAlgorithm (WIP)", new GreedyCycleAlgorithm(0L));
        JButton algo4 = createAlgorithmButton("SingleTest (WIP)", new SingleOnly(0L));
        JButton algo5 = createAlgorithmButton("GroupTest (WIP)", new TTCGroups(0L));

        //Controls
        JButton algoPause = new JButton("Pause Algorithm");
        JButton algoStep = new JButton("Step Algorithm");
        JPanel algoSpeedPanel = new JPanel();
        JButton verbose = new JButton("Verbose");

        algoPause.addActionListener(e -> {
            if (Objects.equals(algoPause.getText(), "Pause Algorithm")) {
                algoPause.setText("Resume Algorithm");
                Algorithm.pause();
            } else {
                algoPause.setText("Pause Algorithm");
                Algorithm.resume();
            }
        });
        algoStep.addActionListener(e -> {
            Algorithm.step();
            algoPause.setText("Resume Algorithm");
        });

        algoSpeedPanel.setLayout(new BoxLayout(algoSpeedPanel, BoxLayout.X_AXIS));
        JSlider algoSpeed = new JSlider(0, 1000, 100);
        JButton algoFast = new JButton("Run Fast");
        algoSpeedPanel.add(algoSpeed);
        algoSpeed.addChangeListener(e -> {
            if (algoSpeed.getValue() == 0) {
                algoSpeed.setValue(10);
            }
            Algorithm.setSpeed(algoSpeed.getValue());
            algoSpeed.setToolTipText("Waiting Time between Steps: " + algoSpeed.getValue() + " ms");
        });
        algoSpeed.setMajorTickSpacing(250);
        algoSpeed.setMinorTickSpacing(10);
        algoSpeed.setPaintTicks(true);
        algoSpeed.setPaintLabels(true);
        algoSpeed.setSnapToTicks(true);
        algoSpeedPanel.add(algoFast);
        algoFast.addActionListener(e -> {
            if (Objects.equals(algoFast.getText(), "Run Fast")) {
                algoFast.setText("Run Slow");
                Algorithm.setSlow(false);
            } else {
                algoFast.setText("Run Fast");
                Algorithm.setSlow(true);
            }
        });

        verbose.addActionListener(e -> {
            if (verbose.getText().equals("Verbose")) {
                verbose.setText("Non-Verbose");
                Algorithm.setVerbose(true);
            } else {
                verbose.setText("Verbose");
                Algorithm.setVerbose(false);
            }
        });


        //Add to panel
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
        buttonPanel.add(algo5, c);

        c.gridy += 2;
        buttonPanel.add(algoPause, c);
        c.gridy++;
        buttonPanel.add(algoStep, c);
        c.gridy++;
        buttonPanel.add(verbose, c);
        c.gridy++;
        buttonPanel.add(algoSpeedPanel, c);
    }

    private static void createGeneralButtons(JPanel panel, GridBagConstraints constraints) {
        JButton hideCollection1 = new JButton("Hide Collection 1");
        JButton hideCollection2 = new JButton("Hide Collection 2");
        JButton hideCollection3 = new JButton("Hide Collection 3");
        JButton clear = new JButton("Clear");
        JButton checkErrors = new JButton("Check Errors");
        JButton score = new JButton("Score");
        JPanel dataSetSelection = new JPanel();


        hideCollection1.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 1) {
                    if (hideCollection1.getText().equals("Hide Collection 1"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (hideCollection1.getText().equals("Hide Collection 1")) hideCollection1.setText("Show Collection 1");
            else hideCollection1.setText("Hide Collection 1");
        });
        hideCollection2.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 2) {
                    if (hideCollection2.getText().equals("Hide Collection 2"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (hideCollection2.getText().equals("Hide Collection 2")) hideCollection2.setText("Show Collection 2");
            else hideCollection2.setText("Hide Collection 2");
        });
        hideCollection3.addActionListener(e -> {
            graph.edges().forEach(edge -> {
                if (((Application) edge.getAttribute("data")).collectionID() == 3) {
                    if (hideCollection3.getText().equals("Hide Collection 3"))
                        edge.setAttribute("ui.style", "visibility-mode: hidden;");
                    else edge.setAttribute("ui.style", "visibility-mode: normal;");
                }
            });
            if (hideCollection3.getText().equals("Hide Collection 3")) hideCollection3.setText("Show Collection 3");
            else hideCollection3.setText("Hide Collection 3");
        });
        clear.addActionListener(e -> {
            for (Topic topic : provider.courseAndTopicProvider.getTopicList()) {
                topic.clearApplications();
            }
            Util.repaintGraph();
            provider.applicationsProvider.getApplicationList().forEach(System.out::println);
        });
        checkErrors.addActionListener(e -> CheckErrors.check(provider));
        score.addActionListener(e -> Score.score(provider));

        dataSetSelection.setLayout(new BoxLayout(dataSetSelection, BoxLayout.X_AXIS));
        JSpinner dataSet = new JSpinner(new SpinnerNumberModel(DATASET, -1, 4, 1));
        dataSetSelection.add(dataSet);
        JButton loadSet = new JButton("Load Set");
        dataSetSelection.add(loadSet);
        loadSet.addActionListener(e -> {
            int set;
            try {
                set = (int) dataSet.getValue();
            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number!");
                return;
            }
            graph.clear();
            graph.setAttribute("ui.anti-alias");
            graph.setAttribute("ui.quality");
            graph.setAttribute("ui.stylesheet", styleSheet);

            provider = new Provider(set);
            provider.fillGraph();

            Util.repaintGraph();
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(hideCollection1, constraints);
        constraints.gridy++;
        panel.add(hideCollection2, constraints);
        constraints.gridy++;
        panel.add(hideCollection3, constraints);
        constraints.gridy++;
        panel.add(clear, constraints);
        constraints.gridy++;
        panel.add(checkErrors, constraints);
        constraints.gridy++;
        panel.add(score, constraints);
        constraints.gridy += 4;
        panel.add(dataSetSelection, constraints);
    }
}