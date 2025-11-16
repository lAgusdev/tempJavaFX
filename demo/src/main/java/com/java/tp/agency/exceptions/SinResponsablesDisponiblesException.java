/**
 * Excepción usada cuando no hay ni un responsable disponible.
 */
package com.java.tp.agency.exceptions;

public class SinResponsablesDisponiblesException extends RuntimeException {
    public SinResponsablesDisponiblesException(String message) {
        super(message);
    }
}
