import javax.swing.*;
import java.awt.*;

public class Main {

    public static int WIDTH_SIZE;
    public static int HEIGHT_SIZE;

    public static void main(String[] args) {
        Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        WIDTH_SIZE = (int) (screenSize.width * 0.9);
        HEIGHT_SIZE = (int) (screenSize.height * 0.9);

        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        UIManager.put("Button.disabledText", Color.black);
        javax.swing.SwingUtilities.invokeLater(Window::createAndShowGUI);
    }

}
