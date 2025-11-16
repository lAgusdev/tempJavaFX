/**
 * Excepción usada cuando no hay ni un vehiculo disponible.
 */
package com.java.tp.agency.exceptions;

public class SinVehiculosDisponiblesException extends RuntimeException {
    public SinVehiculosDisponiblesException(String message) {
        super(message);
    }
}
