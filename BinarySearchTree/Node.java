package BinarySearchTree;

public class Node<T extends Comparable<? super T>>{
    private T o;
    private Node<T> parent;
    private Node<T> leftChild; 
    private Node<T> rightChild;

    public Node(Node<T> parent, T o) {
        this.parent = parent;
        this.o = o;
    }

    public T getObject() {
        return o;
    }

    public Node<T> getParent() {
        return parent;
    }

    public Node<T> getLeftChild() {
        return leftChild;
    }

    public Node<T> getRightChild() {
        return rightChild;
    }

     public void setElement(T o) {
        this.o = o;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public void setLeftChild(Node<T> leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(Node<T> rightChild) {
        this.rightChild = rightChild;
    }
}
