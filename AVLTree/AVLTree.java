package AVLTree;

import BinarySearchTree.BinarySearchTree;
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

    @Override
    public AVLNode<T> search(T key) {
        AVLNode<T> current = cast(root);
        lastParent = null; // Reset lastParent before searching

        while (current != null) {
            int comparison = key.compareTo(current.getObject());
            if (comparison == 0) {
                return current; // Key found
            }
            lastParent = current; // Update lastParent to the current node
            current = (comparison < 0)
                    ? cast(current.getLeftChild())
                    : cast(current.getRightChild());
        }
        return null; // Key not found
    }

    @Override
    public void insert(T key) {
        if (root == null) {
            root = createNode(key, null); // Create the root node
            return;
        }

        AVLNode<T> found = search(key);
        if (found != null) {
            return; // Key already exists, do not insert duplicates
        }

        AVLNode<T> parent = cast(lastParent);
        AVLNode<T> newNode = cast(createNode(key, parent));

        if (key.compareTo(parent.getObject()) < 0) {
            parent.setLeftChild(newNode); // Set the new node as the left child of the parent
        } else {
            parent.setRightChild(newNode); // Set the new node as the right child of the parent
        }

        balance(parent); // Balance the tree after insertion
    }

    @Override
    public void remove(T key) {
        AVLNode<T> nodeToRemove = search(key);
        if (nodeToRemove == null) {
            return; // Key not found, nothing to remove
        }
        AVLNode<T> parent = cast(nodeToRemove.getParent());

        if (nodeToRemove.getLeftChild() == null
                || nodeToRemove.getRightChild() == null) {
            // Node has at most one child
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
                child.setParent(parent); // Update the parent reference of the child
            }
        } else {
            // Node has two children, find the in-order sucessor
            AVLNode<T> successor = cast(nodeToRemove.getRightChild());
            while (successor.getLeftChild() != null) {
                successor = cast(successor.getLeftChild());
            }
            T tempKey = successor.getObject();
            delete(successor.getObject()); // Remove the successor node
            nodeToRemove.setElement(tempKey); // Replace the key of the node to remove with
        }
        balance(parent); // Balance the tree after deletion
    }

    // Balancing methods
    private void update(AVLNode<T> node) {
        node.height
                = Math.max(h(cast(node.getLeftChild())), h(cast(node.getRightChild()))) + 1;
        node.balanceFactor = bf(node);
    }

    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        // ...
    }

    private AVLNode<T> rotateRight(AVLNode<T> node) {
        // ...
    }

    private AVLNode<T> rebalance(AVLNode<T> node) {
        // ...
    }

    private void balance(AVLNode<T> node) {
        AVLNode<T> current = node;
        while (current != null) {
            AVLNode<T> parent = cast(current.getParent());
            rebalance(current);
            current = parent; // Move up to the parent node
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
                : h(cast(node.getRightChild())) - h(cast(node.getLeftChild())); // Balance factor is right height - left height
    }

    private AVLNode<T> newNode(T key, Node<T> parent) {
        return new AVLNode<>(key, parent);
    }
}
