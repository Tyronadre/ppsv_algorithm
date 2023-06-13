package de.henrik;

import de.henrik.data.Application;
import de.henrik.generator.Provider;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.awt.*;

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
        var graph = new SingleGraph("Tutorial 1");
        graph.setAttribute("ui.anti-alias");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.stylesheet", styleSheet);


        provider.fillGraph(graph);
        var viewer = graph.display();
        viewer.disableAutoLayout();

        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 500);

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

        JButton algo = new JButton("Start Algorithm");
//        algo.addActionListener(e -> AutomaticAssignments.startAlgo(false, provider, graph));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(10, 1));
        buttonPanel.add(jButton);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton2);

        buttonPanel.add(algo);

        frame.add(buttonPanel);
        frame.setVisible(true);

    }
}