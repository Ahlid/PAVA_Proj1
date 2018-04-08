/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ist.meic.pa.GenericFunctions.structure;

/**
 *
 */
public class OutofBoundsException extends RuntimeException {

	public OutofBoundsException(int r) {
		super("O rank "+r+" é inválido");
	}
    
}
