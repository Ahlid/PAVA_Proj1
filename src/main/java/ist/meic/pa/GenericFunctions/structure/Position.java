package main.java.ist.meic.pa.GenericFunctions.structure;
/**
 * An interface for a position, which is a holder object storing a
 * single element.
 */
//begin#fragment All
public interface Position<E> {
  /** Return the element stored at this position. */
  E element();
}
//end#fragment All
