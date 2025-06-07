/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package javafxappaerolinea.exception;

/**
 *
 * @author Dell
 */
public class CapacityExceededException extends Exception {

    /**
     * Constructs an instance of <code>CapacityExceededException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CapacityExceededException(String msg) {
        super(msg);
    }
}
