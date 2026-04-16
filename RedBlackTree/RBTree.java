package RedBlackTree;

import BinarySearchTree.BinarySearchTree;
import BinarySearchTree.Node;

public class RBTree<
  T extends Comparable<? super T>> extends BinarySearchTree<T> {
    
    public RBTree() {
        super();
    }

    @Override
    public RBNode<T> getRoot() {
        return cast(this.root);
    }

    @Override
    protected Node<T> createNode(T key, Node<T> parent) {
        return new RBNode<>(key, parent);
    }

    // Search method override
    @Override
    public RBNode<T> search(T key) {
        RBNode<T> current = cast(root);
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

        RBNode<T> found = search(key);
        if (found != null) {
            return; // Key already exists, do not insert duplicates
        }

        RBNode<T> parent = cast(lastParent);
        RBNode<T> newNode = cast(createNode(key, parent)); // Create the new node linking to its parent

        if (key.compareTo(parent.getObject()) < 0) {
            parent.setLeftChild(newNode); // Set the new node as the left child of the parent
        } else {
            parent.setRightChild(newNode); // Set the new node as the right child of the parent
        }

        balance(parent); // Balance the tree traversing from the inserted node's parent up to the root
    }

    // Balance methods

    private void balance(RBNode<T> node) {
        // Implement the balancing logic for the Red-Black Tree
        // This will involve checking the colors of the nodes and performing rotations and recoloring as necessary
    }

    // Helper methods
    private RBNode<T> cast(Node<T> node) {
        return (RBNode<T>) node;
    }
}
