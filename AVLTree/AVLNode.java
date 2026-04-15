package AVLTree;

import BinarySearchTree.Node;

public class AVLNode<T extends Comparable<? super T>> extends Node<T> {
    public int balanceFactor;
    public int height;

    public AVLNode(T data, Node<T> parent) {
        super(parent, data);
        this.height = 1; // New nodes are initially added as leaf nodes
        this.balanceFactor = 0; // Balance factor is initially 0
    }
}