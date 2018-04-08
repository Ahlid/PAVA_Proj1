package main.java.ist.meic.pa.GenericFunctions.structure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 */

    public interface List<E> extends Iterable<E> {
	public int size();
	public boolean isEmpty();
	public E get(int r) throws 			OutofBoundsException;
	public void add(int r, E elem) throws 	OutofBoundsException;
	public E  remove(int r) throws OutofBoundsException;
	public E set(int r, E elem) throws 	OutofBoundsException;


}
