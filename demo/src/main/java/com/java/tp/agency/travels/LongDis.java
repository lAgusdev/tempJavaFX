package com.java.tp.agency.travels;

import com.java.tp.agency.Agency;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.exceptions.CamasLargaDisException;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.vehicles.Vehicles;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.HashMap;
import java.util.TreeSet;

@XmlRootElement(name = "largaDis")
public class LongDis extends Travel{

    private TreeSet<String> dnisPerResponsables; //uso treeset para buscar con dni

    public LongDis(){
        super();
    }

    public LongDis(String id,String patVehiculo, String destino, int cPasajeros, float kmRec, TreeSet<String> responsables){
        super(id,patVehiculo,destino,cPasajeros,kmRec);
        dnisPerResponsables=responsables;
        for (String dni : dnisPerResponsables) {
            Agency.getInstancia().ActualizaResKm(dni, kmRec);
        }
    }
    //Getter
    @XmlElement
    public TreeSet<String> getDnisPerResponsables() {return dnisPerResponsables;}
    //
    //Setters
    public void setDnisPerResponsables(TreeSet<String> dnisPerResponsables) {this.dnisPerResponsables = dnisPerResponsables;}
    //
    public float calculaSueldos(HashMap<String,Responsable> responsables){
        float total=0;
        for (String dni: dnisPerResponsables){
            Responsable aux = responsables.get(dni);
            if (aux != null) {
                total+=aux.getSalario();
            }
        }
        return total;
    }
    @Override
    public float devuelveValorCalculado(Vehicles vehiculo, Place destino, HashMap<String,Responsable> responsableABordo, int cantPas, int cantCamas){
        if (cantPas-cantCamas > 6){
            throw new CamasLargaDisException("No hay tantos asientos sin cama disponibles");
        }
        // Usar responsableABordo para calcular sueldos basados en los responsables seleccionados
        float sueldosResponsables = 0;
        for (Responsable responsable : responsableABordo.values()) {
            sueldosResponsables += responsable.getSalario();
        }
        return vehiculo.calculaCosto(destino.getKm(),cantPas,cantCamas) + sueldosResponsables;
    }

    public TreeSet<String> getPerResponsables() {
        return dnisPerResponsables;
    }
}
