package BinarySearchTree;

public class Teste {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        System.out.println("Inserindo elementos: 50, 30, 20, 40, 70, 60, 80");
        bst.insert(50);
        bst.insert(30);
        bst.insert(20);
        bst.insert(40);
        bst.insert(70);
        bst.insert(60);
        bst.insert(80);

        System.out.println("\nEstrutura da Árvore:");
        bst.display();

        System.out.println("\nBuscando pelo número 40:");
        Node<Integer> found = bst.search(40);
        if (found != null) {
            System.out.println("Encontrado: " + found.getObject());
        } else {
            System.out.println("Não encontrado.");
        }

        System.out.println("\nRemovendo o 20 (Nó folha):");
        bst.remove(20);
        bst.display();

        System.out.println("\nRemovendo o 30 (Nó com um filho):");
        bst.remove(30);
        bst.display();

        System.out.println("\nRemovendo o 50 (Nó com dois filhos):");
        bst.remove(50);
        bst.display();

        System.out.println("\nInvertendo a árvore (Mirror):");
        bst.mirror();
        bst.display();
    }
}
