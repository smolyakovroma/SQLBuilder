package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.DBHelper;

import java.io.IOException;

public class MainController{

    private Stage mainStage;
    @FXML
    private Button btnTest;
    @FXML
    private Button btnConsole;
//    @FXML
//    private Button btnDelete;
//    @FXML
//    private Button btnSearch;
//    @FXML
//    private TableView tableAddressBook;
//    @FXML
//    private Label labelCount;
    @FXML
    private TextField txtURL;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtPassword;
    @FXML
    private Label labelConnection;

    private Parent fxmlConsole;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private ConsoleController consoleController;
    private Stage ConsoleStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void initialize() {
        initLoader();
    }

    private void initLoader() {
        try {

            fxmlLoader.setLocation(getClass().getResource("../fxml/console.fxml"));
            fxmlConsole = fxmlLoader.load();
            consoleController = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        if (!(actionEvent.getSource() instanceof Button)) return;

        Button btn = (Button) actionEvent.getSource();
        switch (btn.getId()) {
            case "btnTest":
                DBHelper.setUserPassword(txtURL.getText(), txtUser.getText(), txtPassword.getText());
                if(DBHelper.testConnection()){
                    labelConnection.setText("Статус: подключен");
                    labelConnection.setStyle("-fx-text-fill:green");
                } else {
                    labelConnection.setText("Статус: не подключен");
                    labelConnection.setStyle("-fx-text-fill:red");
                }

                break;
            case "btnConsole":
                showConsole();
                break;
            case "btn2":

                break;
            case "btn3":
                System.out.println("search");
                break;
        }
    }

    private void showConsole() {

        if (ConsoleStage ==null) {
            ConsoleStage = new Stage();
            ConsoleStage.setTitle("Консоль");
            ConsoleStage.setMinHeight(500);
            consoleController.setMainStage(ConsoleStage);
            ConsoleStage.setMinWidth(700);
            ConsoleStage.setResizable(true);
            ConsoleStage.setScene(new Scene(fxmlConsole));
            ConsoleStage.initModality(Modality.WINDOW_MODAL);
            ConsoleStage.initOwner(mainStage);
        }

        ConsoleStage.showAndWait();

    }


}
