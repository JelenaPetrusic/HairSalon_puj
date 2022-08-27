package com.jlm.hairsalon.controller;

import com.jlm.hairsalon.Main;
import com.jlm.hairsalon.model.CryptMD5;
import com.jlm.hairsalon.model.HairdresserView;
import com.jlm.hairsalon.model.Task;
import com.jlm.hairsalon.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class HairdresserProfileController implements Initializable {

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField emailTxt;

    @FXML
    private TextField firstNameTxt;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private TextField phoneNumTxt;

    @FXML
    private TextField userNameTxt;

    private TableView tasksTbl;
    HairdresserView selectedTask=null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    this.firstNameTxt.setText(LoginController.loggedUser.getFirstName());
    this.lastNameTxt.setText(LoginController.loggedUser.getLastName());
    this.addressTxt.setText(LoginController.loggedUser.getAddress());
    this.phoneNumTxt.setText(LoginController.loggedUser.getPhoneNumber());
    this.emailTxt.setText(LoginController.loggedUser.getEmail());
    this.userNameTxt.setText(LoginController.loggedUser.getUserName());
    this.passwordTxt.setText(LoginController.loggedUser.getPassword());
    }

    @FXML
    public void editProfile(MouseEvent event) throws Exception {
    String firstName=this.firstNameTxt.getText();
    String lastName=this.lastNameTxt.getText();
    String address=this.addressTxt.getText();
    String phoneNum=this.phoneNumTxt.getText();
    String email=this.emailTxt.getText();
    String userName=this.userNameTxt.getText();
    String password=this.passwordTxt.getText();

    User u= LoginController.loggedUser;

    u.setFirstName(firstName);
    u.setLastName(lastName);
    u.setAddress(address);
    u.setPhoneNumber(phoneNum);
    u.setEmail(email);
    u.setUserName(userName);
    if(password.equals(u.getPassword()))
        u.setPassword((password));
    else
        u.setPassword(CryptMD5.cryptWithMD5(password));



    u.update();

    this.firstNameTxt.setText(u.getFirstName());
    this.lastNameTxt.setText(u.getLastName());
    this.addressTxt.setText(u.getAddress());
    this.phoneNumTxt.setText(u.getPhoneNumber());
    this.emailTxt.setText(u.getEmail());
    this.userNameTxt.setText(u.getUserName());
    this.passwordTxt.setText(u.getPassword());

    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully", ButtonType.OK);
    alert.setTitle("Success");
    alert.setHeaderText("Profile edited!");
    alert.showAndWait();

    }


}