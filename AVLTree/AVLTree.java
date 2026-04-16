package AVLTree;

import BinarySearchTree.BinarySearchTree;
import BinarySearchTree.Node;
import java.util.*;

public class AVLTree<
  T extends Comparable<? super T>> extends BinarySearchTree<T> {

    public AVLTree() {
        super();
    }

    @Override
    public AVLNode<T> getRoot() {
        return cast(this.root);
    }

    @Override
    protected Node<T> createNode(T key, Node<T> parent) {
        return new AVLNode<>(key, parent);
    }

    // Search method override
    @Override
    public AVLNode<T> search(T key) {
        AVLNode<T> current = cast(root);
        lastParent = null; // Reset lastParent before searching, used later for insertions

        while (current != null) {
            int comparison = key.compareTo(current.getObject());
            if (comparison == 0) {
                return current; // Key found
            }
            lastParent = current; // Update lastParent to the current node before moving down
            current = (comparison < 0)
                    ? cast(current.getLeftChild()) // Go left
                    : cast(current.getRightChild()); // Go right
        }
        return null; // Key not found
    }

    // Insert method override
    @Override
    public void insert(T key) {
        if (root == null) {
            root = createNode(key, null); // Create the root node if tree is empty
            return;
        }

        AVLNode<T> found = search(key);
        if (found != null) {
            return; // Key already exists, do not insert duplicates
        }

        AVLNode<T> parent = cast(lastParent);
        AVLNode<T> newNode = cast(createNode(key, parent)); // Create the new node linking to its parent

        if (key.compareTo(parent.getObject()) < 0) {
            parent.setLeftChild(newNode); // Set the new node as the left child of the parent
        } else {
            parent.setRightChild(newNode); // Set the new node as the right child of the parent
        }

        balance(parent); // Balance the tree traversing from the inserted node's parent up to the root
    }

    // Remove method override
    @Override
    public void remove(T key) {
        AVLNode<T> nodeToRemove = search(key);
        if (nodeToRemove == null) {
            return; // Key not found, nothing to remove
        }
        AVLNode<T> parent = cast(nodeToRemove.getParent());

        if (nodeToRemove.getLeftChild() == null
                || nodeToRemove.getRightChild() == null) {
            // Case 1 & 2: Node has at most one child (or no children)
            AVLNode<T> child = (nodeToRemove.getLeftChild() != null)
                    ? cast(nodeToRemove.getLeftChild())
                    : cast(nodeToRemove.getRightChild());

            if (parent == null) {
                root = child; // Removing the root node
            } else if (parent.getLeftChild() == nodeToRemove) {
                parent.setLeftChild(child); // Set the child as the left child of the parent
            } else {
                parent.setRightChild(child); // Set the child as the right child of the parent
            }

            if (child != null) {
                child.setParent(parent); // Update the parent reference of the ascending child
            }
        } else {
            // Case 3: Node has two children, find the in-order successor (smallest in the right subtree)
            AVLNode<T> successor = cast(nodeToRemove.getRightChild());
            while (successor.getLeftChild() != null) {
                successor = cast(successor.getLeftChild());
            }
            T tempKey = successor.getObject(); // Store the successor's value
            delete(successor.getObject()); // Recursively remove the successor node (which triggers a bottom-up balance)
            nodeToRemove.setElement(tempKey); // Replace the target node's value with the successor's value
            return; // Return immediately, because the recursive delete() already balanced the tree from the bottom up
        }
        balance(parent); // Balance the tree after deletion for Cases 1 & 2
    }

    // Balancing methods
    private void update(AVLNode<T> node) {
        // Recalculates height based on the tallest child, plus 1 for the current node
        node.height = Math.max(h(cast(node.getLeftChild())), h(cast(node.getRightChild()))) + 1;
        node.balanceFactor = bf(node); // Updates the balance factor (Left height - Right height)
    }

    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        AVLNode<T> y = cast(node.getRightChild()); // y becomes the new root of this subtree
        AVLNode<T> T2 = (y != null) ? cast(y.getLeftChild()) : null; // T2 is the left child of y

        // Perform rotation (y shifts up, the old root shifts left)
        y.setLeftChild(node); // Old root becomes the left child of y
        y.setParent(node.getParent()); // Propagate the old root's parent upward to y

        node.setRightChild(T2); // Reattach y's former left subtree as the new right child of the old root
        if (T2 != null) {
            T2.setParent(node); // Update the parent pointer of T2
        }
        node.setParent(y); // The old root is now a child of y

        // Link the new subtree root (y) to the broader tree structure
        if (y.getParent() == null) {
            root = y; // y becomes the actual tree root if the former root had no parent
        } else if (y.getParent().getLeftChild() == node) {
            y.getParent().setLeftChild(y); // y takes the old root's spot as a left child
        } else {
            y.getParent().setRightChild(y); // y takes the old root's spot as a right child
        }

        int bfB = node.balanceFactor;
        int bfA = y.balanceFactor;

        int newBf_B = bfB + 1 - Math.min(bfA, 0);
        int newBf_A = bfA + 1 + Math.max(newBf_B, 0);

        node.balanceFactor = newBf_B;
        y.balanceFactor = newBf_A;

        node.height = Math.max(h(cast(node.getLeftChild())), h(cast(node.getRightChild()))) + 1;
        y.height = Math.max(h(cast(y.getLeftChild())), h(cast(y.getRightChild()))) + 1;

        return y; // The new root of the rotated structure
    }

    private AVLNode<T> rotateRight(AVLNode<T> node) {
        AVLNode<T> y = cast(node.getLeftChild()); // y becomes the new root of this subtree
        AVLNode<T> T2 = (y != null) ? cast(y.getRightChild()) : null; // T2 is the right child of y

        // Perform rotation (y shifts up, the old root shifts right)
        y.setRightChild(node); // Old root becomes the right child of y
        y.setParent(node.getParent()); // Propagate the old root's parent upward to y

        node.setLeftChild(T2); // Reattach y's former right subtree as the new left child of the old root
        if (T2 != null) {
            T2.setParent(node); // Update the parent pointer of T2
        }

        node.setParent(y); // The old root is now a child of y

        // Link the new subtree root (y) to the broader tree structure
        if (y.getParent() == null) {
            root = y; // y becomes the actual tree root if the former root had no parent
        } else if (y.getParent().getLeftChild() == node) {
            y.getParent().setLeftChild(y); // y takes the old root's spot as a left child
        } else {
            y.getParent().setRightChild(y); // y takes the old root's spot as a right child
        }

        int bfB = node.balanceFactor;
        int bfA = y.balanceFactor;

        int newBf_B = bfB - 1 - Math.max(bfA, 0);
        int newBf_A = bfA - 1 + Math.min(newBf_B, 0);

        node.balanceFactor = newBf_B;
        y.balanceFactor = newBf_A;

        node.height = Math.max(h(cast(node.getLeftChild())), h(cast(node.getRightChild()))) + 1;
        y.height = Math.max(h(cast(y.getLeftChild())), h(cast(y.getRightChild()))) + 1;

        return y; // The new root of the rotated structure
    }

    private AVLNode<T> rotateDoubleRight(AVLNode<T> node) {
        // Double Right Rotation 
        rotateLeft(cast(node.getLeftChild()));
        return rotateRight(node);
    }

    private AVLNode<T> rotateDoubleLeft(AVLNode<T> node) {
        // Double Left Rotation 
        rotateRight(cast(node.getRightChild()));
        return rotateLeft(node);
    }

    private AVLNode<T> rebalance(AVLNode<T> node) {
        update(node); // Recalculate height/balance before performing evaluations
        int balanceFactor = node.balanceFactor;

        if (balanceFactor > 1) {  // Left heavy: the left subtree is unbalanced
            if (bf(cast(node.getLeftChild())) < 0) {
                // Left-Right Unbalance (Dog-leg): child leans completely right
                return rotateDoubleRight(node); // Use double rotation
            }
            // Left-Left Unbalance: Now standard, perform a simple right rotation
            return rotateRight(node); 
        }

        if (balanceFactor < -1) { // Right heavy: the right subtree is unbalanced
            if (bf(cast(node.getRightChild())) > 0) {
                // Right-Left Unbalance (Dog-leg): child leans completely left
                return rotateDoubleLeft(node); // Use double rotation
            }
            // Right-Right Unbalance: Now standard, perform a simple left rotation
            return rotateLeft(node); 
        }

        return node; // No rotation needed, subtree is balanced
    }

    private void balance(AVLNode<T> node) {
        AVLNode<T> current = node;
        // Trace back upward from the inserted/deleted node, rebalancing every layer to the root
        while (current != null) {
            AVLNode<T> parent = cast(current.getParent());
            rebalance(current); // Adjust the subtrees if there's a violation
            current = parent; // Ascend to the next ancestor layer
        }
    }

    // Print methods
    public void printTree() {
        if (root == null) {
            System.out.println("The tree is empty.");
            return;
        }

        int height = cast(root).height;
        List<AVLNode<T>> currentLevel = new ArrayList<>();
        currentLevel.add(cast(root));

        for (int depth = 1; depth <= height; depth++) {
            int gaps = (int) Math.pow(2, height - depth) - 1;
            int between = (int) Math.pow(2, height - depth + 1) - 1;

            printSpaces(gaps);
            List<AVLNode<T>> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i++) {
                AVLNode<T> node = currentLevel.get(i);
                if (node != null) {
                    System.out.print(node.getObject() + "[" + node.balanceFactor + "]");
                    nextLevel.add(cast(node.getLeftChild()));
                    nextLevel.add(cast(node.getRightChild()));
                } else {
                    System.out.print(" ");
                    nextLevel.add(null);
                    nextLevel.add(null); // Keep placeholders
                }
                if (i < currentLevel.size() - 1) {
                    printSpaces(between);
                }
            }
            System.out.println();

            currentLevel = nextLevel;
        }
    }

    // Helper methods
    private AVLNode<T> cast(Node<T> node) {
        return (AVLNode<T>) node;
    }

    private void delete(T value) {
        remove(value);
    }

    private int h(AVLNode<T> node) {
        return (node == null) ? 0 : node.height; // Height of a null node is considered 0
    }

    private int bf(AVLNode<T> node) {
        return (node == null)
                ? 0
                : h(cast(node.getLeftChild())) - h(cast(node.getRightChild())); // Balance factor is left height - right height
    }

    private void printSpaces(int n) {
        for (int i = 0; i < n; i++) {
            System.out.print(" ");
        }
    }
}
