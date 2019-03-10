/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.SchedulesController.appointments;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;
import model.User;
import util.DBConnection;

public class ConsultantsController implements Initializable {
  
  @FXML
  private ComboBox consultCbx;
          
  @FXML
  private TableView<Appointment> apptTbl;
  
  @FXML
  private TableColumn<Appointment, String> apptCustCol,
            apptTitleCol, apptLocCol, apptDescCol, apptStartCol, apptEndCol;
  
  private static ObservableList<User> users = FXCollections.observableArrayList();
  private static FilteredList<Appointment> filteredAppts;
  private User currentUser;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
  
    try{
      getAllConsultants();
    }catch(SQLException | ClassNotFoundException | IOException e){
      e.printStackTrace();
    }
    
    for (User user : users){
      consultCbx.getItems().add(user.getUsername());
    }
    
    filteredAppts = new FilteredList<>(appointments, p -> true);
    
    consultCbx.valueProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
        
        for (User user : users){
          if(user.getUsername().equals(consultCbx.getSelectionModel().getSelectedItem())){
            currentUser = user;
          }
        }
        
        filteredAppts.setPredicate(appt -> {
            
          if (currentUser.getUserID() == appt.getUserID()){
            return true;
          }
          return false;
        });
        
      }    
    });
    
    SortedList<Appointment> sortedAppts = new SortedList<>(filteredAppts);

    sortedAppts.comparatorProperty().bind(apptTbl.comparatorProperty());

    apptTbl.setItems(sortedAppts);
    
    //This lambda is necessary because the setCellValueFactory method
    //takes a Callback to find the value. The SimpleStringProperty is used because
    //the customer name is a property on  Customer
    //object that is a property of the Appointment.
    
    apptCustCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
    
    apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
    apptDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    apptLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
    apptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));

  }
  
  private void getAllConsultants() throws SQLException, ClassNotFoundException, IOException{
    
    DBConnection.connect();
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement("select userName, userId from user");
    ResultSet rs = stmt.executeQuery();
    
    while(rs.next()){
      User user = new User();
      user.setUserID(Integer.parseInt(rs.getString("userId")));
      user.setUsername(rs.getString("userName"));
      users.add(user);
    }
    
    DBConnection.disconnect();
  }
  
}
