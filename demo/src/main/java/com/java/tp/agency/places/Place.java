package com.java.tp.agency.places;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "destino") // Necesario para JAXB
public class Place {
    private String id;
    private float km;

    public Place() {
        // JAXB lo usa para crear la instancia antes de llamar a los setters.
    }

    @XmlElement
    public float getKm() {
        return km;
    }


    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String inid) {
        id = inid;
    }
    public void setKm(float inkm) {
        km = inkm;
    }
}
