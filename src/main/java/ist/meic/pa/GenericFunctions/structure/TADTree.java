/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.ist.meic.pa.GenericFunctions.structure;

/**
 * @author Utilizador
 */
public class TADTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LinkedTree<String> myTree = new LinkedTree("Animal");
        Position<String> root = myTree.root();
        Position<String> mamifero = myTree.insert(root, "Mamifero");
        Position<String> ave = myTree.insert(root, "Ave");
        myTree.insert(mamifero, "Cao");
        Position<String> s12 = myTree.insert(mamifero, "Gato");
        myTree.insert(mamifero, "Vaca");
        myTree.insert(ave, "Papagaio");
        Position<String> aguia = myTree.insert(ave, "Aguia");
        myTree.insert(aguia, "Aguia Real");
        System.out.println("E externo " + myTree.isExternal(aguia));
        System.out.println("");
        System.out.println("NUMERO DE ELEMENTOS " + myTree.size());
        System.out.println("ALTURA " + myTree.heightS());
        System.out.println("TREE " + myTree);
        int count = 1;
        for (Position<String> pos : myTree.positions())
            System.out.println(count++ + " - " + pos.element());


    }
//    
}
