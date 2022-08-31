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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class AddServiceController implements Initializable {


    @FXML
    private TextField quantityTxt;

    @FXML
    private ComboBox selectService;

    @FXML
    private TableView servicesTbl;

    @FXML
    private TableColumn nameTblCol;

    @FXML
    private TableColumn priceTblCol;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClose;

    @FXML
    private TableColumn quantityTblCol;

    @FXML
    private TableColumn idTblCol;

    private ServicesRendered selectedService=null;
    private Task selectedTask=null;


    @FXML
    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onDelete(ActionEvent event) {
        if (selectedService!= null) {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Are you sure you want to delete service "+this.selectedService.getId()+"?");
                alert.setContentText("Delete?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type == okButton)
                    {
                        try {
                            ServiceStock serviceStock = (ServiceStock) this.selectService.getValue();
                            Double quantity= Double.valueOf(quantityTxt.getText());
                            selectedService.delete();
                            ServiceStock stockQuantityObject= (ServiceStock) Table.get(ServiceStock.class,serviceStock.getId());
                            Double stockQuantity=stockQuantityObject.getQuantity();
                            stockQuantityObject.setQuantity(stockQuantity+quantity);
                            stockQuantityObject.update();
                            this.fillServicesRendered();
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
    public void onSave(ActionEvent event) throws Exception {

        boolean isSelectServiceEmpty =(this.selectService.getValue() == null);


        if ( isSelectServiceEmpty || quantityTxt.getText().equals("") || !isNumeric(quantityTxt.getText()) ) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "All fields are mandatory and quantity must be a number!", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Input error!");
            alert.showAndWait();
        } else {
            ServiceStock serviceStock = (ServiceStock) this.selectService.getValue();
            ServiceStock stockQuantityObject= (ServiceStock) Table.get(ServiceStock.class,serviceStock.getId());
            Double stockQuantity=stockQuantityObject.getQuantity();

            ServicesRendered sr;
            if (this.selectedService == null) {
                sr = new  ServicesRendered();
            } else {
                sr = this.selectedService;
            }

                try {
                    if (this.selectedService == null) {
                        if(Double.valueOf(quantityTxt.getText())>stockQuantity)
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "There is not enough on stock!Only "+stockQuantity+" left", ButtonType.OK);
                            alert.setTitle("Warning");
                            alert.setHeaderText("Error!");
                            alert.showAndWait();
                        }
                        else
                        {
                            sr.setQuantity(Double.valueOf(quantityTxt.getText()));
                            sr.setTask_FK(selectedTask.getId());
                            sr.setServiceStock_FK(serviceStock.getId());

                            sr.save();
                            stockQuantityObject.setQuantity(stockQuantity-Double.valueOf(quantityTxt.getText()));
                            stockQuantityObject.update();


                            this.fillServicesRendered();
                            this.removeSelection();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service added succesfully", ButtonType.OK);
                            alert.setTitle("Information");
                            alert.setHeaderText("Success!");
                            alert.showAndWait();

                        }
                    } else {
                            if(Double.valueOf(quantityTxt.getText())>this.selectedService.getQuantity())
                            {
                                    if((this.selectedService.getQuantity()+stockQuantity)<Double.valueOf(quantityTxt.getText()))
                                {
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "There is not enough on stock!Only "+stockQuantity+" left", ButtonType.OK);
                                    alert.setTitle("Warning");
                                    alert.setHeaderText("Error!");
                                    alert.showAndWait();
                                }
                                else {
                                    stockQuantityObject.setQuantity(stockQuantity-(Double.valueOf(quantityTxt.getText())-this.selectedService.getQuantity()));
                                    stockQuantityObject.update();
                                    sr.setQuantity(Double.valueOf(quantityTxt.getText()));
                                    sr.setTask_FK(selectedTask.getId());
                                    sr.setServiceStock_FK(serviceStock.getId());
                                    sr.update();

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service edited succesfully", ButtonType.OK);
                                    alert.setTitle("Information");
                                    alert.setHeaderText("Success!");
                                    alert.showAndWait();
                                }

                            }
                            else
                            {
                                stockQuantityObject.setQuantity(stockQuantity+(this.selectedService.getQuantity()-Double.valueOf(quantityTxt.getText())));
                                stockQuantityObject.update();
                                sr.setQuantity(Double.valueOf(quantityTxt.getText()));
                                sr.setTask_FK(selectedTask.getId());
                                sr.setServiceStock_FK(serviceStock.getId());
                                sr.update();

                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service edited succesfully", ButtonType.OK);
                                alert.setTitle("Information");
                                alert.setHeaderText("Success!");
                                alert.showAndWait();
                            }
                            this.fillServicesRendered();
                            this.removeSelection();


                        }

                    fillServicesRendered();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            List<?>serviceStock= Table.list(ServiceStock.class);
            ObservableList<?>observableServiceStockList= FXCollections.observableList(serviceStock);
            selectService.setItems(observableServiceStockList);

            idTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServicesRendered, Long>, SimpleLongProperty>) ServicesRenderedLongCellDataFeatures -> new SimpleLongProperty(ServicesRenderedLongCellDataFeatures.getValue().getId())
            );

            nameTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServicesRendered, String>, SimpleStringProperty>) ServicesRenderedStringCellDataFeatures -> {
                        try {
                            return new SimpleStringProperty(ServicesRenderedStringCellDataFeatures.getValue().getServiceStock().getName());
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        return null;
                    }
            );
            quantityTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServicesRendered, Double>, SimpleDoubleProperty>) ServicesRenderedSDoubleCellDataFeatures -> new SimpleDoubleProperty(ServicesRenderedSDoubleCellDataFeatures.getValue().getQuantity())
            );
            priceTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ServicesRendered, Double>, SimpleDoubleProperty>) ServicesRenderedSDoubleCellDataFeatures -> {
                        try {
                            return new SimpleDoubleProperty(ServicesRenderedSDoubleCellDataFeatures.getValue().getServiceStock().getPrice());
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        return null;
                    }
            );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillServicesRendered()
    {
        try {
            List<ServicesRendered>  servicesRendered = Table.servicesRendered(makeSelectQuery());
            ObservableList<?> servicesObservableList = FXCollections.observableList(servicesRendered);
            this.servicesTbl.setItems(servicesObservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void selectService(MouseEvent evt) throws Exception
    {
        try {
            this.selectedService = (ServicesRendered) this.servicesTbl.getSelectionModel().getSelectedItem();
            if(selectedService!=null)
            {
                this.btnAdd.setText("Edit");
                this.selectService.setDisable(true);
                this.quantityTxt.setText(String.valueOf(this.selectedService.getQuantity()));
                this.selectService.setValue(this.selectedService.getServiceStock());
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }
    public void setSelectedTask(Task t)
    {
        this.selectedTask=t;
    }

    @FXML
    protected void removeSelection() {
        this.selectedService = null;
        this.selectService.setDisable(false);
        this.btnAdd.setText("Add");
        this.selectService.valueProperty().set(null);
        this.quantityTxt.setText("");
    }

    public ResultSet makeSelectQuery() throws SQLException {
        String SQL = "SELECT ServicesRendered.id,ServicesRendered.serviceStock_FK,ServicesRendered.task_FK,ServicesRendered.quantity FROM Task LEFT JOIN ServicesRendered ON Task.id = ServicesRendered.task_FK LEFT JOIN ServicesRendered ON ServicesRendered.serviceStock_FK = ServicesRendered.id WHERE Task.id="+this.selectedTask.getId();
        Statement stmt = DatabaseConnection.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);
        return rs;
    }

    @FXML
    public void remove(MouseEvent event) {
        if(this.selectedService!=null)
        {
            servicesTbl.getSelectionModel().clearSelection();
            removeSelection();
        }

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


}
