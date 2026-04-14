package BinarySearchTree;

import java.util.Collections;

public class BinarySearchTree<T extends Comparable<? super T>> {

    protected Node<T> root;
    protected Node<T> lastParent;

    // Constructor
    public BinarySearchTree() {
        this.root = null;
        this.lastParent = null;
    }

    // Insert method
    protected Node<T> createNode(T key, Node<T> parent) {
        return new Node<>(parent, key);
    }

    public void insert(T key) {
        if (root == null) {
            root = createNode(key, null); // Create the root node
        } else {
            insertRecursive(root, key); // Insert recursively starting from the root
        }
    }

    private void insertRecursive(Node<T> current, T key) {
        T currentValue = current.getObject();

        if (key.compareTo(currentValue) < 0) {
            // Go left
            if (current.getLeftChild() == null) {
                Node<T> newNode = createNode(key, current); // Create a new node with the current node as its parent
                current.setLeftChild(newNode); // Set the new node as the left child of the current node
            } else {
                insertRecursive(current.getLeftChild(), key); // Recur on the left child
            }
        } else if (key.compareTo(currentValue) > 0) {
            // Go right
            if (current.getRightChild() == null) {
                Node<T> newNode = createNode(key, current); // Create a new node with the current node as its parent
                current.setRightChild(newNode); // Set the new node as the right child of the current node
            } else {
                insertRecursive(current.getRightChild(), key); // Recur on the right child
            }
        }
    }

    // Remove method

    public void remove(T key) {
        root = removeRecursive(root, key);

        if (root != null) {
            root.setParent(null);
        }
    }

    private Node<T> removeRecursive(Node<T> current, T value) {
        if (current == null) {
            return null; // Key not found
        }

        T currentValue = current.getObject();

        if (value.compareTo(currentValue) < 0) {
            // Go left
            Node<T> leftChild = current.getLeftChild();
            Node<T> newLeftChild = removeRecursive(leftChild, value); // Recur on the left child
            current.setLeftChild(newLeftChild);
            if (newLeftChild != null) {
                newLeftChild.setParent(current); // Update parent reference
            }
        } else if (value.compareTo(currentValue) > 0) {
            // Go right
            Node<T> rightChild = current.getRightChild();
            Node<T> newRightChild = removeRecursive(rightChild, value); // Recur on the right child
            current.setRightChild(newRightChild);
            if (newRightChild != null) {
                newRightChild.setParent(current); // Update parent reference
            }
        } else {
            // Node to delete found
            if (current.getLeftChild() == null && current.getRightChild() == null) {
                return null; // Node has no children, simply remove it
            } else if (current.getLeftChild() == null) {
                // Node has only right child
                return current.getRightChild(); // Replace it with the right child
            } else if (current.getRightChild() == null) {
                // Node has only left child
                return current.getLeftChild(); // Replace it with the left child
            } else {
                // Node has two children, replace it with the smallest value in the right subtree
                Node<T> rightChild = current.getRightChild();
                T minValue = findMinValue(rightChild);
                current.setElement(minValue); // Replace current node's value with the minimum value
                Node<T> newRightChild = removeRecursive(rightChild, minValue); // Remove the duplicate value from the right subtree
                current.setRightChild(newRightChild);
                if (newRightChild != null) {
                    newRightChild.setParent(current); // Update parent reference
                }
            }
        }
        return current;
    }

    private T findMinValue(Node<T> node) {
        while (node.getLeftChild() != null) {
            // Go left until the leftmost node is reached
            node = node.getLeftChild();
        }
        return node.getObject();
    }

    // Display method
    public void display() {
        displayRecursive(root, 0); // Start displaying from the root at level 0
    }

    private void displayRecursive(Node<T> node, int depth) {
        if (node == null) {
            return;
        }

        displayRecursive(node.getRightChild(), depth + 1); // Display right subtree first (higher depth)
        System.out.println(
                String.join("", Collections.nCopies(depth * 4, " ")) + node.getObject()
        );
        displayRecursive(node.getLeftChild(), depth + 1); // Display left subtree last (lower depth)
    }

    // Search method
    public Node<T> search(T key) {
        Node<T> current = root;
        lastParent = null;

        while (current != null) {
            int comparison = key.compareTo(current.getObject()); // Compare the key with the current node's value
            if (comparison == 0) {
                return current; // Key found
            }
            lastParent = current; // Update lastParent to the current node before moving down the tree
            current = (comparison < 0)
                    ? current.getLeftChild()
                    : current.getRightChild(); // Go left or right based on comparison
        }
        return null; // Key not found
    }

    public Node<T> search(T key, Node<T> current) {
        if (current == null) {
            return null; // Key not found
        }

        int comparison = key.compareTo(current.getObject()); // Compare the key with the current node's value

        if (comparison == 0) {
            return current; // Key found
        } else if (comparison < 0) {
            return search(key, current.getLeftChild()); // Recur on the left child
        } else {
            return search(key, current.getRightChild()); // Recur on the right child
        }
    }

    // Getters & Setters
    public Node<T> getRoot() {
        return root;
    }

    public Node<T> getLastParent() {
        return lastParent;
    }

    public void setRoot(Node<T> root) {
        this.root = root;
        if (this.root != null) {
            this.root.setParent(null); // Ensure the root's parent is null
        }
    }

    // Mirror method
    public void mirror() {
        mirrorRecursive(root);
    }

    private Node<T> mirrorRecursive(Node<T> node) {
        if (node == null) {
            return null; // Base case: if the node is null, return null
        }

        Node<T> leftMirror = mirrorRecursive(node.getLeftChild()); // Mirror the left subtree
        Node<T> rightMirror = mirrorRecursive(node.getRightChild()); // Mirror the right subtree

        node.setLeftChild(rightMirror);
        if (rightMirror != null) {
            rightMirror.setParent(node); // Update parent reference for the new left child
        }
        node.setRightChild(leftMirror);
        if (leftMirror != null) {
            leftMirror.setParent(node); // Update parent reference for the new right child
        }
        return node;
    }
}
