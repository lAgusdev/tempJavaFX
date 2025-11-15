package com.java.tp.agency.reports;
import com.java.tp.agency.Agency;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.travels.Travel;
import com.java.tp.agency.vehicles.Vehicles;
import com.java.tp.agency.responsables.Responsable;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VehiclesReportGen {
    
    public static List<String> getVehiculosFormateados() {
        HashMap<String, Travel> viajes = Agency.getInstancia().getViajes();
        HashMap<String, Vehicles> vehiculos = Agency.getInstancia().getVehiculos();
        TreeMap<String, Place> destinos = Agency.getInstancia().getDestinos();
        HashMap<String, Responsable> responsables = Agency.getInstancia().getResponsables();
        
        if (viajes == null || viajes.isEmpty() || vehiculos == null || vehiculos.isEmpty()) {
            return List.of("No se pudieron cargar los vehículos");
        }
        
        float acumBusCC = 0, acumBusSC = 0, acumMiniBus = 0, acumCar = 0;
        
        for (Travel viaje : viajes.values()) {
            Vehicles vehiculo = vehiculos.get(viaje.getPatVehiculo());
            Place destino = destinos.get(viaje.getIdDestino());
            
            if (vehiculo == null || destino == null) {
                continue;
            }
            
            // Obtener responsables del viaje
            HashMap<String, Responsable> responsablesDelViaje = new HashMap<>();
            java.util.TreeSet<String> dnisResponsables = viaje.getPerResponsables();
            if (dnisResponsables != null) {
                for (String dni : dnisResponsables) {
                    Responsable resp = responsables.get(dni);
                    if (resp != null) {
                        responsablesDelViaje.put(dni, resp);
                    }
                }
            }
            
            // Calcular camas si es coche cama
            int cantCamas = 0;
            if (vehiculo.getCapacidad() == 32) { // Coche cama
                cantCamas = Math.min(viaje.getcPasajeros(), 26);
                int asientosComunes = viaje.getcPasajeros() - cantCamas;
                if (asientosComunes > 6) {
                    cantCamas = viaje.getcPasajeros() - 6;
                }
            }
            
            // Calcular el costo del viaje
            float costoViaje = viaje.devuelveValorCalculado(
                vehiculo,
                destino,
                responsablesDelViaje,
                viaje.getcPasajeros()
            );
            
            // Acumular por tipo de vehículo
            int capacidad = vehiculo.getCapacidad();
            switch (capacidad) {
                case 32:
                    acumBusCC += costoViaje;
                    break;
                case 40:
                    acumBusSC += costoViaje;
                    break;
                case 16:
                    acumMiniBus += costoViaje;
                    break;
                case 4:
                    acumCar += costoViaje;
                    break;
            }
        }
        
        List<String> vehicleReport = new ArrayList<>();
        vehicleReport.add("Auto: " + String.format("$%.2f", acumCar));
        vehicleReport.add("Combi: " + String.format("$%.2f", acumMiniBus));
        vehicleReport.add("Colectivo semi-cama: " + String.format("$%.2f", acumBusSC));
        vehicleReport.add("Colectivo coche-cama: " + String.format("$%.2f", acumBusCC));
        
        return vehicleReport;
    }
    
    public static float getPrecioTotalVehiculos() {
        HashMap<String, Travel> viajes = Agency.getInstancia().getViajes();
        HashMap<String, Vehicles> vehiculos = Agency.getInstancia().getVehiculos();
        TreeMap<String, Place> destinos = Agency.getInstancia().getDestinos();
        HashMap<String, Responsable> responsables = Agency.getInstancia().getResponsables();
        
        if (viajes == null || viajes.isEmpty()) {
            return 0.0f;
        }
        
        float precioTotal = 0;
        
        for (Travel viaje : viajes.values()) {
            Vehicles vehiculo = vehiculos.get(viaje.getPatVehiculo());
            Place destino = destinos.get(viaje.getIdDestino());
            
            if (vehiculo == null || destino == null) {
                continue;
            }
            
            // Obtener responsables del viaje
            HashMap<String, Responsable> responsablesDelViaje = new HashMap<>();
            java.util.TreeSet<String> dnisResponsables = viaje.getPerResponsables();
            if (dnisResponsables != null) {
                for (String dni : dnisResponsables) {
                    Responsable resp = responsables.get(dni);
                    if (resp != null) {
                        responsablesDelViaje.put(dni, resp);
                    }
                }
            }
            
            // Calcular camas si es coche cama
            int cantCamas = 0;
            if (vehiculo.getCapacidad() == 32) {
                cantCamas = Math.min(viaje.getcPasajeros(), 26);
                int asientosComunes = viaje.getcPasajeros() - cantCamas;
                if (asientosComunes > 6) {
                    cantCamas = viaje.getcPasajeros() - 6;
                }
            }
            
            precioTotal += viaje.devuelveValorCalculado(
                vehiculo,
                destino,
                responsablesDelViaje,
                viaje.getcPasajeros()            );
        }
        
        return precioTotal;
    }





    public static void generateReport() throws IOException {
        List<String> lineasReporte = getVehiculosFormateados();
        
        // Si la única línea es el mensaje de error, no se genera el archivo.
        if (lineasReporte.size() == 1 && lineasReporte.get(0).equals("No hay vehículos cargados")) {
            System.out.println("No se puede generar el reporte: No hay datos de vehículos cargados.");
            return;
        }

        // Añadir encabezado al reporte
        lineasReporte.add(0, "--- REPORTE DE VEHÍCULOS DE LA AGENCIA ---");
        
        // Generar nombre de archivo único con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "Reporte_Vehiculos_" + timestamp + ".txt";
        
        // Definir la ruta del archivo (se guardará en el directorio de ejecución del programa)
        Path rutaArchivo = Path.of("reports/" + nombreArchivo);
        
        try {
            // Escribir todas las líneas en el archivo. Esto lo crea si no existe.
            Files.write(rutaArchivo, lineasReporte);
            
            System.out.println("Reporte de vehículos generado con éxito en: " + rutaArchivo.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de reporte de vehículos: " + e.getMessage());
        }
    }
}
