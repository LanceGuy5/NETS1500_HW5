import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class Window {

    enum mapOp {
        CONTAINS_KEY,
        CONTAINS_VALUE,
        GET,
        PUT,
        REMOVE,
        CLEAR
    }

    private static MultiGraph graphView;
    private static SpriteManager graphSpriteManger;
    private static JFrame mainFrame;
    private static int ySegment = 0;

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

        // Makes input field for major code
        // Major codes found here: https://catalog.upenn.edu/courses/
        JLabel majorCodeLabel = new JLabel("Major code (i.e. CIS): ");
        keyTextField = new JTextField();
        keyTextField.setColumns(5);

        // Search for a specific course here
        JLabel codeSearchLabel = new JLabel("Course description (takes code): ");
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
        searchButton.addActionListener(e -> {
            // Define the mapOp.PUT operation
            hashOperation(mapOp.GET, codeSearchTextField.getText());
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            // Clears graph
            hashOperation(mapOp.CLEAR, keyTextField.getText());
        });

        controlPanel.add(majorCodeLabel);
        controlPanel.add(keyTextField);
        controlPanel.add(codeSearchTextField);

        controlPanel.add(searchButton);
        controlPanel.add(searchCourseButton);
        controlPanel.add(clearBtn);

        graphView = new MultiGraph("Graph");
        graphSpriteManger = new SpriteManager(graphView);
        Viewer viewer = new Viewer(graphView, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();
        JPanel view = viewer.addDefaultView(false);

        repaint();

        mainFrame.add(controlPanel, BorderLayout.NORTH);
        mainFrame.add(view, BorderLayout.CENTER);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }

    private static void recursiveDraw(int startX, int endX, int level, ClassNode root, Node parentNode) {
        // Generate a universal ID for our new node
        String id = UUID.randomUUID().toString();

        // Initialize the new node in the visualizer
        Node n = graphView.addNode(id);

        // Set the x and y coordinate of our node
        n.setAttribute("xy", (startX + endX) / 2, -level * ySegment);

        // Set the label of n as the code of the newly added ClassNode
        n.setAttribute("ui.label", root.getCode());

        // Add sprite and generate (UI)
//        Sprite s = graphSpriteManger.addSprite(UUID.randomUUID().toString());
//        s.attachToNode(id);
//        s.setPosition(Units.PX, 24, 0, 0);
//        s.setAttribute("ui.label", root.getCode());

        // If the "parent" to the node we are adding is not null, draw an edge.
        if (parentNode != null) {
            graphView.addEdge(UUID.randomUUID().toString(), parentNode, n);
        }

        // For spacing purposes in the future
        int numOfChild = 0;

        // If there are some children to root
        if (!root.getChildren().isEmpty()) {
            // Mark for UI purposes
            n.setAttribute("ui.class", "marked");

            // For each child, count (spacing for later on)
            for (ClassNode child : root.getChildren()) {
                if (child != null) {
                    numOfChild++;
                }
            }

            // Determine segment length for clean rendering
            int xSegment = (endX - startX) / numOfChild;
            int counter = 0;

            // Recursively draw the children
            for (ClassNode child : root.getChildren()) {
                recursiveDraw(startX + counter * xSegment,
                        startX + (counter + 1) * xSegment,
                        level + 1,
                        child,
                        n);
                counter++;
            }
        }
    }

    private static void repaint() {
        // Clear view and set up for UI development
        graphView.clear();
        graphView.addAttribute("ui.stylesheet", styleSheet);

        // Get height of graph for rendering purposes
        int height = graph.getHeight();

        // Determine y differential for rendering
        ySegment = Main.HEIGHT_SIZE / (height + 1);

        // Get root node
        ClassNode root = graph.getRoot();

        // Draw down from root node
        recursiveDraw(0, Main.WIDTH_SIZE, 0, root, null);
    }

    private static void hashOperation(mapOp op, String code) {
        keyTextField.setText("");
        codeSearchTextField.setText("");

        switch (op) {
            case PUT:
//                map.put(code, value);
//                keySet.add(code);
//                repaint();
                break;
            case REMOVE:
//                map.remove(code);
//                keySet.remove(code);
//                repaint();
                break;
            case CLEAR:
//                map.clear();
//                keySet.clear();
//                repaint();
                break;
            case CONTAINS_KEY:
//                boolean containsKey = map.containsKey(code);
//                JOptionPane.showMessageDialog(mainFrame, code + (containsKey ? " exists " : " missing ") + "in the keys of the map");
                break;
            case CONTAINS_VALUE:
//                boolean containsValue = map.containsValue(value);
//                JOptionPane.showMessageDialog(mainFrame, value + (containsValue ? " exists " : " missing ") + "in the values of the map");
                break;
            case GET:
//                String v = map.get(code);
//                repaint();
//                JOptionPane.showMessageDialog(mainFrame, "Value for key " + code + " is " + v);
                break;
        }
    }

}
