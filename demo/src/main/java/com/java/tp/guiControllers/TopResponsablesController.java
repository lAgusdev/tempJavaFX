package com.java.tp.guiControllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.java.tp.App;
import com.java.tp.agency.Agency;
import com.java.tp.agency.responsables.Responsable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class TopResponsablesController {

    @FXML
    private javafx.scene.control.ListView<String> list;

    /**
     * Método auxiliar para obtener la lista de responsables formateada, ordenada
     * y con el número de puesto (ranking) al inicio.
     * @return Una lista de Strings con los responsables formateados ("#Puesto - DNI - Nombre - $Salario").
     */
    private List<String> getResponsablesFormateados() {
        HashMap<String, Responsable> res = Agency.getInstancia().getResponsables();
        
        if (res == null || res.isEmpty()) {
            return List.of("No hay responsables cargados");
        } else {
            // 1. Obtener la lista de responsables formateada y ordenada (base)
            // La ordenación es alfabética por el String completo (DNI, Nombre, Salario).
            List<String> responsablesOrdenados = res.values().stream()
                .map(r -> r.getDni() + " - " + r.getNombre() + " - $" + r.getSalario())
                .sorted() 
                .collect(Collectors.toList());

            List<String> listaConPuesto = new ArrayList<>();
            
            // 2. Iterar sobre la lista ordenada para añadir el número de puesto (ranking)
            for (int i = 0; i < responsablesOrdenados.size(); i++) {
                // Se añade el número de posición (i + 1). Ejemplo: "#1 - 12345678 - Juan Perez - $50000"
                String linea = String.format("#%d - %s", (i + 1), responsablesOrdenados.get(i)); 
                listaConPuesto.add(linea);
            }
            
            return listaConPuesto;
        }
    }

    @FXML
    private void initialize() {
        try {
            List<String> lista = getResponsablesFormateados();
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            list.setItems(items);
        } catch (Exception e) {
            ObservableList<String> items = FXCollections.observableArrayList(
                List.of("No se pudieron cargar los responsables")
            );
            list.setItems(items);
            System.out.println("No se pudieron cargar los responsables: " + e.getMessage());
        }
    }

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("reportMenu");
    }

    /** Generador de reportes de responsables */
    @FXML
    private void generateReport() throws IOException {
        List<String> lineasReporte = getResponsablesFormateados();
        
        // Se verifica si el reporte contiene el mensaje de error
        if (lineasReporte.size() == 1 && lineasReporte.get(0).equals("No hay responsables cargados")) {
             System.out.println("No se puede generar el reporte: No hay datos de responsables cargados.");
            return;
        }

        // 1. Añadir encabezado al reporte
        lineasReporte.add(0, "--- REPORTE DE RESPONSABLES DE LA AGENCIA ---");
        
        // 2. Generar nombre de archivo único con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "Reporte_Responsables_" + timestamp + ".txt";
        
        // 3. Definir la ruta del archivo
        Path rutaArchivo = Path.of("reports/" + nombreArchivo);
        
        try {
            // Escribir todas las líneas en el archivo (creándolo si no existe)
            Files.write(rutaArchivo, lineasReporte);
            
            System.out.println("Reporte generado con éxito en: " + rutaArchivo.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de reporte: " + e.getMessage());
        }
    }
}