/**
* Esta clase se encarga de controlar el apartado grafico de un submenú para que el usario decida a que reporte quiere acceder.
* Permitiendo acceder a algun reporte o volver al menuprincipal.
*/
package com.java.tp.guiControllers;
import java.io.IOException;

import com.java.tp.App;

import javafx.fxml.FXML;

public class ReportMenuController {

    @FXML
    private void switchToResponsableReport() throws IOException {
        App.setRoot("responsablesReport");
    }
    @FXML
    private void switchToVehiculosReport() throws IOException {
        App.setRoot("vehiclesReport");
    }
    @FXML
    private void switchToPlacesReport() throws IOException {
        App.setRoot("placesReport");
    }
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }

}
