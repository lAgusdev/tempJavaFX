package com.java.tp.agency.dataController;
import com.java.tp.agency.Agency;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.exceptions.*;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.vehicles.*;
import com.java.tp.agency.travels.*;
import java.io.*;
import java.util.*;
import jakarta.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class DataController {

    public void crearViaje(String destino, String patVeh, int pasajeros, float kmRec, TreeSet<String> res) throws SinResLargaDisException {
        Agency.getInstancia().crearViaje(destino, patVeh, pasajeros, kmRec, res);
    }

    public HashMap<String, Vehicles> VehiculosDisponibles() {
        return Agency.getInstancia().VehiculosDisponibles();
    }

    public HashMap<String, Responsable> ResponsablesDisponibles() {
        return Agency.getInstancia().ResponsablesDisponibles();
    }

    public void iniciaxml(Agency agency){
        deserealizaResponsables(agency);
        deserealizaDestinos(agency);
        deserializaVehiculos(agency);
        deserializaViajes(agency);
    }
    private void deserealizaResponsables(Agency agency) {
        HashMap<String, Responsable> res = agency.getResponsables();
        try {
            JAXBContext contexto = JAXBContext.newInstance(Responsable.class); //se instancia el contexto a partir de la clase Responsable para pasar de xml a obj
            Unmarshaller unmarshaller = contexto.createUnmarshaller(); //se crea el encargado de convertir el XML a objetos Java.
            XMLInputFactory factory = XMLInputFactory.newFactory(); //se crea el parsers/lectores de XML
            InputStream is = getClass().getResourceAsStream("/com/java/tp/data/responsables.xml"); //indica la ruta del archivo
            //caso de que no exista el archivo
            if (is == null) {
                System.out.println("Recurso no encontrado: com/java/tp/data/responsables.xml");
                return;
            }
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            //analiza errores de coherencia en el XML
            while (reader.hasNext()) { //while !eof
                if (reader.isStartElement() && reader.getLocalName().equals("responsable")) {
                    try {
                        Responsable r = (Responsable) unmarshaller.unmarshal(reader);
                        if (r.getNombre().isEmpty()) {
                            throw new ResponsableInvalidoException("Nombre Vacio");
                        }
                        if (!r.getDni().matches("[0-9]{8}")) {
                            throw new ResponsableInvalidoException("Dni Invalido");
                        }
                        if (r.getSalario() < 0) {
                            throw new ResponsableInvalidoException("Salario negativo");
                        }
                        if (res.containsKey(r.getDni())) {
                            throw new ResponsableInvalidoException("Dni repetido");
                        }
                        res.put(r.getDni(), r);
                    } catch (Exception e) {
                        System.out.println("Responsable inválido," + e.getMessage());
                    }
                }
                reader.next();
            }
            reader.close();
            System.out.println("------ Carga completa, se cargaron: " + res.size() + " Responsables------");
        } catch (Exception e) {
            System.out.println("Error general al leer XML: " + e.getMessage());
        }
    }

    private void deserealizaDestinos(Agency agency) {
        TreeMap<String, Place> des = agency.getDestinos();
        try {
            JAXBContext contexto = JAXBContext.newInstance(Place.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            XMLInputFactory factory = XMLInputFactory.newFactory();
            InputStream is = getClass().getResourceAsStream("/com/java/tp/data/destinos.xml");
            if (is == null) {
                System.out.println("Recurso no encontrado: com/java/tp/data/destinos.xml");
                return;
            }
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            while (reader.hasNext()) { //while !eof
                if (reader.isStartElement() && reader.getLocalName().equals("destino")) {
                    try {
                        Place d = (Place) unmarshaller.unmarshal(reader);
                        if (d.getId().isEmpty()) {
                            throw new DestinoInvalidoException("Id Vacio");
                        }
                        if (d.getKm() <= 0) {
                            throw new DestinoInvalidoException("Destino km negativo");
                        }

                        if (des.containsKey(d.getId())) {
                            throw new DestinoInvalidoException("Id repetido");
                        }
                        des.put(d.getId(), d);
                    } catch (Exception e) {
                        System.out.println("Destino inválido, " + e.getMessage());
                    }
                }
                reader.next();
            }
            reader.close();
            System.out.println("------ Carga completa, se cargaron: " + des.size() + " Destinos ------");
        } catch (Exception e) {
            System.out.println("Error general al leer XML: " + e.getMessage());
        }
    }

    private void deserializaVehiculos(Agency agency) {
        HashMap<String, Vehicles> veh = agency.getVehiculos();
        try {
            JAXBContext contexto = JAXBContext.newInstance(Car.class, MiniBus.class, BusSC.class, BusCC.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            XMLInputFactory factory = XMLInputFactory.newFactory();
            InputStream is = getClass().getResourceAsStream("/com/java/tp/data/vehiculos.xml");
            if (is == null) {
                System.out.println("Recurso no encontrado: com/java/tp/data/vehiculos.xml");
                return;
            }
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            while (reader.hasNext()) { //while !eof
                if (reader.isStartElement() && (reader.getLocalName().equals("auto")||reader.getLocalName().equals("combi")||reader.getLocalName().equals("colectivoSC")||reader.getLocalName().equals("colectivoCC"))) {
                    try {
                        String tag = reader.getLocalName();
                        Vehicles v = null;
                        switch (tag) {
                            case "auto": v = (Car) unmarshaller.unmarshal(reader);break;
                            case "combi": v = (MiniBus) unmarshaller.unmarshal(reader);break;
                            case "colectivoSC": v = (BusSC) unmarshaller.unmarshal(reader);break;
                            case "colectivoCC": v = (BusCC) unmarshaller.unmarshal(reader);break;
                        }
                            if (v.getPatente().isEmpty()) {
                                throw new VehiculoInvalidoException("Patente vacía");
                            }
                            if (v.getVelPerH() <= 0) {
                                throw new VehiculoInvalidoException("Velocidad negativa o nula");
                            }
                            if (veh.containsKey(v.getPatente())) {
                                throw new VehiculoInvalidoException("Patente repetida");
                            }
                        switch (tag) {
                            case "auto" : 
                                Car a = (Car) v;
                                if (a.getValKm() <= 0) {
                                    throw new VehiculoInvalidoException("Auto con valor por Km inválido");
                                }
                                if(a.getValBase()<=0){
                                    throw new VehiculoInvalidoException("Auto con valor base inválido");
                                }
                            break;
                            case "combi" :
                                MiniBus c = (MiniBus) v;
                                if (c.getValPasajero() <= 0) {
                                    throw new VehiculoInvalidoException("Combi con valor por pasajero inválida");
                                }
                                if (c.getValBase()<=0){
                                    throw new VehiculoInvalidoException("Combi con valor base invalida");
                                }
                            break;
                            case "colectivoSC" :
                                BusSC sc= (BusSC) v;
                                if (sc.getValPasajero()<= 0) {
                                    throw new VehiculoInvalidoException("Colectivo con valor por Pasajero inválido");
                                }
                            break;
                            case "colectivoCC" :
                                BusCC cc=(BusCC) v;
                                if(cc.getValPasajeroAsiento()<=0){
                                    throw new VehiculoInvalidoException("Colectivo con valor por asiento invalido");
                                }
                                if (cc.getValPasajeroCama()<=0){
                                    throw new VehiculoInvalidoException("Colectivo con valor por cama invalido");
                                }
                            break;

                        }
                        veh.put(v.getPatente(), v);
                    }catch (Exception e) {
                        System.out.println("vehiculo inválido, " + e.getMessage());
                    }

                }else{
                    reader.next();
                }
            }
            reader.close();
            System.out.println("------ Carga completa, se cargaron: " + veh.size() + " vehiculos ------");
        } catch (Exception e) {
            System.out.println("Error general al leer XML: " + e.getMessage());
        }
    }

    public void deserializaViajes(Agency agency){
        HashMap<String, Travel> via = agency.getViajes();
        try {
            JAXBContext contexto = JAXBContext.newInstance(LongDis.class,ShortDis.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            XMLInputFactory factory = XMLInputFactory.newFactory();
            InputStream is = getClass().getResourceAsStream("/com/java/tp/data/viajes.xml");
            if (is == null) {
                System.out.println("Recurso no encontrado: com/java/tp/data/viajes.xml");
                return;
            }
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            while (reader.hasNext()) { //while !eof
                if (reader.isStartElement() && (reader.getLocalName().equals("largaDis")|| reader.getLocalName().equals("cortaDis"))){
                    String tag = reader.getLocalName();
                    Travel v = null;
                    switch (tag){
                        case "largaDis":v = (LongDis) unmarshaller.unmarshal(reader);break;
                        case "cortaDis":v = (ShortDis) unmarshaller.unmarshal(reader);break;
                    }
                    if (v == null) {
                        // Skip malformed or unmarshal-failed entries
                        continue;
                    }
                    try {
                        // Si el viaje no tiene ID (XML viejo), crear uno nuevo
                        if (v.getId() == null || v.getId().isEmpty()) {
                            v.setId(agency.creaIdViaje(v.getIdDestino()));
                        } else {
                            // Si tiene ID, actualizar el contador para que futuros IDs no se repitan
                            agency.actualizarContadorDesdeId(v.getId());
                        }
                        
                        v.setEstado(v.actualizaEstadoViaje(v.getIdDestino(), agency.getDestinos()));
                        via.put(v.getId(), v);
                        
                        // Actualizar estado de vehículo si el viaje está activo o en curso
                        if (v.getEstado() == com.java.tp.agency.enums.TravelStatus.PENDIENTE || 
                            v.getEstado() == com.java.tp.agency.enums.TravelStatus.EN_CURSO) {
                            com.java.tp.agency.vehicles.Vehicles vehiculo = agency.getVehiculos().get(v.getPatVehiculo());
                            if (vehiculo != null) {
                                vehiculo.setEstado(com.java.tp.agency.enums.Unoccupied.OCUPADO);
                            }
                            
                            // Actualizar estado de responsables si el viaje está activo o en curso
                            java.util.TreeSet<String> responsablesDni = v.getPerResponsables();
                            if (responsablesDni != null && !responsablesDni.isEmpty()) {
                                for (String dni : responsablesDni) {
                                    com.java.tp.agency.responsables.Responsable responsable = agency.getResponsables().get(dni);
                                    if (responsable != null) {
                                        responsable.setEstado(com.java.tp.agency.enums.Unoccupied.OCUPADO);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Viaje inválido al asignar id/estado: " + e.getMessage());
                    }
                }
                reader.next();
            }
            reader.close();
            System.out.println("------ Carga completa, se cargaron: " + via.size() + " Viajes ------");
        }catch (Exception e){
            System.out.println("Error general al leer XML:" + e.getMessage());
        }
    }
    public void muestraviajes(){
        Agency.getInstancia().muestraViajes();
    }
    public void serializaViajes() {
        List<Travel> listaViajes = new ArrayList<>(Agency.getInstancia().getViajes().values());
        try {
            JAXBContext contexto = JAXBContext.newInstance(Travel.class, LongDis.class, ShortDis.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            sw.write("<viajes>\n");
            for (Travel viaje : listaViajes) {
                StringWriter viajeWriter = new StringWriter();
                marshaller.marshal(viaje, viajeWriter);
                String viajeXml = viajeWriter.toString();
                int idx = viajeXml.indexOf("?>");
                String contenido = (idx != -1) ? viajeXml.substring(idx + 2).trim() : viajeXml.trim();
                sw.write("    " + contenido + "\n");
            }
            sw.write("</viajes>");
            
            // Obtener la ruta correcta al archivo de recursos
            String userDir = System.getProperty("user.dir");
            File archivo = new File(userDir, "demo/src/main/resources/com/java/tp/data/viajes.xml");
            
            // Si estamos en el directorio demo, ajustar la ruta
            if (userDir.endsWith("demo")) {
                archivo = new File(userDir, "src/main/resources/com/java/tp/data/viajes.xml");
            }
            
            archivo.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(archivo)) {
                fw.write(sw.toString());
            }
            System.out.println("------ Viajes serializados: " + listaViajes.size() + " viajes ------");
            System.out.println("Guardado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error al serializar viajes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}