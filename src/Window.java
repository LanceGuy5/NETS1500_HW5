import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class Window {

    enum mapOp {
        GET,
        PUT,
        CLEAR,
        SEARCH
    }

    private static MultiGraph graphView;
    private static JFrame mainFrame;

    private static int[] counters;

    private static ClassGraph graph;

    private static JTextField keyTextField;
    private static JTextField codeSearchTextField;

    protected static String styleSheet =
            "node {" +
                    "fill-color: #d3d3d3;" +
                    "size: 22px;" +
                    "fill-mode: dyn-plain;" +
                    "stroke-color: black;" +
                    "stroke-width: 1px;" +
                    "text-size: 21px;" +
                    "}" +
                    "edge {" +
                    "text-size: 25px;" +
                    "text-background-mode: plain;" +
                    "text-background-color: white;" +
                    "}"
                    + "node.marked { text-color: red; }"
                    + "node.error { fill-mode: none; }"
                    + "sprite {" +
                    "fill-color: #d3d3d3;" +
                    "shape: box;" +
                    "size: 23px;" +
                    "text-size: 21px;" +
                    "}"
                    + "edge.marked {fill-color: red; size: 4px; }";

    public static void createAndShowGUI() {
        // Initialize Graph
        graph = new ClassGraph();

        // Making window
        mainFrame = new JFrame("TrieMap Visualizer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(Main.WIDTH_SIZE, Main.HEIGHT_SIZE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set selected button
        JRadioButton setButton = new JRadioButton("Set Taken");
        setButton.setSelected(false);

        // Get recommended schedule button
        JButton scheduleButton = new JButton("Recommend Schedule");
        scheduleButton.addActionListener(e -> {
            // Define the mapOp.PUT operation
            hashOperation(mapOp.SEARCH, null);
        });

        // Makes input field for major code
        // Major codes found here: https://catalog.upenn.edu/courses/
        JLabel majorCodeLabel = new JLabel("Get Major Tree:");
        keyTextField = new JTextField();
        keyTextField.setColumns(5);

        // Search for a specific course here
        JLabel codeSearchLabel = new JLabel("Get Course description:");
        codeSearchTextField = new JTextField();
        codeSearchTextField.setColumns(5);

        // Panel to store button.
        JPanel controlPanel = new JPanel();

        JButton searchButton = new JButton("Find Major Schedule");
        searchButton.addActionListener(e -> {
            // Define the mapOp.PUT operation
            hashOperation(mapOp.PUT, keyTextField.getText());
        });

        JButton searchCourseButton = new JButton("Find Course Description");
        searchCourseButton.addActionListener(e -> {
            // Define the mapOp.PUT operation
            hashOperation(mapOp.GET, codeSearchTextField.getText());
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            // Clears graph
            hashOperation(mapOp.CLEAR, keyTextField.getText());
        });

        // Search for classes
        controlPanel.add(setButton);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Search for schedule
        controlPanel.add(scheduleButton);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        controlPanel.add(majorCodeLabel);
        controlPanel.add(keyTextField);
        controlPanel.add(searchButton);

        // Gap
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Search for class description
        controlPanel.add(codeSearchLabel);
        controlPanel.add(codeSearchTextField);
        controlPanel.add(searchCourseButton);

        // Gap
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Clear graph
        controlPanel.add(clearBtn);

        graphView = new MultiGraph("Graph");
        Viewer viewer = new Viewer(graphView, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

//        viewer.enableAutoLayout();
        JPanel view = viewer.addDefaultView(false);

        // Logic for clicking on node
        viewer.getDefaultView().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GraphicElement node = viewer.getDefaultView().findNodeOrSpriteAt(e.getX(), e.getY());
                if (!setButton.isSelected()) {
                    if (node != null) {
                        for (Node n : viewer.getGraphicGraph().getEachNode()) {
                            if (graph.getVal(n.getId()).isTaken()) {
                                n.setAttribute("ui.style", "fill-color: green;");
                            } else {
                                n.setAttribute("ui.style", "fill-color: gray;");
                            }
                        }
                        node.setAttribute("ui.style", "fill-color: red;");
                        for (Edge edge : viewer.getGraphicGraph().getEachEdge()) {
                            if (edge.getNode0().equals(node)) {
                                edge.getNode1().setAttribute("ui.style", "fill-color: blue;");
                            }
                        }
                    }
                } else {
                    ClassNode n = graph.getVal(node.getId());
                    if (n.isTaken()) {
                        node.setAttribute("ui.style", "fill-color: gray;");
                        n.setTaken(false);
                        for (ClassNode neighbor : n.getChildren()) {
                            neighbor.setInDegree(neighbor.getInDegree() + 1);
                        }
                    } else {
                        node.setAttribute("ui.style", "fill-color: green;");
                        n.setTaken(true);
                        for (ClassNode neighbor : n.getChildren()) {
                            neighbor.setInDegree(neighbor.getInDegree() - 1);
                        }
                    }
                }
            }
        });

        repaint();

        mainFrame.add(controlPanel, BorderLayout.NORTH);
        mainFrame.add(view, BorderLayout.CENTER);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }

    private static void recursiveDraw(ClassNode root,
                                      Node parentNode) {
        if (root == null) {
            return;
        }

        // Initialize the new node in the visualizer
        Node n = null;
        if (!root.equals(graph.getRoot())) {
            counters[root.getThousand()] = counters[root.getThousand()] + 1;
            n = graphView.addNode(root.getUUID());

            // Some preemptive rendering rules
            n.setAttribute("ui.style", "size: 15px;");
            n.setAttribute("ui.style", "text-size: 15px;");
            // Rendering algorithm
            n.setAttribute("xy",
                    (root.getThousand() / 9.0) * (Main.WIDTH_SIZE),
                    (counters[root.getThousand()])
                            * (Main.HEIGHT_SIZE / (double)graph.getThousandCount(root.getThousand())) + 10);


            // Set the label of n as the code of the newly added ClassNode
            n.setAttribute("ui.label", root.getCode());

            // If the "parent" to the node we are adding is not null, draw an edge.
            if (parentNode != null) {
                graphView.addEdge(UUID.randomUUID().toString(), parentNode, n, true);
            }
        }

        // If there are some children to root
        if (!root.getChildren().isEmpty()) {
            // Mark for UI purposes
//            n.setAttribute("ui.class", "marked");

            // Recursively draw the children
            for (ClassNode child : root.getChildren()) {
                if (graphView.getNode(child.getUUID()) != null) {
                    graphView.addEdge(UUID.randomUUID().toString(),
                            n,
                            graphView.getNode(child.getUUID()),
                            true);
                } else {
                    recursiveDraw(
                            child,
                            n);
                }
            }
        }
    }

    private static void repaint() {

        counters = new int[10];

        // Clear view and set up for UI development
        graphView.clear();
        graphView.addAttribute("ui.stylesheet", styleSheet);

        // Get root node
        ClassNode root = graph.getRoot();

        // Draw down from root node
        recursiveDraw(root, null);
    }

    private static void hashOperation(mapOp op, String code) {
        keyTextField.setText("");
        codeSearchTextField.setText("");

        switch (op) {
            case PUT:
                // What to do if a put request is made
                // This only occurs if we are putting original graph
                if (graph.hasContent()) {
                    // Error here -> already has content in the graph
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Graph has content -> clear first."
                    );
                    break;
                }
                try {
                    graph = WebScraper.scrapeMajor(code);
                    repaint();
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            code + " not a valid major code!"
                    );
                }
                break;
            case CLEAR:
                // Just clears graph
                graph.clear();
                repaint();
                break;
            case GET:
                // What to do if a get request is made
                // This only occurs if graph exists
                if (!graph.hasContent()) {
                    // Throw error -> no content in graph
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Empty graph -> find major requirement tree before you can search a code!"
                    );
                    break;
                }
                // Scrape code
                try {
                    if (!graph.containsVal(code)) {
                        JOptionPane.showMessageDialog(
                                mainFrame, code + " does not appear in the major requirement tree!"
                        );
                        break;
                    }
                    String v = WebScraper.scrapeClassInfo(code);
                    JOptionPane.showMessageDialog(
                            mainFrame, insertNewlines(v, 150)
                    );
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(
                            mainFrame, code + " does not appear in the major requirement tree!"
                    );
                }
                break;
            case SEARCH:
                if (!graph.hasContent()) {
                    // Throw error -> no content in graph
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Empty graph -> find major requirement tree before you can search a code!"
                    );
                    break;
                }
                JOptionPane.showMessageDialog(
                        mainFrame,
                        insertNewlines(graph.recommendSchedule(), 150)
                );
        }
    }

    public static String insertNewlines(String input, int maxLineWidth) {
        StringBuilder builder = new StringBuilder();
        int currentIndex = 0;

        while (currentIndex < input.length()) {
            int endIndex = Math.min(currentIndex + maxLineWidth, input.length());
            // Find the last space within the maximum width
            int lastSpaceIndex = input.substring(currentIndex, endIndex).lastIndexOf(' ');
            if (lastSpaceIndex != -1 && lastSpaceIndex != endIndex - 1) {
                // If there's a space within the maximum width, break the line at that space
                builder.append(input, currentIndex, currentIndex + lastSpaceIndex + 1);
                builder.append("\n");
                currentIndex += lastSpaceIndex + 1;
            } else {
                // If no space found within the maximum width, break the line at the maximum width
                builder.append(input, currentIndex, endIndex);
                builder.append("\n");
                currentIndex = endIndex;
            }
        }

        return builder.toString().replaceAll("&quot;", "\"");
    }

}
