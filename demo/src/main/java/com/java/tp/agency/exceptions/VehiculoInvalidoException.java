/**
 * Excepción usada cuando hay una varaibale de vehiculo inválida.
 */
package com.java.tp.agency.exceptions;

public class VehiculoInvalidoException extends RuntimeException {
    public VehiculoInvalidoException(String message) {
        super(message);
    }
}
