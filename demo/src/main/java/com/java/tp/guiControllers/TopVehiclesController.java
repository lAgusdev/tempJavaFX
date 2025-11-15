package com.java.tp.guiControllers;
import com.java.tp.App;
import com.java.tp.agency.reports.ReportPlaces;
import com.java.tp.agency.reports.ReportVehicles;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;


public class TopVehiclesController {

    @FXML
    private javafx.scene.control.ListView<String> list;
    
    @FXML
    private javafx.scene.control.Label totalVehicles;

    @FXML
    private void initialize() {
        try {
            List<String> lista = ReportVehicles.getVehiculosFormateados();
            float totalPrecio = ReportVehicles.getPrecioTotalVehiculos();
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            list.setItems(items);
            totalVehicles.setText(String.format("$%.2f", totalPrecio));
            
        } catch (Exception e) {
            ObservableList<String> items = FXCollections.observableArrayList(
                List.of("No se pudieron cargar los destinos")
            );
            list.setItems(items);
            totalVehicles.setText("0.00");
            System.out.println("No se pudieron cargar los vehículos: " + e.getMessage());
        }
    }

    @FXML
    private void generateReport() {
        try {
            ReportVehicles.generateReport();
        } catch (IOException e) {
            System.err.println("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("reportMenu");
    }

}