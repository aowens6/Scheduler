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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Address;
import model.City;
import model.Country;
import model.Customer;
import util.DBConnection;

public class SchedulesController implements Initializable {
  
  @FXML
  private TableView<Customer> custTbl;
  
  @FXML
  private TableColumn<Customer, String> nameCol;
  
  @FXML
  private TableColumn<Customer, String> phoneCol;
  
  @FXML
  private TableColumn<Customer, String> addressCol;
  
  private ResourceBundle rb;
  
  public static ObservableList<Customer> customers = FXCollections.observableArrayList();
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    
    this.rb = rb;
    
    nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    phoneCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getAddress().getPhone()));
    addressCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getAddress().getAddress()));
    
  } 
  
  public void getAllCustomers() throws SQLException{
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement("SELECT * FROM customer");
    ResultSet customerSet = stmt.executeQuery();
    ResultSet address = null;
    ResultSet citySet = null;
    ResultSet countrySet = null;
    
    while(customerSet.next()){
      
      Customer cust = new Customer();
      
      cust.setCustomerId(customerSet.getString("customerId"));
      cust.setCustomerName(customerSet.getString("customerName"));
      
      PreparedStatement addrStmt = DBConnection.conn.prepareStatement("SELECT * FROM address where addressId = ?");
      addrStmt.setString(1, customerSet.getString("addressId"));
      address = addrStmt.executeQuery();
      
      if(address.next()){
        
        Address custAddr = new Address();
        custAddr.setAddress(address.getString("address"));
        custAddr.setAddressId(Integer.parseInt(address.getString("addressId")));
        custAddr.setPhone(address.getString("phone"));

        cust.setAddress(custAddr);
        
        PreparedStatement cityStmt = DBConnection.conn.prepareStatement("SELECT * FROM city where cityId = ?");
        cityStmt.setString(1, address.getString("cityId"));
        citySet = cityStmt.executeQuery();
        
        if(citySet.first()){
          City city = new City();
          city.setCityId(Integer.parseInt(citySet.getString("cityId")));
          city.setCity(citySet.getString("city"));
          city.setCountryId(Integer.parseInt(citySet.getString("countryId")));
          
          cust.setCity(city);
          
          PreparedStatement countryStmt = DBConnection.conn.prepareStatement("Select * from country where countryId = ?");
          countryStmt.setString(1, citySet.getString("countryId"));
          countrySet = countryStmt.executeQuery();
          
          if(countrySet.first()){
            Country country = new Country();
            country.setCountry(countrySet.getString("country"));
            country.setCountryId(Integer.parseInt(countrySet.getString("countryId")));
            
            cust.setCountry(country);
          }
          
        }
        
      }
      
      customers.add(cust);
    }
    
    custTbl.setItems(customers);
    custTbl.getSelectionModel().selectFirst();
    
  }
  
  @FXML
  private void viewAddCustStage() throws IOException{
    FXMLLoader addCustLoader = new FXMLLoader(getClass().getResource("/view/AddCust.fxml"));
    addCustLoader.setResources(rb);
    Parent addCustParent = (Parent) addCustLoader.load();
    Scene addCustScene = new Scene(addCustParent);

    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(addCustScene);
    stage.setTitle("Add Customer");
    stage.show();

  }
  
  @FXML
  private void viewModCustStage() throws IOException{
    
    FXMLLoader modCustLoader = new FXMLLoader(getClass().getResource("/view/AddCust.fxml"));
    modCustLoader.setResources(rb);
    Parent modCustParent = (Parent) modCustLoader.load();
    Scene modCustScene = new Scene(modCustParent);
    
    CustController custController = modCustLoader.getController();
    custController.setCustomer(custTbl.getSelectionModel().getSelectedItem());
    
    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(modCustScene);
    stage.setTitle("Modify Customer");
    stage.show();

  }
  
  @FXML
  public void deleteCust() throws SQLException, ClassNotFoundException, IOException {
    
    Customer currentCust = custTbl.getSelectionModel().getSelectedItem();
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); 
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle("Confirm Delete");
    alert.setContentText("Are you sure you want to delete this customer?");
    alert.showAndWait();

    if(alert.getResult() == ButtonType.OK){
      
      DBConnection.connect();
      
      
      ////DELETE APPOINTMENT BASED ON CUST ID
      
      
      
      PreparedStatement deleteCustStmt = DBConnection.conn.prepareStatement("delete from customer "
              + "where customerId = ?");
      deleteCustStmt.setString(1, currentCust.getCustomerId());
      deleteCustStmt.executeUpdate();
      
      PreparedStatement deleteAddrStmt = DBConnection.conn.prepareStatement("delete from address "
              + "where addressId = ?");
      deleteAddrStmt.setString(1, Integer.toString(currentCust.getAddress().getAddressId()));
      deleteAddrStmt.executeUpdate();
      
      DBConnection.disconnect();
      
      customers.remove(currentCust);
      
    }else{
      alert.close();
    }
    
  }

}
