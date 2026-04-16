package RedBlackTree;

import BinarySearchTree.BinarySearchTree;
import BinarySearchTree.Node;
import java.util.ArrayList;
import java.util.List;

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
            cast(root).setColor(false); // Root must be black
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

        balanceInsertion(newNode); // Balance the tree traversing from the inserted node's parent up to the root
    }

    // Remove method override
    @Override
    public void remove(T key) {
        RBNode<T> nodeToRemove = search(key);
        if (nodeToRemove == null) {
            return; // Key not found, nothing to remove
        }

        RBNode<T> child;
        RBNode<T> parent;
        boolean originalColor;

        if (nodeToRemove.getLeftChild() == null || nodeToRemove.getRightChild() == null) {
            // Case 1 & 2: Node has at most one child (or no children)
            originalColor = nodeToRemove.getColor();
            parent = cast(nodeToRemove.getParent());
            child = (nodeToRemove.getLeftChild() != null)
                    ? cast(nodeToRemove.getLeftChild())
                    : cast(nodeToRemove.getRightChild());

            if (parent == null) {
                root = child; // Removing the root node
            } else if (parent.getLeftChild() == nodeToRemove) {
                parent.setLeftChild(child); // Replace the node with its child on the left
            } else {
                parent.setRightChild(child); // Replace the node with its child on the right
            }

            if (child != null) {
                child.setParent(parent); // Update the child's parent reference
            }
        } else {
            // Case 3: Node has two children, find the in-order successor (smallest in the right subtree)
            RBNode<T> successor = cast(nodeToRemove.getRightChild());
            while (successor.getLeftChild() != null) {
                successor = cast(successor.getLeftChild());
            }

            // The physical node to be unlinked is the successor, so we care about ITS original color
            originalColor = successor.getColor();
            parent = cast(successor.getParent());
            child = cast(successor.getRightChild()); // Successor only has right child (or null)

            // Unlink successor
            if (parent.getLeftChild() == successor) {
                parent.setLeftChild(child);
            } else {
                parent.setRightChild(child);
            }

            if (child != null) {
                child.setParent(parent);
            }

            // Replace nodeToRemove's core value with successor's value
            nodeToRemove.setElement(successor.getObject());
        }

        // Only balance if the physically removed node was Black
        if (!originalColor) {
            balanceRemoval(child, parent);
        }
    }

    // Balance methods
    private RBNode<T> rotateLeft(RBNode<T> node) {
        RBNode<T> y = cast(node.getRightChild()); // y becomes the new root of this subtree
        RBNode<T> T2 = (y != null) ? cast(y.getLeftChild()) : null; // T2 is the left child of y

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

        return y; // The new root of the rotated structure
    }

    private RBNode<T> rotateRight(RBNode<T> node) {
        RBNode<T> y = cast(node.getLeftChild()); // y becomes the new root of this subtree
        RBNode<T> T2 = (y != null) ? cast(y.getRightChild()) : null; // T2 is the right child of y

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

        return y; // The new root of the rotated structure
    }

    private void balanceInsertion(RBNode<T> node) {
        // Continue balancing as long as the current node is not the root and its parent is RED
        while (node != root && isRed(cast(node.getParent()))) {
            RBNode<T> parent = cast(node.getParent());
            RBNode<T> grandparent = cast(parent.getParent());

            // Case A: The parent is the left child of the grandparent
            if (parent == grandparent.getLeftChild()) {
                RBNode<T> uncle = cast(grandparent.getRightChild());

                if (isRed(uncle)) {
                    // Case 1: Uncle is RED. 
                    // Action: Recolor parent, uncle, and grandparent. Move node pointer to grandparent.
                    parent.setColor(false); // Black
                    uncle.setColor(false); // Black
                    grandparent.setColor(true); // Red
                    node = grandparent;

                } else {
                    // Case 2: Uncle is BLACK and node is a RIGHT child (Dog-leg / Triangle)
                    // Action: Rotate left on the parent to transform it into a Straight Line (Case 3)
                    if (node == parent.getRightChild()) {
                        node = parent;
                        rotateLeft(node);
                        // After rotation, update references for Case 3
                        parent = cast(node.getParent());
                    }

                    // Case 3: Uncle is BLACK and node is a LEFT child (Straight Line)
                    // Action: Recolor parent and grandparent, then rotate right on the grandparent
                    parent.setColor(false); // Black
                    grandparent.setColor(true); // Red
                    rotateRight(grandparent);
                }
            } 
            // Case B: The parent is the right child of the grandparent (Symmetric to Case A)
            else {
                RBNode<T> uncle = cast(grandparent.getLeftChild());

                if (isRed(uncle)) {
                    // Case 1: Uncle is RED.
                    parent.setColor(false); // Black
                    uncle.setColor(false); // Black
                    grandparent.setColor(true); // Red
                    node = grandparent;

                } else {
                    // Case 2: Uncle is BLACK and node is a LEFT child (Dog-leg / Triangle)
                    if (node == parent.getLeftChild()) {
                        node = parent;
                        rotateRight(node);
                        // After rotation, update references for Case 3
                        parent = cast(node.getParent());
                    }

                    // Case 3: Uncle is BLACK and node is a RIGHT child (Straight Line)
                    parent.setColor(false); // Black
                    grandparent.setColor(true); // Red
                    rotateLeft(grandparent);
                }
            }
        }
        // At the end of balancing, the root must always be BLACK
        cast(root).setColor(false);
    }

    private void balanceRemoval(RBNode<T> node, RBNode<T> parent) {
        RBNode<T> sibling;

        // Loop runs as long as there is an "extra black" weight that hasn't reached 
        // the root or hasn't found a RED node that can just absorb the black color.
        while (node != root && !isRed(node)) {

            // Case A: The extra black node is the left child 
            // (Even if node is null, parent.getLeftChild() will also be null, mapping correctly)
            if (node == parent.getLeftChild()) {
                sibling = cast(parent.getRightChild());

                // Case 1: Sibling is RED
                // Action: Recolor sibling to BLACK, parent to RED, and rotate left on parent.
                // This pushes the RED color down and converts to Case 2, 3, or 4.
                if (isRed(sibling)) {
                    sibling.setColor(false); // Black
                    parent.setColor(true); // Red
                    rotateLeft(parent);
                    sibling = cast(parent.getRightChild()); // Update sibling reference
                }

                // Treat null children from sibling as BLACK
                boolean siblingLeftIsRed = isRed(cast(sibling.getLeftChild()));
                boolean siblingRightIsRed = isRed(cast(sibling.getRightChild()));

                // Case 2: Sibling is BLACK, both of its children are BLACK
                // Action: Recolor sibling to RED, extracting a black layer. 
                // The "extra black" weight bubbles up to the parent.
                if (!siblingLeftIsRed && !siblingRightIsRed) {
                    sibling.setColor(true); // Red
                    node = parent;
                    parent = cast(node.getParent());
                } else {
                    // Case 3: Sibling is BLACK, its left child is RED and right child is BLACK (Dog-leg)
                    // Action: Recolor sibling's left child to BLACK, sibling to RED, rotate right on sibling.
                    // This creates a straight line of RED to perfectly fall into Case 4.
                    if (!siblingRightIsRed) {
                        if (sibling.getLeftChild() != null) {
                            cast(sibling.getLeftChild()).setColor(false); // Black
                        }
                        sibling.setColor(true); // Red
                        rotateRight(sibling);
                        sibling = cast(parent.getRightChild()); // Update sibling reference
                    }

                    // Case 4: Sibling is BLACK, and its right child is naturally RED
                    // Action: Sibling copies parent's color. Parent and Sibling's Right child become BLACK.
                    // Rotate left on parent perfectly resolves the black height mismatch globally.
                    sibling.setColor(parent.getColor());
                    parent.setColor(false); // Black
                    if (sibling.getRightChild() != null) {
                        cast(sibling.getRightChild()).setColor(false); // Black
                    }
                    rotateLeft(parent);
                    node = cast(root); // Subtree is balanced, break out of loop
                }
            }
            // Case B: The extra black node is the right child (Symmetric logic from Case A)
            else {
                sibling = cast(parent.getLeftChild());

                // Case 1: Sibling is RED
                if (isRed(sibling)) {
                    sibling.setColor(false); // Black
                    parent.setColor(true); // Red
                    rotateRight(parent);
                    sibling = cast(parent.getLeftChild()); // Update sibling reference
                }

                boolean siblingLeftIsRed = isRed(cast(sibling.getLeftChild()));
                boolean siblingRightIsRed = isRed(cast(sibling.getRightChild()));

                // Case 2: Sibling's children are both BLACK
                if (!siblingLeftIsRed && !siblingRightIsRed) {
                    sibling.setColor(true); // Red
                    node = parent;
                    parent = cast(node.getParent());
                } else {
                    // Case 3: Sibling's right child is RED, left is BLACK
                    if (!siblingLeftIsRed) {
                        if (sibling.getRightChild() != null) {
                            cast(sibling.getRightChild()).setColor(false); // Black
                        }
                        sibling.setColor(true); // Red
                        rotateLeft(sibling);
                        sibling = cast(parent.getLeftChild());
                    }

                    // Case 4: Sibling's left child is RED
                    sibling.setColor(parent.getColor());
                    parent.setColor(false); // Black
                    if (sibling.getLeftChild() != null) {
                        cast(sibling.getLeftChild()).setColor(false); // Black
                    }
                    rotateRight(parent);
                    node = cast(root); // Break out
                }
            }
        }

        // Finally, if the node that absorbed the blackness exists, 
        // ensure it securely absorbs the color and becomes purely BLACK.
        if (node != null) {
            node.setColor(false);
        }
    }

    // Helper methods
    private RBNode<T> cast(Node<T> node) {
        return (RBNode<T>) node;
    }

    private boolean isRed(RBNode<T> node) {
        if (node == null) {
            return false; // Null nodes are considered black
        }
        return node.getColor(); // True if red, False if black
    }

    // Print methods
    public void printTree() {
        if (root == null) {
            System.out.println("The tree is empty.");
            return;
        }

        int height = getMaxDepth(cast(root));
        List<RBNode<T>> currentLevel = new ArrayList<>();
        currentLevel.add(cast(root));

        for (int depth = 1; depth <= height; depth++) {
            int gaps = (int) Math.pow(2, height - depth) - 1;
            int between = (int) Math.pow(2, height - depth + 1) - 1;

            printSpaces(gaps);
            List<RBNode<T>> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i++) {
                RBNode<T> node = currentLevel.get(i);
                if (node != null) {
                    String color = isRed(node) ? "R" : "B";
                    System.out.print(node.getObject() + "[" + color + "]");
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

    private int getMaxDepth(RBNode<T> node) {
        if (node == null) {
            return 0; // Null nodes have depth 0
        }
        return 1 + Math.max(getMaxDepth(cast(node.getLeftChild())), getMaxDepth(cast(node.getRightChild())));
    }

    private void printSpaces(int n) {
        for (int i = 0; i < n; i++) {
            System.out.print(" ");
        }
    }
}
