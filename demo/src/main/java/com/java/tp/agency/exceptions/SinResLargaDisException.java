/**
 * Excepción usada cuando hay una viaje larga distancia que no tiene al menos un responsable a bordo.
 */
package com.java.tp.agency.exceptions;

public class SinResLargaDisException extends RuntimeException {
    public SinResLargaDisException(String message) {
        super(message);
    }
}
