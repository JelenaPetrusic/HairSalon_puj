package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceController implements Initializable {

    @FXML
    private TableView servicesTbl;

    @FXML
    private TableColumn IDTblCol;

    @FXML
    private TableColumn nameTblCol;

    @FXML
    private TableColumn quantityTblCol;

    @FXML
    private TableColumn PriceTblCol;

    @FXML
    private TextField searchTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField quantityTxt;

    @FXML
    private TextField PriceTxt;

    @FXML
    private Button btnAdd;

    private ServiceStock selectedService=null;

    @FXML
    public void onDelete(ActionEvent event) {
        if (selectedService != null){
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Are you sure you want to delete service "+this.selectedService.getName()+"?");
                alert.setContentText("Delete?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type == okButton)
                    {
                        try {
                            selectedService.delete();
                            this.fillServices();
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
    public void onSave(ActionEvent event) {
        String name = this.nameTxt.getText();
        String quantity = this.quantityTxt.getText();
        String Price = this.PriceTxt.getText();

        if (name.equals("")|| Price.equals("")|| quantity==null || !isNumeric(Price)  || !isNumeric(quantity))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "All fields are mandatory!Quantity and price must be a number", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Input error!");
            alert.showAndWait();
        }
        else
        {
            ServiceStock s;
            if (this.selectedService == null) {
                s = new ServiceStock();
            } else {
                s= this.selectedService;
            }
            s.setName(name);
            s.setQuantity(Double.valueOf(quantity));
            s.setPrice(Double.valueOf(Price));
            try {
                if (this.selectedService == null) {
                    s.save();
                    this.fillServices();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "New service added succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }
                else
                {
                    s.update();
                    this.fillServices();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service edited succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }
                fillServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            List<?> servicesList = Table.list(ServiceStock.class);
            ObservableList<?> servicesObservableList = FXCollections.observableList(servicesList);

            IDTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServiceStock, Long>, SimpleLongProperty>) servicesLongCellDataFeatures -> new SimpleLongProperty(servicesLongCellDataFeatures.getValue().getId())
            );
            nameTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServiceStock, String>, SimpleStringProperty>) servicesLongCellDataFeatures -> new SimpleStringProperty(servicesLongCellDataFeatures.getValue().getName())
            );
            PriceTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServiceStock, Double>, SimpleDoubleProperty>) servicesLongCellDataFeatures -> new SimpleDoubleProperty(servicesLongCellDataFeatures.getValue().getPrice())
            );


            fillServices();

            searchTxt.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        try {
                            servicesTbl.setItems(filterList((List<ServiceStock>) servicesObservableList, newValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillServices(){
        try {
            List<?> servicesList = Table.list(ServiceStock.class);
            ObservableList<?> servicesObservableList = FXCollections.observableList(servicesList);
            this.servicesTbl.setItems(servicesObservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void removeSelection() {
        this.selectedService = null;
        this.fillServices();
        this.btnAdd.setText("Add");
        this.nameTxt.setText("");
        this.PriceTxt.setText("");

    }

    @FXML
    public void selectService (MouseEvent evt){
        this.selectedService = (ServiceStock) this.servicesTbl.getSelectionModel().getSelectedItem();
        if(this.selectedService!=null)
        {
            this.btnAdd.setText("Edit");
            this.nameTxt.setText(this.selectedService.getName());
            this.PriceTxt.setText(String.valueOf(this.selectedService.getPrice()));
        }
    }

    private boolean searchFindsService(ServiceStock service, String searchText) throws Exception {
        return (service.getName().toLowerCase().contains(searchText.toLowerCase())) ||
               (Integer.valueOf(service.getId()).toString().equals(searchText.toLowerCase())) ||
               (Double.valueOf(service.getPrice()).toString().equals(searchText.toLowerCase()));

    }

    private ObservableList<ServiceStock> filterList(List<ServiceStock> list, String searchText) throws Exception {
        List<ServiceStock> filteredList = new ArrayList<>();
        for (ServiceStock service : list){
            if(searchFindsService(service, searchText))
                filteredList.add(service);
        }
        return FXCollections.observableList(filteredList);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @FXML
   public void remove(MouseEvent event) {
        if(this.selectedService!=null)
        {
            servicesTbl.getSelectionModel().clearSelection();
            removeSelection();
        }

    }
}
