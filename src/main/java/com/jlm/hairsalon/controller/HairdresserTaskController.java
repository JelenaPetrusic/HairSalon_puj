package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.Main;
import com.jlm.hairsalon.model.DatabaseConnection;
import com.jlm.hairsalon.model.HairdresserView;
import com.jlm.hairsalon.model.Table;
import com.jlm.hairsalon.model.Task;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class HairdresserTaskController implements Initializable {


    @FXML
    private TableColumn clientAddressTblCol;

    @FXML
    private TableColumn clientNameTblCol;

    @FXML
    private TableColumn idTblCol;

    @FXML
    private TableColumn taskDateTblCol;

    @FXML
    private TableView tasksTbl;

    HairdresserView selectedTask=null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idTblCol.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<HairdresserView, Long>, SimpleLongProperty>) hairdresserTaskLongCellDataFeatures -> new SimpleLongProperty(hairdresserTaskLongCellDataFeatures.getValue().getTaskId())
        );
        clientNameTblCol.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<HairdresserView, String>, SimpleStringProperty>) hairdresserTaskStringCellDataFeatures -> new SimpleStringProperty(hairdresserTaskStringCellDataFeatures.getValue().getClientName())
        );
        clientAddressTblCol.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<HairdresserView, String>, SimpleStringProperty>) hairdresserTaskStringCellDataFeatures -> new SimpleStringProperty(hairdresserTaskStringCellDataFeatures.getValue().getClientAddress())
        );
        taskDateTblCol.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures< HairdresserView, String>, SimpleStringProperty>) hairdresserTaskStringCellDataFeatures -> new SimpleStringProperty(hairdresserTaskStringCellDataFeatures.getValue().getDate())
        );
    fillHairdresserTasks();
    }

    private void fillHairdresserTasks() {
        try {
            List<HairdresserView> hairdresserTasks = Table.HairdresserTasks(makeQueryforTasks());
            ObservableList<HairdresserView> hairdresserTasksObservableList = FXCollections.observableList(hairdresserTasks);
            this.tasksTbl.setItems(hairdresserTasksObservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet makeQueryforTasks()throws SQLException {
        String SQL = "SELECT Task.id,Client.firstName,Client.address,Task.date,Client.id FROM Client LEFT JOIN Task ON Client.id = Task.client_FK LEFT JOIN User ON User_FK=User.id  WHERE Task.User_FK="+ LoginController.loggedUser.getId();
        Statement stmt = DatabaseConnection.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);
        return rs;
    }

    @FXML
    public void selectTask(MouseEvent evt) throws Exception {
        this.selectedTask = (HairdresserView)  this.tasksTbl.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void taskDetails(ActionEvent event) throws Exception {
        if(selectedTask!=null)
        {
            Task t=new Task();

            t.setId(this.selectedTask.getTaskId());
            t.setDate(Date.valueOf(this.selectedTask.getDate()));
            t.setClient_FK(this.selectedTask.getClientId());

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("invoice.fxml"));
            Parent root = fxmlLoader.load();
            InvoiceController incont = fxmlLoader.getController();
            incont.setSelectedTask(t);
            incont.initController();
            Stage stage = new Stage();
            stage.setTitle("View services");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
    }

    @FXML
    public void remove(MouseEvent event) {
        tasksTbl.getSelectionModel().clearSelection();
    }

}
