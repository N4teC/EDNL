package AVLTree;

import java.util.Scanner;

public class TesteAVL {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AVLTree<Integer> tree = new AVLTree<>();

        System.out.println("=== Teste da Árvore AVL O(log n) com Fórmulas FB ===");
        System.out.println("Inserindo valores iniciais para reproduzir exemplo: 10, 5, 15, 2, 8, 22");

        int[] initialValues = {10, 5, 15, 2, 8, 22};
        for (int val : initialValues) {
            tree.insert(val);
        }

        System.out.println("\nEstado da Árvore após inserções iniciais:");
        tree.printTree();

        while (true) {
            System.out.println("\nMenu Interativo:");
            System.out.println("1. Inserir");
            System.out.println("2. Remover");
            System.out.println("3. Buscar");
            System.out.println("4. Mostrar Árvore");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opcao: ");

            String input = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!");
                continue;
            }

            if (opcao == 5) {
                System.out.println("Saindo...");
                break;
            }

            switch (opcao) {
                case 1:
                    System.out.print("Digite o valor para INSERIR: ");
                    int valInsert = Integer.parseInt(scanner.nextLine());
                    tree.insert(valInsert);
                    System.out.println("Valor " + valInsert + " inserido.");
                    tree.printTree();
                    break;
                case 2:
                    System.out.print("Digite o valor para REMOVER: ");
                    int valRemove = Integer.parseInt(scanner.nextLine());
                    tree.remove(valRemove);
                    System.out.println("Comando de remoção para " + valRemove + " executado.");
                    tree.printTree();
                    break;
                case 3:
                    System.out.print("Digite o valor para BUSCAR: ");
                    int valSearch = Integer.parseInt(scanner.nextLine());
                    if (tree.search(valSearch) != null) {
                        System.out.println("Valor " + valSearch + " ENCONTRADO na árvore.");
                    } else {
                        System.out.println("Valor " + valSearch + " NÃO ENCONTRADO.");
                    }
                    break;
                case 4:
                    System.out.println("\nEstrutura Atual da Árvore:");
                    tree.printTree();
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
        
        scanner.close();
    }
}
