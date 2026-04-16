package RedBlackTree;

import BinarySearchTree.Node;

public class RBNode<T extends Comparable<? super T>> extends Node<T> {
    public boolean isRed;

    public RBNode(T data, Node<T> parent) {
        super(parent, data);
        this.isRed = true; // New nodes are initially red
    }

    public boolean getColor() {
        return isRed;
    }

    public void setColor(boolean color) {
        this.isRed = color;
    }
}