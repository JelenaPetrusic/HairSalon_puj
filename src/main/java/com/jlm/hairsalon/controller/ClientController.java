package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.model.Client;
import com.jlm.hairsalon.model.Table;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private TableView clientsTbl;

    @FXML
    private TableColumn IDTblCol;

    @FXML
    private TableColumn nameTblCol;

    @FXML
    private TableColumn surnameTblCol;

    @FXML
    private TableColumn addressTblCol;

    @FXML
    private TableColumn mobilePhoneTblCol;

    @FXML
    private TableColumn e_mailTblCol;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField surnameTxt;

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField mobilePhoneTxt;

    @FXML
    private TextField e_mailTxt;

    @FXML
    private Button btnAdd;

    @FXML
    private TextField searchTxt;

    private Client selectedClient=null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<?>clientList=null;

        try {
            clientList=Table.list(Client.class);

            ObservableList<?> clientObservableList = FXCollections.observableList(clientList);

            IDTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, Long>, SimpleLongProperty>) clientLongCellDataFeatures -> new SimpleLongProperty(clientLongCellDataFeatures.getValue().getId())
            );

            nameTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, String>, SimpleStringProperty>) clientLongCellDataFeatures -> new SimpleStringProperty(clientLongCellDataFeatures.getValue().getFirstName())
            );

            surnameTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, String>, SimpleStringProperty>) clientLongCellDataFeatures -> new SimpleStringProperty(clientLongCellDataFeatures.getValue().getLastName())
            );

            addressTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, String>, SimpleStringProperty>) clientLongCellDataFeatures -> new SimpleStringProperty(clientLongCellDataFeatures.getValue().getAdress())
            );
            mobilePhoneTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, String>, SimpleStringProperty>) clientLongCellDataFeatures -> new SimpleStringProperty(clientLongCellDataFeatures.getValue().getPhoneNumber())
            );
            e_mailTblCol.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<Client, String>, SimpleStringProperty>) clientLongCellDataFeatures -> new SimpleStringProperty(clientLongCellDataFeatures.getValue().getEmail())
            );


            fillClients();

            searchTxt.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        try {
                            clientsTbl.setItems(filterList((List<Client>) clientObservableList, newValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void fillClients(){
        try {
            List<?> clientsList = Table.list(Client.class);
            ObservableList<?> clientObeservableList = FXCollections.observableList(clientsList);
            this.clientsTbl.setItems(clientObeservableList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onDelete(ActionEvent event) {
        if (selectedClient != null){
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Are you sure you want to delete client "+this.selectedClient.getFirstName()+"?");
                alert.setContentText("Delete?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type == okButton)
                    {
                        try {
                            selectedClient.delete();
                            this.fillClients();
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
        String firstName = this.nameTxt.getText();
        String lastName = this.surnameTxt.getText();
        String adress=this.addressTxt.getText();
        String phoneNumb = this.mobilePhoneTxt.getText();
        String email = this.e_mailTxt.getText();


        if (firstName.equals("")||lastName.equals("")||phoneNumb.equals("")||email.equals("")||adress.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter all fields!", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Input error!");
            alert.showAndWait();
        }
        else
        {
            Client c;
            if (this.selectedClient == null) {
                c = new Client();
            } else {
                c = this.selectedClient;
            }
            c.setFirstName(firstName);
            c.setLastName(lastName);
            c.setAdress(adress);
            c.setPhoneNumber(phoneNumb);
            c.setEmail(email);


            try {
                if (this.selectedClient == null) {
                    c.save();
                    this.fillClients();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "New client added succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }
                else
                {
                    c.update();
                    this.fillClients();
                    this.removeSelection();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Client edited succesfully", ButtonType.OK);
                    alert.setTitle("Information");
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }

                fillClients();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    public void selectClient(MouseEvent evt){
        this.selectedClient = (Client) this.clientsTbl.getSelectionModel().getSelectedItem();
        if(selectedClient!=null)
        {

            this.btnAdd.setText("Edit");
            this.nameTxt.setText(this.selectedClient.getFirstName());
            this.surnameTxt.setText(this.selectedClient.getLastName());
            this.addressTxt.setText(this.selectedClient.getAdress());
            this.mobilePhoneTxt.setText(this.selectedClient.getPhoneNumber());
            this.e_mailTxt.setText(this.selectedClient.getEmail());
        }
    }

    @FXML
    protected void removeSelection(){
        this.selectedClient = null;
        this.fillClients();
        this.btnAdd.setText("Add");
        this.nameTxt.setText("");
        this.surnameTxt.setText("");
        this.addressTxt.setText("");
        this.mobilePhoneTxt.setText("");
        this.e_mailTxt.setText("");
    }

    private boolean searchFindsClient(Client client, String searchText) throws Exception {
        return (client.getFirstName().toLowerCase().contains(searchText.toLowerCase())) ||
                (client.getLastName().toLowerCase().contains(searchText.toLowerCase())) ||
                Integer.valueOf(client.getId()).toString().equals(searchText.toLowerCase())||
                (client.getAdress().toLowerCase().contains(searchText.toLowerCase())) ||
                (client.getEmail().toLowerCase().contains(searchText.toLowerCase()));

    }

    private ObservableList<Client> filterList(List<Client> list, String searchText) throws Exception {
        List<Client> filteredList = new ArrayList<>();
        for (Client client : list){
            if(searchFindsClient(client, searchText))
                filteredList.add(client);
        }
        return FXCollections.observableList(filteredList);
    }

    @FXML
    public void remove(MouseEvent event) {
        if(this.selectedClient!=null)
        {
            clientsTbl.getSelectionModel().clearSelection();
            removeSelection();
        }

    }

}
