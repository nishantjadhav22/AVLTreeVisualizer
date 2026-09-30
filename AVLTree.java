import java.util.ArrayList;
import java.util.List;

/** AVL tree implementation. This class contains no GUI code. */
public class AVLTree {
    AVLNode root;
    private RotationType lastRotationType = RotationType.NONE;

    public AVLNode getRoot() {
        return root;
    }

    public RotationType getLastRotationType() {
        return lastRotationType;
    }

    public String getLastRotationDescription() {
        switch (lastRotationType) {
            case LL: return "LL Case → Right Rotation";
            case RR: return "RR Case → Left Rotation";
            case LR: return "LR Case → Left Rotation + Right Rotation";
            case RL: return "RL Case → Right Rotation + Left Rotation";
            default: return "No rotation required";
        }
    }

    public int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    public int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    /** Inserts a value; returns false when it is already present. */
    public boolean insert(int value) {
        if (search(value)) return false;
        lastRotationType = RotationType.NONE;
        root = insertRecursive(root, value);
        return true;
    }

    private AVLNode insertRecursive(AVLNode node, int value) {
        if (node == null) return new AVLNode(value);
        if (value < node.data) node.left = insertRecursive(node.left, value);
        else node.right = insertRecursive(node.right, value);

        updateHeight(node);
        int balance = getBalance(node);

        // The inserted value's path identifies the LL, RR, LR, or RL case.
        if (balance > 1 && value < node.left.data) {
            recordRotation(RotationType.LL);
            return rightRotate(node);
        }
        if (balance < -1 && value > node.right.data) {
            recordRotation(RotationType.RR);
            return leftRotate(node);
        }
        if (balance > 1 && value > node.left.data) {
            recordRotation(RotationType.LR);
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && value < node.right.data) {
            recordRotation(RotationType.RL);
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    /** Deletes a value; returns false when it is absent. */
    public boolean delete(int value) {
        if (!search(value)) return false;
        lastRotationType = RotationType.NONE;
        root = deleteRecursive(root, value);
        return true;
    }

    private AVLNode deleteRecursive(AVLNode node, int value) {
        if (node == null) return null;
        if (value < node.data) {
            node.left = deleteRecursive(node.left, value);
        } else if (value > node.data) {
            node.right = deleteRecursive(node.right, value);
        } else if (node.left == null || node.right == null) {
            node = node.left != null ? node.left : node.right;
        } else {
            // Replace a two-child node with its inorder successor.
            AVLNode successor = minimumNode(node.right);
            node.data = successor.data;
            node.right = deleteRecursive(node.right, successor.data);
        }

        if (node == null) return null;
        updateHeight(node);
        int balance = getBalance(node);

        // Deletion cases are determined by the heavy child's balance factor.
        if (balance > 1 && getBalance(node.left) >= 0) {
            recordRotation(RotationType.LL);
            return rightRotate(node);
        }
        if (balance > 1 && getBalance(node.left) < 0) {
            recordRotation(RotationType.LR);
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) {
            recordRotation(RotationType.RR);
            return leftRotate(node);
        }
        if (balance < -1 && getBalance(node.right) > 0) {
            recordRotation(RotationType.RL);
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private void recordRotation(RotationType type) {
        // Keep the first reported rebalance if cascading deletion needs more than one.
        if (lastRotationType == RotationType.NONE) lastRotationType = type;
    }

    private AVLNode minimumNode(AVLNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public boolean search(int value) {
        AVLNode current = root;
        while (current != null) {
            if (value == current.data) return true;
            current = value < current.data ? current.left : current.right;
        }
        return false;
    }

    public String getInorder() {
        List<Integer> values = new ArrayList<>();
        inorder(root, values);
        return join(values);
    }

    public String getPreorder() {
        List<Integer> values = new ArrayList<>();
        preorder(root, values);
        return join(values);
    }

    public String getPostorder() {
        List<Integer> values = new ArrayList<>();
        postorder(root, values);
        return join(values);
    }

    private void inorder(AVLNode node, List<Integer> values) {
        if (node == null) return;
        inorder(node.left, values);
        values.add(node.data);
        inorder(node.right, values);
    }

    private void preorder(AVLNode node, List<Integer> values) {
        if (node == null) return;
        values.add(node.data);
        preorder(node.left, values);
        preorder(node.right, values);
    }

    private void postorder(AVLNode node, List<Integer> values) {
        if (node == null) return;
        postorder(node.left, values);
        postorder(node.right, values);
        values.add(node.data);
    }

    private String join(List<Integer> values) {
        StringBuilder result = new StringBuilder();
        for (int value : values) {
            if (result.length() > 0) result.append(' ');
            result.append(value);
        }
        return result.toString();
    }

    private AVLNode rightRotate(AVLNode top) {
        AVLNode pivot = top.left;
        AVLNode transferSubtree = pivot.right;
        pivot.right = top;
        top.left = transferSubtree;
        updateHeight(top);
        updateHeight(pivot);
        return pivot;
    }

    private AVLNode leftRotate(AVLNode top) {
        AVLNode pivot = top.right;
        AVLNode transferSubtree = pivot.left;
        pivot.left = top;
        top.right = transferSubtree;
        updateHeight(top);
        updateHeight(pivot);
        return pivot;
    }
}
