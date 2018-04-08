/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.ist.meic.pa.GenericFunctions.structure;

/**
 *
 */
public class DNode<E> {
    
    private E element;
    private DNode<E> next;
    private DNode<E> previous;
    
    public DNode(E elem,DNode<E> prev, DNode<E> next){
        this.element=elem;
        this.next=next;
        this.previous=prev;
    }

    public DNode(E elem) {
        this.element = elem;
        this.next=null;
        this.previous=null;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public DNode<E> getNext() {
        return next;
    }

    public void setNext(DNode<E> next) {
        this.next = next;
    }

    public DNode<E> getPrevious() {
        return previous;
    }

    public void setPrevious(DNode<E> previous) {
        this.previous = previous;
    }
    
    
    
    
}
