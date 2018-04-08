package main.java.ist.meic.pa.GenericFunctions.structure;
/**
 * Runtime exception thrown when one tries to access the root of an
 * empty tree.
 */

public class EmptyTreeException extends RuntimeException {  
  public EmptyTreeException(String err) {
    super(err);
  }
}
