package com.java.tp.agency.travels;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.Agency;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.vehicles.Vehicles;
import com.java.tp.agency.enums.TravelStatus;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.*;
@XmlSeeAlso({LongDis.class,ShortDis.class})
public  abstract class Travel {
    private String id;//nombre; //formato "Ciudad -4
    private String patVehiculo;
    private String idDestino; //se cambio para no duplicar los datos
    private int cPasajeros; //cantidad de pasajeros a bordo del viaje
    private TravelStatus estado;
    private float kmRec;
    public  Travel(){ //constructor predef para json
    }

    public Travel(String inId,String inpatVehiculo ,String inDestino, int inCPasajeros,float inkmRec){
        id = inId;
        patVehiculo=inpatVehiculo;
        idDestino = inDestino;
        cPasajeros = inCPasajeros;
        kmRec = inkmRec;
        estado=actualizaEstadoViaje(inDestino);
    }
    //getters
    @XmlElement
    public float getKmRec() {return kmRec;}
    @XmlTransient
    public String getId() {return id;}
    @XmlElement
    public String getPatVehiculo() {return patVehiculo;}
    @XmlElement
    public int getcPasajeros() {return cPasajeros;}
    @XmlElement
    public String getIdDestino() {return idDestino;}
    @XmlTransient
    public TravelStatus getEstado() {return estado;}
    //
    //Setters //los setters los usa el jaxb
    public void setId(String id) {this.id = id;}
    public void setcPasajeros(int cPasajeros) {this.cPasajeros = cPasajeros;}
    public void setEstado(TravelStatus estado) {this.estado = estado;}
    public void setIdDestino(String idDestino) {this.idDestino = idDestino;}
    public void setKmRec(float kmRec) {this.kmRec = kmRec;}
    public void setPatVehiculo(String patVehiculo) {this.patVehiculo = patVehiculo;}

    // Sobrecarga para usar cuando Agency ya está inicializado
    public TravelStatus actualizaEstadoViaje(String idDestino){
        TreeMap<String,Place> aux= Agency.getInstancia().getDestinos();
        return actualizaEstadoViaje(idDestino, aux);
    }
    
    // Versión que recibe destinos como parámetro (para usar durante deserialización)
    public TravelStatus actualizaEstadoViaje(String idDestino, TreeMap<String,Place> destinos){
        Place destinoaux=destinos.get(idDestino);
        if(destinoaux.getKm()== kmRec){
            return TravelStatus.FINALIZADO;
        } else if (kmRec==0) {
            return TravelStatus.PENDIENTE;
        }else {
            return TravelStatus.EN_CURSO;
        }
    }

    public  TreeSet<String> getPerResponsables() {// Devuelve un TreeSet vacío
        return new TreeSet<>();
    }



    public abstract float devuelveValorCalculado(Vehicles vehiculo, Place destino, HashMap<String,Responsable> responsableABordo, int cantPas, int cantCamas);
}
