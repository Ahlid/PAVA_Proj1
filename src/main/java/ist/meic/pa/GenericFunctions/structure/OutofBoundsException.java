/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.ist.meic.pa.GenericFunctions.structure;

/**
 *
 * @author PM-Uninova
 */
public class OutofBoundsException extends RuntimeException {

	public OutofBoundsException(int r) {
		super("O rank "+r+" é inválido");
	}
    
}
