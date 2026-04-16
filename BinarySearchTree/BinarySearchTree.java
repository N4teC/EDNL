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

        // Ensure the new root (if any) doesn't hold an old parent reference
        if (root != null) {
            root.setParent(null);
        }
    }

    private Node<T> removeRecursive(Node<T> current, T value) {
        if (current == null) {
            return null; // Base case: end of branch, key not found
        }

        T currentValue = current.getObject(); // Read current node value

        // Step 1: Navigate the tree to find the target node
        if (value.compareTo(currentValue) < 0) {
            // Target is smaller, go to the left subtree
            Node<T> leftChild = current.getLeftChild();
            Node<T> newLeftChild = removeRecursive(leftChild, value); 
            current.setLeftChild(newLeftChild); // Re-link the modified left subtree
            if (newLeftChild != null) {
                newLeftChild.setParent(current); // Keep bidirectional continuity 
            }
        } else if (value.compareTo(currentValue) > 0) {
            // Target is explicitly bigger, go to the right subtree
            Node<T> rightChild = current.getRightChild();
            Node<T> newRightChild = removeRecursive(rightChild, value); 
            current.setRightChild(newRightChild); // Re-link the modified right subtree 
            if (newRightChild != null) {
                newRightChild.setParent(current); // Keep bidirectional continuity 
            }
        } else {
            // Step 2: Target node found! Proceed with removal operations
            
            // Case 1: Node is a Leaf (no children)
            if (current.getLeftChild() == null && current.getRightChild() == null) {
                return null; // Returning null unlinks this node from its parent
            } 
            // Case 2A: Node only has a Right child
            else if (current.getLeftChild() == null) {
                return current.getRightChild(); // Returning right child unlinks current, shifting the child up
            } 
            // Case 2B: Node only has a Left child
            else if (current.getRightChild() == null) {
                return current.getLeftChild(); // Returning left child unlinks current, shifting the child up
            } 
            // Case 3: Node has both Left and Right children
            else {
                // To maintain BST order, we replace this node's value with the "In-Order Successor"
                // The successor is the smallest value in the right subtree
                Node<T> rightChild = current.getRightChild();
                T minValue = findMinValue(rightChild);
                
                // Overwrite the current node's value with the successor's value
                current.setElement(minValue); 
                
                // Recursively delete the successor node from the right subtree
                Node<T> newRightChild = removeRecursive(rightChild, minValue); 
                current.setRightChild(newRightChild);
                if (newRightChild != null) {
                    newRightChild.setParent(current); // Maintain parent integrity
                }
            }
        }
        return current; // Return the current (possibly unmodified) sub-tree root upwards
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
