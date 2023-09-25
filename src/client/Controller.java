package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import thedrake.ui.TheDrakeApp;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button startBtn_1;

    @FXML
    private Button exitBtn;

    Stage stage;

    @FXML
    public void onStart(ActionEvent event) {
        stage = (Stage) startBtn_1.getScene().getWindow();
        try {
            new TheDrakeApp().start(stage);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    @FXML
    public void onExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konec hry");
        alert.setHeaderText("Chystáte se ukončit hru!");
        alert.setContentText("Opravdu chcete ukončit hru?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) exitBtn.getScene().getWindow();
            stage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
