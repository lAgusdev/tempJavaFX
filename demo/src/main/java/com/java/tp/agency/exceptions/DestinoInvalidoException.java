/**
 * Excepción usada cuando hay una varaibale de destino inválida.
 */
package com.java.tp.agency.exceptions;

public class DestinoInvalidoException extends RuntimeException {
    public DestinoInvalidoException(String message) {
        super(message);
    }
}
