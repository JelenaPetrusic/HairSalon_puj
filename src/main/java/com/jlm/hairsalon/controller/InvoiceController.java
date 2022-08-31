package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class InvoiceController implements Initializable {

    @FXML
    private TableView servicesTbl;

    @FXML
    private TableColumn idTblCol;

    @FXML
    private TableColumn nameTblCol;

    @FXML
    private TableColumn quantityTblCol;

    @FXML
    private TableColumn priceTblCol;

    @FXML
    private TableColumn sumTblCol;

    @FXML
    private Label clientAdressLbl;

    @FXML
    private Label clientIdLbl;

    @FXML
    private Label clientNameLbl;

    @FXML
    private Label clientPhoneLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private Label invoiceLbl;

    @FXML
    private Label totalSumLbl;


    private static Task selectedTask=null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

                idTblCol.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<Invoice, Long>, SimpleLongProperty>) tempTableLongCellDataFeatures -> new SimpleLongProperty(tempTableLongCellDataFeatures.getValue().getId())
                );
                nameTblCol.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<Invoice, String>, SimpleStringProperty>) tempTableLongCellDataFeatures -> new SimpleStringProperty(tempTableLongCellDataFeatures.getValue().getName())
                );
                quantityTblCol.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<Invoice, Double>, SimpleDoubleProperty>) tempTableLongCellDataFeatures -> new SimpleDoubleProperty(tempTableLongCellDataFeatures.getValue().getQuantity())
                );

                priceTblCol.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<Invoice, Double>, SimpleDoubleProperty>) tempTableLongCellDataFeatures -> new SimpleDoubleProperty(tempTableLongCellDataFeatures.getValue().getPrice())
                );

                sumTblCol.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<Invoice, Double>, SimpleDoubleProperty>) tempTableLongCellDataFeatures -> new SimpleDoubleProperty(tempTableLongCellDataFeatures.getValue().getSum())
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    protected void fillTempTable(){
        try {
            List<Invoice> tempTableList = Table.listTempTable(makeQuery());
            ObservableList<Invoice> servicesObservableList = FXCollections.observableList(tempTableList);
            this.servicesTbl.setItems(servicesObservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setSelectedTask(Task t)
    {
        this.selectedTask=t;
    }

    public ResultSet makeQuery() throws SQLException {
        String SQL = "SELECT ServiceStock.id, ServiceStock.name, SUM(ServicesRendered.quantity),ServiceStock.Price FROM Task LEFT JOIN ServicesRendered ON Task.id = ServicesRendered.task_FK LEFT JOIN ServiceStock ON ServicesRendered.serviceStock_FK = ServiceStock.id WHERE Task.id="+selectedTask.getId()+" GROUP BY ServiceStock.name ";
        Statement stmt = DatabaseConnection.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);
        return rs;
    }

    public void initController()throws Exception {
        fillTempTable();
        this.invoiceLbl.setText(String.valueOf(this.selectedTask.getId()));
        this.clientIdLbl.setText(String.valueOf(this.selectedTask.getClient().getId()));
        this.dateLbl.setText(this.selectedTask.getDate().toString());
        this.clientNameLbl.setText(this.selectedTask.getClient().getFirstName()+" "+this.selectedTask.getClient().getLastName());
        this.clientPhoneLbl.setText(this.selectedTask.getClient().getPhoneNumber());
        this.clientAdressLbl.setText(this.selectedTask.getClient().getAdress());
        this.totalSumLbl.setText(getSum());
    }

    public String getSum() {

        Double sum=0.0;
        for(int i=0;i<servicesTbl.getItems().size();i++)
        {
            Invoice item =(Invoice) servicesTbl.getItems().get(i);
            TableColumn col = (TableColumn) servicesTbl.getColumns().get(4);
            Double data = (Double) col.getCellObservableValue(item).getValue();
            sum+=data;
        }
        return String.valueOf(sum);

    }


}