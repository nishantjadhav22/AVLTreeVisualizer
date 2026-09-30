import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

/** Paints tree links and node cards using Java2D. */
public class TreePanel extends JPanel {
    private static final int NODE_WIDTH = 82;
    private static final int NODE_HEIGHT = 66;
    private static final int HORIZONTAL_GAP = 112;
    private static final int LEVEL_GAP = 112;
    private final AVLTree tree;
    private Integer highlightedValue;

    public TreePanel(AVLTree tree) {
        this.tree = tree;
        setBackground(new Color(247, 249, 252));
        setPreferredSize(new Dimension(850, 500));
        setToolTipText("Each node shows its value, height (H), and balance factor (BF).");
    }

    public void setHighlightedValue(Integer value) {
        highlightedValue = value;
        repaint();
    }

    /** Recalculate scrollable canvas size after a tree operation. */
    public void refreshTree() {
        AVLNode root = tree.getRoot();
        int nodeCount = countNodes(root);
        int depth = root == null ? 1 : root.height;
        int width = Math.max(850, nodeCount * HORIZONTAL_GAP + 100);
        int height = Math.max(500, depth * LEVEL_GAP + 100);
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    private int countNodes(AVLNode node) {
        return node == null ? 0 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AVLNode root = tree.getRoot();
        if (root == null) {
            g.setColor(new Color(105, 116, 132));
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawCentered(g, "AVL Tree is Empty", getWidth() / 2, getHeight() / 2);
            g.dispose();
            return;
        }

        Map<AVLNode, Dimension> positions = new HashMap<>();
        int[] inorderIndex = {0};
        layout(root, 0, inorderIndex, positions);
        // Draw links first so they remain behind the node cards.
        g.setColor(new Color(148, 163, 184));
        g.setStroke(new java.awt.BasicStroke(2f));
        for (Map.Entry<AVLNode, Dimension> entry : positions.entrySet()) {
            AVLNode node = entry.getKey();
            Dimension point = entry.getValue();
            if (node.left != null) drawEdge(g, point, positions.get(node.left));
            if (node.right != null) drawEdge(g, point, positions.get(node.right));
        }
        for (Map.Entry<AVLNode, Dimension> entry : positions.entrySet()) {
            drawNode(g, entry.getKey(), entry.getValue());
        }
        g.dispose();
    }

    private void layout(AVLNode node, int level, int[] index, Map<AVLNode, Dimension> positions) {
        if (node == null) return;
        layout(node.left, level + 1, index, positions);
        int x = 58 + index[0] * HORIZONTAL_GAP;
        int y = 42 + level * LEVEL_GAP;
        positions.put(node, new Dimension(x, y));
        index[0]++;
        layout(node.right, level + 1, index, positions);
    }

    private void drawEdge(Graphics2D g, Dimension parent, Dimension child) {
        g.drawLine(parent.width, parent.height + NODE_HEIGHT / 2,
                child.width, child.height - NODE_HEIGHT / 2);
    }

    private void drawNode(Graphics2D g, AVLNode node, Dimension point) {
        int x = point.width - NODE_WIDTH / 2;
        int y = point.height - NODE_HEIGHT / 2;
        boolean highlighted = highlightedValue != null && highlightedValue == node.data;
        g.setColor(highlighted ? new Color(245, 158, 11) : Color.WHITE);
        g.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 18, 18);
        g.setColor(highlighted ? new Color(180, 83, 9) : new Color(37, 99, 145));
        g.setStroke(new java.awt.BasicStroke(highlighted ? 3f : 2f));
        g.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 18, 18);
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.setColor(new Color(25, 45, 65));
        drawCentered(g, Integer.toString(node.data), point.width, y + 22);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(76, 91, 110));
        drawCentered(g, "H: " + node.height + "   BF: " + tree.getBalance(node), point.width, y + 46);
    }

    private void drawCentered(Graphics2D g, String text, int centerX, int baselineY) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
    }
}
