/**
 * Esta clase es la encargada de generar reportes relacionados con los destinos de la agencia y generar un archivo de reporte TXT.
 * A partir de calcular los ganancias acumuladas por cada destino.
 */
package com.java.tp.agency.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.java.tp.agency.Agency;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.travels.Travel;

public class PlacesReportGen {
    
    public static List<String> getKmDestino() {
        TreeMap<String, Place> destinos = Agency.getInstancia().getDestinos();
        HashMap<String, Travel> viajes = Agency.getInstancia().getViajes();

        if (destinos == null || destinos.isEmpty()) {
            return List.of("No se pudieron cargar los destinos");
        }
        
        if (viajes == null || viajes.isEmpty()) {
            return List.of("No hay viajes cargados para evaluar destinos");
        }
        
        // HashMap para acumular km por destino
        HashMap<String, Float> kmPorDestino = new HashMap<>();
        
        // Obtener vehículos y responsables de Agency
        HashMap<String, com.java.tp.agency.vehicles.Vehicles> vehiculos = Agency.getInstancia().getVehiculos();
        HashMap<String, com.java.tp.agency.responsables.Responsable> responsables = Agency.getInstancia().getResponsables();
        
        // Recorrer todos los destinos del TreeMap
        for (String destinoId : destinos.keySet()) {
            float precioAcumulados = 0;
            
            // Recorrer todos los viajes
            for (Travel viaje : viajes.values()) {
                String viajeId = viaje.getId(); // Ej: "destino-1"
                
                // Extraer la parte del destino del ID del viaje (antes del guion)
                String destinoDelViaje = viajeId.substring(0, viajeId.lastIndexOf('-'));
                
                // Si el viaje corresponde a este destino, calcular el costo
                if (destinoDelViaje.equals(destinoId)) {
                    // Obtener los objetos necesarios
                    com.java.tp.agency.vehicles.Vehicles vehiculo = vehiculos.get(viaje.getPatVehiculo());
                    Place destino = destinos.get(viaje.getIdDestino());
                    
                    // Obtener responsables del viaje
                    HashMap<String, com.java.tp.agency.responsables.Responsable> responsablesDelViaje = new HashMap<>();
                    java.util.TreeSet<String> dnisResponsables = viaje.getPerResponsables();
                    if (dnisResponsables != null) {
                        for (String dni : dnisResponsables) {
                            com.java.tp.agency.responsables.Responsable resp = responsables.get(dni);
                            if (resp != null) {
                                responsablesDelViaje.put(dni, resp);
                            }
                        }
                    }
                    
                    if (vehiculo != null && destino != null) {
                        precioAcumulados += viaje.devuelveValorCalculado(
                            vehiculo, 
                            destino, 
                            responsablesDelViaje, 
                            viaje.getcPasajeros()
                        );
                    }
                }
            }
            
            // Almacenar el acumulado para este destino
            kmPorDestino.put(destinoId, precioAcumulados);
        }
        
        // Convertir a lista sin ordenar
        List<String> destinosFormateados = kmPorDestino.entrySet().stream()
            .map(entry -> entry.getKey() + " - " + String.format(" $%.2f", entry.getValue()))
            .collect(Collectors.toList());
        
        return destinosFormateados;
    }
    
    public static float getPrecioAcumulados() {
        HashMap<String, Travel> viajes = Agency.getInstancia().getViajes();
        TreeMap<String, Place> destinos = Agency.getInstancia().getDestinos();
        HashMap<String, com.java.tp.agency.vehicles.Vehicles> vehiculos = Agency.getInstancia().getVehiculos();
        HashMap<String, com.java.tp.agency.responsables.Responsable> responsables = Agency.getInstancia().getResponsables();
        
        if (viajes == null || viajes.isEmpty()) {
            return 0.0f;
        }
        
        float precioTotal = 0;
        for (Travel viaje : viajes.values()) {
            // Obtener los objetos necesarios
            com.java.tp.agency.vehicles.Vehicles vehiculo = vehiculos.get(viaje.getPatVehiculo());
            Place destino = destinos.get(viaje.getIdDestino());
            
            // Obtener responsables del viaje
            HashMap<String, com.java.tp.agency.responsables.Responsable> responsablesDelViaje = new HashMap<>();
            java.util.TreeSet<String> dnisResponsables = viaje.getPerResponsables();
            if (dnisResponsables != null) {
                for (String dni : dnisResponsables) {
                    com.java.tp.agency.responsables.Responsable resp = responsables.get(dni);
                    if (resp != null) {
                        responsablesDelViaje.put(dni, resp);
                    }
                }
            }
            
            // Calcular camas si es coche cama
            int cantCamas = 0;
            if (vehiculo != null && vehiculo.getCapacidad() == 32) { // Coche cama
                cantCamas = Math.min(viaje.getcPasajeros(), 26);
                int asientosComunes = viaje.getcPasajeros() - cantCamas;
                if (asientosComunes > 6) {
                    cantCamas = viaje.getcPasajeros() - 6;
                }
            }
            
            if (vehiculo != null && destino != null) {
                precioTotal += viaje.devuelveValorCalculado(
                    vehiculo, 
                    destino, 
                    responsablesDelViaje, 
                    viaje.getcPasajeros()
                );
            }
        }
        
        return precioTotal;
    }

// Clase para generar reportes de destinos
public static void generateReport() throws IOException {
    List<String> lineasReporte = getKmDestino();
        
    // Verifica si el reporte contiene el mensaje de error
    if (lineasReporte.size() == 1 && lineasReporte.get(0).equals("No se pudieron cargar los destinos")) {
        System.out.println("No se puede generar el reporte: No hay datos de destinos cargados.");
        return;
    }

    // Añadir encabezado al reporte
        lineasReporte.add(0, "--- REPORTE DE DESTINOS DE LA AGENCIA ---");
        
        // Generar nombre de archivo único con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "Reporte_Destinos_" + timestamp + ".txt";
        
        // Definir la ruta del archivo (se guardará en el directorio de ejecución del programa)
        Path rutaArchivo = Path.of("reports/" + nombreArchivo);
        
        try {
            // Escribir todas las líneas en el archivo. Esto lo crea si no existe.
            Files.write(rutaArchivo, lineasReporte);
            
            System.out.println("Reporte de destinos generado con éxito en: " + rutaArchivo.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de reporte de destinos: " + e.getMessage());
        }
    }
}