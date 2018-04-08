package main.java.ist.meic.pa.GenericFunctions.structure;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Utilizador
 * @param
 */
public class LinkedTree<E> implements Tree<E> {

    private int size;
    private TreeNode root;

    public LinkedTree() {
        this.size = 0;
    }

    public LinkedTree(E root) {
        this.root = new TreeNode(root);
        this.size = 1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E replace(Position<E> v, E e) throws InvalidPositionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Position<E> root() throws EmptyTreeException {
        return root;

    }

    @Override
    public Position<E> parent(Position<E> v) throws InvalidPositionException, BoundaryViolationException {
        TreeNode node = checkPosition(v);
        return node.parent;

    }

    @Override
    public Iterable<Position<E>> children(Position<E> v) throws InvalidPositionException {
        TreeNode node = checkPosition(v);
        return node.children;
    }

    @Override
    public boolean isInternal(Position<E> v) throws InvalidPositionException {
        TreeNode aux = checkPosition(v);
        return !aux.children.isEmpty() && aux != this.root;
    }

    @Override
    public boolean isExternal(Position<E> v) throws InvalidPositionException {
        TreeNode aux = checkPosition(v);
        return aux.children.isEmpty();
    }

    @Override
    public boolean isRoot(Position<E> v) throws InvalidPositionException {
        TreeNode aux = checkPosition(v);
        return this.root == aux;

    }

    @Override
    public Position<E> insert(Position<E> parent, E elem) throws InvalidPositionException {
        TreeNode aux = checkPosition(parent);
        size++;
        return aux.addChild(elem);

    }

    @Override
    public E remove(Position<E> position) throws InvalidPositionException {
        TreeNode aux = checkPosition(position);
        E e = aux.element;
        aux.removeChild(aux);
        size--;
        return e;
    }

    private TreeNode checkPosition(Position<E> v) {
        if (v == null) {
            throw new InvalidPositionException();
        }

        try {
            TreeNode treeNode = (TreeNode) v;
            return treeNode;
        } catch (ClassCastException e) {
            throw new InvalidPositionException();
        }

    }

    @Override
    public Iterable<Position<E>> positions() {
        DoubleLinkedList<Position<E>> lista = new DoubleLinkedList<>();
        if (!isEmpty()) {
            positions(root, lista);
        }
        return lista;
    }

    private void positions(Position<E> v, DoubleLinkedList<Position<E>> lista) {

        for (Position<E> w : children(v)) {
            positions(w, lista);
        }
        lista.add(lista.size(), v); // visit (v)
    }

    public int heightS() {
        int h = 0;
        if (!isEmpty()) {
            h = height(root, 1);
        }

        return h;
    }

    private int height(Position<E> v, int nivel) {
        if (isExternal(v)) {
            return nivel;
        }
        int current = nivel;
        for (Position<E> w : children(v)) {
            current = Math.max(current, height(w, nivel + 1));
        }
        return current;
    }

    private int height(Position<E> v) {
        if (isExternal(v)) {
            return 1;
        }
        int maximo = 0;
        for (Position<E> w : children(v)) {
            int h = height(w);

            if (maximo < h) {
                maximo = h;
            }

        }

        return 1 + maximo;

    }

    public int height() {
        int h = 0;
        if (!isEmpty()) {
            h = height(root);
        }

        return h;
    }

//    private int height(Position<E> v) {
//        if (isExternal(v)) {
//            return 1;
//        }
//        int maximo = 0;
//        for (Position<E> w : children(v)) {
//            int h = height(w);
//
//            if (maximo < h) {
//                maximo = h;
//            }
//
//        }
//
//        return 1 + maximo;
//
//    }
    public String toString() {
        String str = "";
        if (!isEmpty()) {
            str = toStringPreOrder(root);
        }

        return str;
    }

    private String toStringPreOrder(Position<E> v) {
        String str = v.element().toString(); // visit (v)
        for (Position<E> w : children(v)) {
            str += "," + toStringPreOrder(w);
        }
        return str;
    }

    private class TreeNode implements Position<E> {

        private E element;  // element stored at this node
        private TreeNode parent;  // adjacent node
        private List<Position<E>> children;  // children nodes

        public TreeNode(E element) {
            this.element = element;
            parent = null;
            children = new DoubleLinkedList<>();
        }

        public TreeNode(E element, TreeNode parent,
                List<Position<E>> children) {
            this.element = element;
            this.parent = parent;
            this.children = children;
        }

        public E element() {
            return element;
        }

        TreeNode addChild(E elem) {
            TreeNode node = new TreeNode(elem, this, new DoubleLinkedList<Position<E>>());
            children.add(children.size(), node);
            return node;
        }

        void removeChild(Position p) {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == p) {
                    children.remove(i);
                    return;
                }

            }
        }

    }

}
