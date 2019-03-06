/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;
import util.DBConnection;

/**
 *
 * @author Austyn
 */
public class LoginController implements Initializable {
  
  @FXML
  private AnchorPane anchorPane;
  
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
  
  public static User currentUser;

  @FXML
  private void login() throws SQLException, IOException{
    
    PreparedStatement stmt;
    ResultSet rs = null;
    
    if (isValidInput()){
      
      stmt = DBConnection.conn.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = ?");
      stmt.setString(1, usernameField.getText());
      stmt.setString(2, passwordField.getText());
      stmt.execute();
      rs = stmt.getResultSet();

      if (rs.next()) {
        User user = new User();
        user.setUsername(usernameField.getText());
        user.setUserID(Integer.parseInt(rs.getString("userId")));
        currentUser = user;
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
        viewScheduleStage();
      }else{
        errorMsg.setText(rb.getString("invalidUser"));
      }
    }  

  }
  
  private boolean isValidInput(){
    boolean validInput = true;
    
    if (usernameField.getText().trim().equals("") ||
        passwordField.getText().trim().equals("")){
      
      validInput = false;
      
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Missing Value");
      alert.setHeaderText("At least one of the inputs is missing a value");
      alert.setContentText("All fields are mandatory.");
      alert.showAndWait();
      
    }
    
    return validInput;
  }
  
  private void viewScheduleStage() throws IOException, SQLException{
    FXMLLoader scheduleLoader = new FXMLLoader(getClass().getResource("/view/Schedules.fxml"));
    scheduleLoader.setResources(rb);
    Parent addPartParent = (Parent) scheduleLoader.load();
    
    SchedulesController schedController = scheduleLoader.getController();
    schedController.getAllCustomers();
    schedController.getAllAppts();
    
    Scene addPartScene = new Scene(addPartParent);

    Stage stage = new Stage();
    stage.setScene(addPartScene);
    stage.setTitle("Schedules");
    stage.show();
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
