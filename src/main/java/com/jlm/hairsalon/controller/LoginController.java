package com.jlm.hairsalon.controller;


import com.jlm.hairsalon.Main;
import com.jlm.hairsalon.model.CryptMD5;
import com.jlm.hairsalon.model.DatabaseConnection;
import com.jlm.hairsalon.model.Table;
import com.jlm.hairsalon.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    @FXML
    private PasswordField tfpassword;

    @FXML
    private TextField tfusername;

    @FXML
    private TextField signupAddress;

    @FXML
    private TextField signupEmail;

    @FXML
    private TextField signupName;

    @FXML
    private PasswordField signupPassword;

    @FXML
    private TextField signupPhoneNumber;

    @FXML
    private TextField signupSurname;

    @FXML
    private TextField signupUsername;

    protected static User loggedUser;

    @FXML
    public void btnloginclicked(ActionEvent event) throws IOException {
        String username = this.tfusername.getText();
        String password = CryptMD5.cryptWithMD5(this.tfpassword.getText());

        if (username.equals("") || password.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter all fields!", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Input error!");
            alert.showAndWait();
        } else {
            try {
                PreparedStatement query = DatabaseConnection.CONNECTION.prepareStatement("SELECT * FROM user WHERE userName=? AND password=?");
                query.setString(1, username);
                query.setString(2, password);
                ResultSet result = query.executeQuery();

                if (result.next()){
                    LoginController.loggedUser = (User) Table.get(User.class, result.getInt("id"));

                    if(loggedUser.getRole().getName().equals("Admin"))
                        Main.showWindow("task.fxml","Welcome "+loggedUser.getUserName(), 1200, 740);
                    else
                        Main.showWindow("hairdresserView.fxml","Welcome "+loggedUser.getUserName(), 900, 700);


                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Wrong credentials!!", ButtonType.OK);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Error");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                System.out.println("Error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void backToLogin()throws Exception {
        Main.showWindow("login.fxml","Login to the system",1200,400);
    }

    @FXML
    public void btnSignupClicked()throws Exception {
        Main.showWindow("signup.fxml","Sign up!", 1200, 600);
    }

    @FXML
    public void signUp() throws IOException {

        String username=signupUsername.getText();
        String firstName=signupName.getText();
        String lastName=signupSurname.getText();
        String adress= signupAddress.getText();
        String email=signupEmail.getText();
        String phoneNumber=signupPhoneNumber.getText();
        String password=signupPassword.getText();

        if (firstName.equals("")||lastName.equals("")||phoneNumber.equals("")||email.equals("")||username.equals("")||adress.equals("")||password.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter all fields", ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
        else {
            try {
                PreparedStatement userCheck = DatabaseConnection.CONNECTION.prepareStatement("SELECT * FROM user WHERE userName=?");
                userCheck.setString(1, username);
                ResultSet result = userCheck.executeQuery();
                if (result.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Username already exists", ButtonType.OK);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Error");
                    alert.showAndWait();
                } else {
                    PreparedStatement insert = DatabaseConnection.CONNECTION.prepareStatement("INSERT INTO user (firstName,lastName,address,phoneNumber,email,userName,password,Role_FK) VALUES(?,?,?,?,?,?,?,?)");
                    insert.setString(1, firstName);
                    insert.setString(2, lastName);
                    insert.setString(3, adress);
                    insert.setString(4, phoneNumber);
                    insert.setString(5, email);
                    insert.setString(6, username);
                    insert.setString(7, CryptMD5.cryptWithMD5(password));
                    insert.setInt(8, 2);
                    insert.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You signed up successfully!", ButtonType.OK);
                    alert.setTitle("Success");
                    alert.setHeaderText("Success");
                    alert.showAndWait();
                    Main.showWindow("login.fxml", "Login to the system", 1200, 400);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


}
