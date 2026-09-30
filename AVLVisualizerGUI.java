import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;

/** Main Swing window and event handlers for the AVL visualizer. */
public class AVLVisualizerGUI extends JFrame {
    private final AVLTree tree = new AVLTree();
    private final TreePanel treePanel = new TreePanel(tree);
    private JTextField valueField;
    private JTextArea outputArea;
    private JLabel statusLabel;
    private JLabel rotationLabel;

    public AVLVisualizerGUI() {
        setTitle("AVL Tree Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 650));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));
        add(createHeaderAndControls(), BorderLayout.NORTH);
        add(new JScrollPane(treePanel), BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderAndControls() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 10));
        wrapper.setOpaque(false);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 50, 78));
        header.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 18));
        JLabel title = new JLabel("AVL TREE VISUALIZER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel subtitle = new JLabel("Data Structures Mini Project");
        subtitle.setForeground(new Color(204, 224, 238));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 3));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);
        header.add(titleGroup, BorderLayout.WEST);
        JButton aboutButton = new JButton("ABOUT");
        aboutButton.addActionListener(e -> showAbout());
        header.add(aboutButton, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        controls.setBorder(BorderFactory.createTitledBorder("Tree operations"));
        controls.add(new JLabel("Enter Value:"));
        valueField = new JTextField(9);
        valueField.addActionListener(e -> insertNode());
        controls.add(valueField);
        JButton insert = button("INSERT");
        JButton delete = button("DELETE");
        JButton search = button("SEARCH");
        JButton reset = button("RESET");
        JButton sample = button("LOAD SAMPLE");
        controls.add(insert);
        controls.add(delete);
        controls.add(search);
        controls.add(reset);
        controls.add(sample);
        insert.addActionListener(e -> insertNode());
        delete.addActionListener(e -> deleteNode());
        search.addActionListener(e -> searchNode());
        reset.addActionListener(e -> resetTree());
        sample.addActionListener(e -> loadSample());
        wrapper.add(controls, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createSidePanel() {
        JPanel side = new JPanel(new BorderLayout(8, 8));
        side.setPreferredSize(new Dimension(255, 0));
        side.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JPanel demonstrations = new JPanel(new GridLayout(3, 2, 6, 6));
        demonstrations.setBorder(BorderFactory.createTitledBorder("Rotation demonstrations"));
        addDemoButton(demonstrations, "DEMO LL", new int[]{30, 20, 10}, RotationType.LL);
        addDemoButton(demonstrations, "DEMO RR", new int[]{10, 20, 30}, RotationType.RR);
        addDemoButton(demonstrations, "DEMO LR", new int[]{30, 10, 20}, RotationType.LR);
        addDemoButton(demonstrations, "DEMO RL", new int[]{10, 30, 20}, RotationType.RL);
        demonstrations.add(new JLabel("Select a demo", SwingConstants.CENTER));
        side.add(demonstrations, BorderLayout.NORTH);

        JPanel traversal = new JPanel(new BorderLayout(5, 6));
        traversal.setBorder(BorderFactory.createTitledBorder("Traversals"));
        JPanel traversalButtons = new JPanel(new GridLayout(1, 3, 4, 4));
        JButton inorder = button("INORDER");
        JButton preorder = button("PREORDER");
        JButton postorder = button("POSTORDER");
        traversalButtons.add(inorder);
        traversalButtons.add(preorder);
        traversalButtons.add(postorder);
        outputArea = new JTextArea(6, 20);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(new Color(248, 250, 252));
        traversal.add(traversalButtons, BorderLayout.NORTH);
        traversal.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        inorder.addActionListener(e -> showTraversal("Inorder", tree.getInorder()));
        preorder.addActionListener(e -> showTraversal("Preorder", tree.getPreorder()));
        postorder.addActionListener(e -> showTraversal("Postorder", tree.getPostorder()));
        side.add(traversal, BorderLayout.CENTER);

        JTextArea complexity = new JTextArea("AVL Tree Complexity\nSearch   O(log n)\nInsert   O(log n)\nDelete   O(log n)\nRotation O(1)\nTraversal O(n)\n\nAVL height stays logarithmic because each subtree is kept within a balance factor of -1 to +1.");
        complexity.setEditable(false);
        complexity.setLineWrap(true);
        complexity.setWrapStyleWord(true);
        complexity.setFont(new Font("SansSerif", Font.PLAIN, 12));
        complexity.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Complexity"), BorderFactory.createEmptyBorder(5, 7, 7, 7)));
        side.add(complexity, BorderLayout.SOUTH);
        return side;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(210, 218, 228)), BorderFactory.createEmptyBorder(7, 4, 0, 4)));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotationLabel = new JLabel("Rotation: —", SwingConstants.RIGHT);
        rotationLabel.setForeground(new Color(37, 99, 145));
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(rotationLabel, BorderLayout.EAST);
        return bar;
    }

    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    private void addDemoButton(JPanel panel, String label, int[] values, RotationType expected) {
        JButton demo = button(label);
        demo.addActionListener(e -> {
            tree.root = null;
            for (int value : values) tree.insert(value);
            refreshTree();
            outputArea.setText("Inserted: " + join(values) + "\nExpected root: 20");
            statusLabel.setText(label + " complete — root is " + tree.getRoot().data);
            rotationLabel.setText("Rotation: " + tree.getLastRotationDescription());
            treePanel.setHighlightedValue(tree.getRoot().data);
        });
        panel.add(demo);
    }

    private String join(int[] values) {
        StringBuilder text = new StringBuilder();
        for (int value : values) {
            if (text.length() > 0) text.append(", ");
            text.append(value);
        }
        return text.toString();
    }

    private Integer readValue() {
        String text = valueField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value.", "Input required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void insertNode() {
        Integer value = readValue();
        if (value == null) return;
        if (!tree.insert(value)) {
            statusLabel.setText("Value already exists!");
            return;
        }
        treePanel.setHighlightedValue(null);
        refreshTree();
        statusLabel.setText("Inserted: " + value);
        showRotationStatus();
        valueField.setText("");
    }

    private void deleteNode() {
        Integer value = readValue();
        if (value == null) return;
        if (!tree.delete(value)) {
            statusLabel.setText("Value not found!");
            return;
        }
        treePanel.setHighlightedValue(null);
        refreshTree();
        statusLabel.setText("Deleted: " + value);
        showRotationStatus();
        valueField.setText("");
    }

    private void searchNode() {
        Integer value = readValue();
        if (value == null) return;
        boolean found = tree.search(value);
        treePanel.setHighlightedValue(found ? value : null);
        statusLabel.setText(found ? "Node " + value + " Found!" : "Node " + value + " Not Found!");
    }

    private void resetTree() {
        tree.root = null;
        treePanel.setHighlightedValue(null);
        treePanel.refreshTree();
        outputArea.setText("");
        statusLabel.setText("AVL Tree is Empty");
        rotationLabel.setText("Rotation: —");
        valueField.setText("");
    }

    private void loadSample() {
        tree.root = null;
        int[] values = {30, 20, 40, 10, 25, 50, 5};
        for (int value : values) tree.insert(value);
        treePanel.setHighlightedValue(null);
        refreshTree();
        statusLabel.setText("Sample data loaded (30, 20, 40, 10, 25, 50, 5)");
        rotationLabel.setText("Rotation: " + tree.getLastRotationDescription());
        outputArea.setText("Sample data loaded.\nInorder: " + tree.getInorder());
    }

    private void refreshTree() {
        treePanel.refreshTree();
    }

    private void showRotationStatus() {
        RotationType type = tree.getLastRotationType();
        rotationLabel.setText("Rotation: " + (type == RotationType.NONE ? "None" : tree.getLastRotationDescription()));
        if (type != RotationType.NONE) statusLabel.setText(statusLabel.getText() + " — " + tree.getLastRotationDescription());
    }

    private void showTraversal(String name, String values) {
        outputArea.setText(name + ":\n" + (values.isEmpty() ? "(empty tree)" : values));
        statusLabel.setText(name + " traversal complete");
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "AVL Tree Visualizer\nData Structures Mini Project\n\nDeveloped using: Java + Java Swing\n\nFeatures:\n• Insert, delete, and search\n• AVL rotations and tree visualization\n• Height and balance factor\n• Inorder, preorder, and postorder traversals",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
