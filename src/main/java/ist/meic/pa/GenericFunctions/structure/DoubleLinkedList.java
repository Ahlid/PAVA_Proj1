/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ist.meic.pa.GenericFunctions.structure;

import java.util.Iterator;

/**
 *
 */
public class DoubleLinkedList<E> implements List<E> {

    private DNode<E> header;
    private DNode<E> tailer;
    private int size;

    public DoubleLinkedList() {
        this.header = new DNode<>(null, null, null);
        this.tailer = new DNode<>(null, header, null);
        this.header.setNext(tailer);
        this.size = 0;
    }

    private DNode<E> nodeAtRank(int r) throws OutofBoundsException {

        DNode<E> nodeAux = header.getNext();

        for (int i = 0; i < r; i++) {
            nodeAux = nodeAux.getNext();

        }
        return nodeAux;

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E get(int r) throws OutofBoundsException {
        if (r < 0 || r >= size) {
            throw new OutofBoundsException(r);
        }
        return nodeAtRank(r).getElement();

    }

    @Override
    public void add(int r, E elem) throws OutofBoundsException {
        if (r < 0 || r > size) {
            throw new OutofBoundsException(r);
        }
        DNode<E> aux = nodeAtRank(r);
        DNode<E> prev = aux.getPrevious();
        DNode<E> newNode = new DNode<>(elem, prev, aux);
        aux.setPrevious(newNode);
        prev.setNext(newNode);
        size++;
    }

    @Override
    public E remove(int r) throws OutofBoundsException {

        if (r < 0 || r >= size) {
            throw new OutofBoundsException(r);
        }
        DNode<E> aux = nodeAtRank(r);
        E elem = aux.getElement();
        DNode<E> prev = aux.getPrevious();
        DNode<E> next = aux.getNext();
        prev.setNext(next);
        next.setPrevious(prev);
        size--;
        return elem;
    }

    @Override
    public E set(int r, E elem) throws OutofBoundsException {

        if (r < 0 || r >= size) {
            throw new OutofBoundsException(r);
        }
        DNode<E> nodeAux = nodeAtRank(r);
        E elemAux = nodeAux.getElement();
        nodeAux.setElement(elem);
        return elemAux;

    }

    // metodos recursivos aula de metodos recursivos
    public boolean exist(E elem) {

        return existR(elem, header.getNext());
    }

    private boolean existR(E elem, DNode pos) {
        if (pos == tailer) {
            return false;
        }
        if (elem.equals(pos.getElement())) {
            return true;
        }
        return existR(elem, pos.getNext());
    }

    private void imprimiR(DNode pos) {
        if (pos != tailer) {
            System.out.print(pos.getElement() + " " + ",");
            imprimiR(pos.getNext());
        }
    }

    public void imprime() {
        imprimiR(header.getNext());
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorList();
    }

    private class IteratorList implements Iterator<E> {

        DNode<E> cursor;

        public IteratorList() {
            cursor = header.getNext();
        }

        @Override
        public boolean hasNext() {
            return cursor != tailer;
        }

        @Override
        public E next() {
            E elem = cursor.getElement();
            cursor = cursor.getNext();
            return elem;
        }

        @Override
        public void remove() {
            E elem = cursor.getElement();
            DNode<E> prev = cursor.getPrevious();
            DNode<E> next = cursor.getNext();
            prev.setNext(next);
            next.setPrevious(prev);
            size--;
        }
    }

}
