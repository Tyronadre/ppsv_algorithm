package de.henrik;

import de.henrik.algorithm.*;
import de.henrik.data.Application;
import de.henrik.generator.Provider;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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
    private static final String PREFS_NODE_CONTROL = "de.henrik.control";
    private static final String PREFS_NODE_GRAPH = "de.henrik.graph";
    private static final String PREF_X = "x";
    private static final String PREF_Y = "y";
    private static final String PREF_WIDTH = "width";
    private static final String PREF_HEIGHT = "height";

    public static int initialDataset = 5;
    public static Graph graph = new AdjacencyListGraph("Graph");
    public static Provider provider = new Provider(initialDataset);

    //CONTROLS
    private static final JButton hideCollection2 = new JButton("Hide Collection 2");
    private static final JButton hideCollection3 = new JButton("Hide Collection 3");
    private static final JButton hideCollection1 = new JButton("Hide Collection 1");
    private static final JButton clear = new JButton("Clear");
    private static final JButton checkErrors = new JButton("Check Errors");
    private static final JButton score = new JButton("Score");
    private static final JButton cancelAlgo = new JButton("Force Stop Algorithm");
    private static final JButton showGraph = new JButton("Hide Graph");
    private static final JPanel dataSetSelection = new JPanel();
    private static final JButton algo1 = createAlgorithmButton("RandomIterationAlgorithm", new RandomIterationAlgorithm(0L));
    private static final JButton algo2 = createAlgorithmButton("HightestPriorityAlgorithm", new HighestPriorityAlgorithm(0L));
    private static final JButton algo3 = createAlgorithmButton("GreedyCycleAlgorithm (WIP)", new GreedyCycleAlgorithm(0L));
    private static final JButton algo4 = createAlgorithmButton("SingleTest (WIP)", new SingleOnly(0L));
    private static final JButton algo5 = createAlgorithmButton("GroupTest (WIP)", new TTCGroups(0L));
    private static final JButton algoPause = new JButton("Pause Algorithm");
    private static final JButton algoStep = new JButton("Step Algorithm");
    private static final JPanel algoSpeedPanel = new JPanel();
    private static final JButton verbose = new JButton("Verbose");

    private static final JFrame graphFrame = new JFrame("Graph");

    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.setProperty("org.graphstream.ui", "swing");
        graph.setAttribute("ui.anti-alias");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.stylesheet", styleSheet);

        // SETUP CONTROL PANEL
        var viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.addDefaultView(false);
        ViewPanel viewPanel = (ViewPanel) viewer.getDefaultView();
        graphFrame.add(viewPanel, BorderLayout.CENTER);


        JFrame frame = new JFrame("Control Panel");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowPositionAndSize(frame,graphFrame);
                System.exit(0);
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadWindowPositionAndSize(frame,graphFrame);
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
        graphFrame.setVisible(true);

        viewer.disableAutoLayout();
        provider.fillGraph();

    }

    private static void loadWindowPositionAndSize(JFrame frame , JFrame graphFrame) {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE_CONTROL);
        int x = prefs.getInt(PREF_X, 100);
        int y = prefs.getInt(PREF_Y, 100);
        int width = prefs.getInt(PREF_WIDTH, 400);
        int height = prefs.getInt(PREF_HEIGHT, 300);


        frame.setLocation(x, y);
        frame.setSize(width, height);

        prefs = Preferences.userRoot().node(PREFS_NODE_GRAPH);
        x = prefs.getInt(PREF_X, 100);
        y = prefs.getInt(PREF_Y, 100);
        width = prefs.getInt(PREF_WIDTH, 400);
        height = prefs.getInt(PREF_HEIGHT, 300);

        graphFrame.setLocation(x, y);
        graphFrame.setSize(width, height);
    }

    private static void saveWindowPositionAndSize(JFrame frame, JFrame graphFrame) {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE_CONTROL);
        prefs.putInt(PREF_X, frame.getX());
        prefs.putInt(PREF_Y, frame.getY());
        prefs.putInt(PREF_WIDTH, frame.getWidth());
        prefs.putInt(PREF_HEIGHT, frame.getHeight());

        prefs = Preferences.userRoot().node(PREFS_NODE_GRAPH);
        prefs.putInt(PREF_X, graphFrame.getX());
        prefs.putInt(PREF_Y, graphFrame.getY());
        prefs.putInt(PREF_WIDTH, graphFrame.getWidth());
        prefs.putInt(PREF_HEIGHT, graphFrame.getHeight());

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private static JButton createAlgorithmButton(String name, Algorithm algorithm) {
        JButton algo = new JButton(name);
        algo.addActionListener(e -> {
            if (Algorithm.isRunning()) {
                System.out.println("An algorithm is already running");
                return;
            }
            dataSetSelection.setEnabled(false);
            clear.setEnabled(false);
            var t = new Thread(algorithm);
            algorithm.onFinish(() -> {
                dataSetSelection.setEnabled(true);
                clear.setEnabled(true);
            });
            t.start();
        });
        return algo;
    }

    private static void createAlgorithmButtons(JPanel buttonPanel, GridBagConstraints c) {
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
            Util.clear();
        });
        checkErrors.addActionListener(e -> CheckErrors.check(provider));
        score.addActionListener(e -> Score.score(provider));
        cancelAlgo.addActionListener(e -> {
            Algorithm.cancel();
            dataSetSelection.setEnabled(true);
            clear.setEnabled(true);
        });
        showGraph.addActionListener(e -> {
            if (showGraph.getText().equals("Show Graph")) {
                showGraph.setText("Hide Graph");
                graphFrame.setVisible(true);
            } else {
                showGraph.setText("Show Graph");
                graphFrame.setVisible(false);
            }
        });

        dataSetSelection.setLayout(new BoxLayout(dataSetSelection, BoxLayout.X_AXIS));
        JSpinner dataSet = new JSpinner(new SpinnerNumberModel(initialDataset, -1, 5, 1));
        dataSetSelection.add(dataSet);
        JButton loadSet = new JButton("Load Set");
        dataSetSelection.add(loadSet);
        dataSetSelection.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("enabled")) {
                loadSet.setEnabled((boolean) e.getNewValue());
            }
        });
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

            //Clear console
            System.out.println(new String(new char[50]).replace("\0", "\r\n"));
            //load new set
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
        constraints.gridy++;
        panel.add(cancelAlgo, constraints);
        constraints.gridy++;
        panel.add(showGraph, constraints);
        constraints.gridy += 2;
        panel.add(dataSetSelection, constraints);
    }
}