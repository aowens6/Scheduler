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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Address;
import model.City;
import model.Country;
import model.Customer;
import util.DBConnection;

/**
 * FXML Controller class
 *
 * @author Austyn
 */
public class CustController implements Initializable {

  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Label idLbl, nameLbl, addrLbl, phoneLbl;
  
  @FXML
  private TextField addId, addName, addAddress, addCity, addCountry, addPhone;
  
  @FXML
  private Button saveBtn, cancelBtn;
  
  private int nameCtr = 1;
  
  private boolean isModifying;
  
  private Customer modCustomer;
  
  private int modCustIndex;
  
  private String origName, origAddr, origCity, origCountry, origPhone;
          
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    
    addName.setText("New Cust" + Integer.toString(nameCtr++));
    addAddress.setText("123 internet street");
    addCity.setText("CityVille");
    addCountry.setText("Blagdon");
    addPhone.setText("8882222-" + Integer.toString(nameCtr++));
    
  }  
  
  public void setCustomer(Customer customer, int index){
    isModifying = true;
    
    modCustomer = customer;
    modCustIndex = index;
    
    origName = customer.getCustomerName();
    origAddr = customer.getAddress().getAddress();
    origCity = customer.getCity().getCity();
    origCountry = customer.getCountry().getCountry();
    origPhone = customer.getAddress().getPhone();
    
    addId.setText(customer.getCustomerId());
    addName.setText(origName);
    addAddress.setText(origAddr);
    addCity.setText(origCity);
    addCountry.setText(origCountry);
    addPhone.setText(origPhone);
    
  }
  
  private boolean isValidInput(){
    boolean validInput = true;
    
    if (addName.getText().trim().equals("") ||
        addAddress.getText().trim().equals("") ||
        addCity.getText().trim().equals("") ||
        addCountry.getText().trim().equals("") ||
        addPhone.getText().trim().equals("")){
      
      validInput = false;
      
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Missing Value");
      alert.setHeaderText("At least one of the inputs is missing a value");
      alert.setContentText("All fields are mandatory.");
      alert.showAndWait();
      
    }
    
    return validInput;
  }
  
  @FXML
  private void saveCust() throws SQLException, ClassNotFoundException, IOException{
    
    ResultSet country = null;
    ResultSet city = null;
    ResultSet address = null;
    ResultSet customer = null;
    
    if(isValidInput()){
      
      DBConnection.connect();

      PreparedStatement findCountryStmt = DBConnection.conn.prepareStatement("select * from country where "
              + "country = ?");
      findCountryStmt.setString(1, addCountry.getText().trim());
      country = findCountryStmt.executeQuery();

      //If the country doesn't exist, create a new country record
      if(!country.next()){
        PreparedStatement addCountryStmt = DBConnection.conn.prepareStatement("insert into country "
                + "(country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                + "values(?, CURRENT_TIMESTAMP,?, CURRENT_TIMESTAMP, ?)");
        
        addCountryStmt.setString(1,  addCountry.getText().trim());
        addCountryStmt.setString(2,  LoginController.currentUser.getUsername());
        addCountryStmt.setString(3,  LoginController.currentUser.getUsername());
        addCountryStmt.executeUpdate();

        PreparedStatement findNewCountryStmt = DBConnection.conn.prepareStatement("select * from country where "
              + "country = ?");
        findNewCountryStmt.setString(1, addCountry.getText().trim());
        country = findNewCountryStmt.executeQuery();

      }
      
      country.first();

      PreparedStatement findCityStmt = DBConnection.conn.prepareStatement("select * from city where "
            + "city = ? and countryId = ?");
      findCityStmt.setString(1, addCity.getText().trim());
      findCityStmt.setString(2, country.getString("countryId"));
      city = findCityStmt.executeQuery();

      if(!city.next()){

        PreparedStatement addCityStmt = DBConnection.conn.prepareStatement("insert into city "
                + "(city, countryId, createDate, createdBy, lastUpdateBy) "
                + "values(?, ?, CURRENT_TIMESTAMP, ?, ?)");
        addCityStmt.setString(1, addCity.getText().trim());
        addCityStmt.setString(2, country.getString("countryId"));
        addCityStmt.setString(3, LoginController.currentUser.getUsername());
        addCityStmt.setString(4, LoginController.currentUser.getUsername());
        addCityStmt.executeUpdate();

        PreparedStatement findNewCityStmt = DBConnection.conn.prepareStatement("select * from city where "
            + "city = ?");
        findNewCityStmt.setString(1, addCity.getText().trim());
        city = findNewCityStmt.executeQuery();

      }//if !city.next()
      
      city.first();
      
      if(isModifying){
        PreparedStatement updateAddrStmt = DBConnection.conn.prepareStatement("update address"
          + " set address = ?, phone = ?, cityId = ?"
          + " where addressId = ?");
        updateAddrStmt.setString(1, addAddress.getText().trim());
        updateAddrStmt.setString(2, addPhone.getText().trim());
        updateAddrStmt.setString(3, city.getString("cityId"));
        updateAddrStmt.setString(4, Integer.toString(modCustomer.getAddress().getAddressId()));
        updateAddrStmt.executeUpdate();
        
        PreparedStatement findAddrStmt = DBConnection.conn.prepareStatement("select * from address where "
                + "addressId = ?");
        findAddrStmt.setString(1, Integer.toString(modCustomer.getAddress().getAddressId()));
        address = findAddrStmt.executeQuery();
        
      }else{
        
        PreparedStatement addAddrStmt = DBConnection.conn.prepareStatement("insert into address "
          + "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) "
          + "values(?, '', ?, 99999,?, CURRENT_TIMESTAMP,?,?)");
        addAddrStmt.setString(1, addAddress.getText().trim());
        addAddrStmt.setString(2, city.getString("cityId"));
        addAddrStmt.setString(3, addPhone.getText().trim());
        addAddrStmt.setString(4, LoginController.currentUser.getUsername());
        addAddrStmt.setString(5, LoginController.currentUser.getUsername());
        addAddrStmt.executeUpdate();

        PreparedStatement findNewAddrStmt = DBConnection.conn.prepareStatement("select * from address where "
                + "addressId = (select max(addressId) from address)");
        address = findNewAddrStmt.executeQuery();
        
      }

      address.first();

      if(isModifying){
        
        PreparedStatement updateCustStmt = DBConnection.conn.prepareStatement("update customer"
                + " set customerName = ?, addressId = ?, lastUpdateBy = ?"
                + " where customerId = ?");
        updateCustStmt.setString(1, addName.getText().trim());
        updateCustStmt.setString(2, address.getString("addressId"));
        updateCustStmt.setString(3, LoginController.currentUser.getUsername());
        updateCustStmt.setString(4, modCustomer.getCustomerId());
        updateCustStmt.executeUpdate();
        
        PreparedStatement findUpdatedCustStmt = DBConnection.conn.prepareStatement("Select * from customer where "
                + "customerId = ?");
        findUpdatedCustStmt.setString(1, modCustomer.getCustomerId());
        customer = findUpdatedCustStmt.executeQuery();

      }else{

        PreparedStatement addCustStmt = DBConnection.conn.prepareStatement("insert into customer "
          + "(customerName, active, addressId, createDate, createdBy, lastUpdateBy) "
          + "values(?, 1, ?, CURRENT_TIMESTAMP, ?, ?)");
        addCustStmt.setString(1, addName.getText().trim());
        addCustStmt.setString(2, address.getString("addressId"));
        addCustStmt.setString(3, LoginController.currentUser.getUsername());
        addCustStmt.setString(4, LoginController.currentUser.getUsername());
        addCustStmt.executeUpdate();

        PreparedStatement findNewCustStmt = DBConnection.conn.prepareStatement("Select * from customer where "
                + "customerId = (select max(customerId) from customer)");
        customer = findNewCustStmt.executeQuery();
      
      }
      
      customer.first();
      
      Country custCountry = new Country();
      custCountry.setCountryId(Integer.parseInt(country.getString("countryId")));
      custCountry.setCountry(country.getString("country"));
      
      City custCity = new City();
      custCity.setCityId(Integer.parseInt(city.getString("cityId")));
      custCity.setCity(city.getString("city"));
      custCity.setCountryId(Integer.parseInt(city.getString("countryId")));
      
      Address custAddr = new Address();
      custAddr.setAddressId(Integer.parseInt(address.getString("addressId")));
      custAddr.setAddress(address.getString("address"));
      custAddr.setPhone(address.getString("phone"));

      Customer cust = new Customer();
      cust.setCustomerId(customer.getString("customerId"));
      cust.setCustomerName(customer.getString("customerName"));
      cust.setCity(custCity);
      cust.setCountry(custCountry);
      cust.setAddress(custAddr);
      
      if(isModifying){
        SchedulesController.customers.set(modCustIndex, cust);
      }else{
        SchedulesController.customers.add(cust);
      }

      DBConnection.disconnect();

      Stage stage = (Stage) anchorPane.getScene().getWindow();
      stage.close();
    }
    
  }
  
  @FXML
  public void cancel(Event e) {
    
    e.consume();

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); 
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle("Confirm Cancel");
    alert.setContentText("Are you sure you want to leave without saving?");
    alert.showAndWait();

    if(alert.getResult() == ButtonType.OK){
      Stage stage = (Stage) anchorPane.getScene().getWindow();
      stage.close();
    }else{
      alert.close();
    }
    
  }
}
