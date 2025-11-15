package com.java.tp.guiControllers;
import java.io.IOException;

import com.java.tp.App;

import javafx.fxml.FXML;

public class MenuPostNewTravelController {

    @FXML
    private void switchToCreateTravel() throws IOException {
        App.setRoot("newTravelMenu");
    }
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }
}