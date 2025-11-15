package com.java.tp.agency.travels;

import com.java.tp.agency.places.Place;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.vehicles.Vehicles;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.HashMap;
@XmlRootElement(name= "cortaDis")
public class ShortDis extends Travel{

    public ShortDis(){
        super();
    }
    public ShortDis(String id, String patVehiculo, String destino, int cPasajeros, float kmRec){
        super(id,patVehiculo,destino,cPasajeros,kmRec);
        //public Viaje(String inId,String inpatVehiculo ,String inDestino, int inCPasajeros,float inkmRec)
    }
    @Override
    public float  devuelveValorCalculado(Vehicles vehiculo,Place destino,HashMap<String,Responsable> responsableABordo, int cantPas){
        return vehiculo.calculaCosto(destino.getKm(),cantPas);
    }
}