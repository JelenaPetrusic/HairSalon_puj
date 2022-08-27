package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.Main;
import com.jlm.hairsalon.model.*;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TaskController implements Initializable {


    @FXML
    private TextField searchTxt;

    @FXML
    private TableView tasksTbl;

    @FXML
    private TableColumn IDTblCol;

    @FXML
    private TableColumn hairdresserTblCol;

    @FXML
    private TableColumn dateTblCol;

    @FXML
    private TableColumn clientTblCol;

    @FXML
    private DatePicker datePick;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnPreview;

    @FXML
    private Button btnAddService;

    @FXML
    private ComboBox selectHairdresser;

    @FXML
    private ComboBox selectClient;

    public Task selectedTask = null;


    @FXML
    public void onDelete(ActionEvent event) {

        if (selectedTask != null) {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Are you sure you want to delete task "+this.selectedTask.getId()+"?");
                alert.setContentText("Delete?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type == okButton)
                    {
                        try {
                            selectedTask.delete();
                            this.fillTasks();
                            this.removeSelection();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        alert.close();
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    public void onSave(ActionEvent event) {
        boolean isSelectHairdresserEmpty =(this.selectHairdresser.getValue() == null);
        boolean isSelectClientEmpty =(this.selectClient.getValue() == null);

        if (datePick.getValue() == null || isSelectClientEmpty || isSelectHairdresserEmpty ) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter all fields!", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Input error!");
            alert.showAndWait();
        } else {
            User hairdresser = (User) this.selectHairdresser.getValue();
            Client client = (Client) this.selectClient.getValue();
            String date = this.datePick.getValue().toString();
            Task t;
            if (this.selectedTask == null) {
                t = new Task();
            } else {
                t = this.selectedTask;
            }
            t.setUser_FK(hairdresser.getId());
            t.setDate(Date.valueOf(date));
            t.setStartTime(Time.valueOf(LocalTime.now()));
            t.setEndTime(Time.valueOf(LocalTime.now()));
            t.setPauseLength(0.5);
            t.setClient_FK(client.getId());

            try {
                if (this.selectedTask == null) {
                    t.save();
                    this.fillTasks();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "New task added succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                } else {
                    t.update();
                    this.fillTasks();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Task edited succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }
                fillTasks();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            List<?> userList = Table.list(User.class);
            List<?> clientList = Table.list(Client.class);
            List<?> taskList = Table.list(Task.class);
            ObservableList<?> observableTaskList = FXCollections.observableList(userList);
            ObservableList<?> observableClientList = FXCollections.observableList(clientList);
            ObservableList<?> taskObservableList = FXCollections.observableList(taskList);
            selectHairdresser.setItems(observableTaskList);
            selectClient.setItems(observableClientList);
            btnPreview.setDisable(true);
            btnAddService.setDisable(true);

            IDTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Task, Long>, SimpleLongProperty>) taskLongCellDataFeatures -> new SimpleLongProperty(taskLongCellDataFeatures.getValue().getId())
            );
            hairdresserTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Task, String>, SimpleStringProperty>) taskLongCellDataFeatures -> {
                        try {
                            return new SimpleStringProperty(taskLongCellDataFeatures.getValue().getUser().getUserName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );
            clientTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Task, String>, SimpleStringProperty>) taskLongCellDataFeatures -> {
                        try {
                            return new SimpleStringProperty(taskLongCellDataFeatures.getValue().getClient().getFirstName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );

            dateTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Task, String>, SimpleStringProperty>) taskLongCellDataFeatures -> new SimpleStringProperty(taskLongCellDataFeatures.getValue().getDate().toString())
            );


            fillTasks();


            searchTxt.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        try {
                            tasksTbl.setItems(filterList((List<Task>) taskObservableList, newValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillTasks() {
        try {
            List<?> tasksList = Table.list(Task.class);
            ObservableList<?> tasksObservableList = FXCollections.observableList(tasksList);
            this.tasksTbl.setItems(tasksObservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void removeSelection() {
        this.selectedTask = null;
        this.fillTasks();
        this.selectHairdresser.valueProperty().set(null);
        this.selectClient.valueProperty().set(null);
        this.btnSave.setText("Add task");
        this.datePick.setValue(null);
        this.btnPreview.setDisable(true);
        this.btnAddService.setDisable(true);
    }

    @FXML
    public void selectTask(MouseEvent evt) throws Exception {
        this.selectedTask = (Task) this.tasksTbl.getSelectionModel().getSelectedItem();
        if(selectedTask!=null)
        {
            this.btnSave.setText("Edit");
            this.selectClient.setValue(this.selectedTask.getClient());
            this.selectHairdresser.setValue(this.selectedTask.getUser());
            this.datePick.setValue(this.selectedTask.getDate().toLocalDate());
            this.btnPreview.setDisable(false);
            this.btnAddService.setDisable(false);
        }

    }

    public void onPreview() throws Exception {
        if(selectedTask!=null)
        {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("invoice.fxml"));
            Parent root = fxmlLoader.load();
            InvoiceController incont = fxmlLoader.getController();
            incont.setSelectedTask(selectedTask);
            incont.initController();
            Stage stage = new Stage();
            stage.setTitle("View services");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
    }

    public void onAddService() throws IOException {
        if (selectedTask != null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("addService.fxml"));
            Parent root = fxmlLoader.load();
            AddServiceController addServiceController = fxmlLoader.getController();
            addServiceController.setSelectedTask(this.selectedTask);
            Stage stage = new Stage();
            stage.setTitle("Set service");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }

    }

    private boolean searchFindsTask(Task task, String searchText) throws Exception {
        return (task.getClient().getFirstName().toLowerCase().contains(searchText.toLowerCase())) ||
                (task.getUser().getUserName().toLowerCase().contains(searchText.toLowerCase())) ||
                Integer.valueOf(task.getId()).toString().equals(searchText.toLowerCase())||
                (task.getDate().toString().toLowerCase().contains(searchText.toLowerCase()));

    }

    private ObservableList<Task> filterList(List<Task> list, String searchText) throws Exception {
        List<Task> filteredList = new ArrayList<>();
        for (Task task : list){
            if(searchFindsTask(task, searchText))
                filteredList.add(task);
        }
        return FXCollections.observableList(filteredList);
    }

    @FXML
    void remove(MouseEvent event) {
        if(this.selectedTask!=null)
        {
            tasksTbl.getSelectionModel().clearSelection();
            removeSelection();
        }
    }


}







