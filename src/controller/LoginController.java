/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.DBConnection;

/**
 *
 * @author Austyn
 */
public class LoginController implements Initializable {

  @FXML
  private TextField usernameField;
  
  @FXML
  private PasswordField passwordField;
  
  @FXML
  private Label userLbl, passwordLbl;
  
  @FXML
  private Button loginBtn;
  
  @FXML
  private Label errorMsg;
  
  private ResourceBundle rb;

  @FXML
  private void login() {
    
    PreparedStatement stmt;
    ResultSet rs = null;
    
    try {
      
      stmt = DBConnection.conn.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = ?");
      stmt.setString(1, usernameField.getText());
      stmt.setString(2, passwordField.getText());
      stmt.execute();
      rs = stmt.getResultSet();
      
      if (rs.next()) {
        String country = rs.getString("userId");
        System.out.println(country);
      }else{
        errorMsg.setText(rb.getString("invalidUser"));
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.rb = rb;
    usernameField.setText("test");
    passwordField.setText("test");
    
    userLbl.setText(rb.getString("username"));
    passwordLbl.setText(rb.getString("password"));
    loginBtn.setText(rb.getString("login"));
    
  }  
  
}
