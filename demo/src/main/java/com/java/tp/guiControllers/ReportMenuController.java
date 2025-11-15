package com.java.tp.guiControllers;
import java.io.IOException;

import com.java.tp.App;

import javafx.fxml.FXML;

public class ReportMenuController {

    @FXML
    private void switchToResponsableReport() throws IOException {
        App.setRoot("topResponsables");
    }
    @FXML
    private void switchToVehiculosReport() throws IOException {
        App.setRoot("topVehicles");
    }
    @FXML
    private void switchToPlacesReport() throws IOException {
        App.setRoot("topPlaces");
    }
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }

}
