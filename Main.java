import javax.swing.SwingUtilities;

/** Application entry point. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AVLVisualizerGUI().setVisible(true));
    }
}
