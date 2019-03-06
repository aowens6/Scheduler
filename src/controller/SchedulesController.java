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
import model.Appointment;
import model.City;
import model.Country;
import model.Customer;
import util.DBConnection;

public class SchedulesController extends Thread implements Initializable{
  
  @FXML
  private TableView<Customer> custTbl;
  
  @FXML
  private TableView<Appointment> apptTbl;
  
  @FXML
  private TableColumn<Customer, String> nameCol;
  
  @FXML
  private TableColumn<Customer, String> phoneCol;
  
  @FXML
  private TableColumn<Customer, String> addressCol;
  
  @FXML
  private TableColumn<Appointment, String> apptCustCol,
            apptTitleCol, apptLocCol, apptDescCol, apptStartCol, apptEndCol;
  
  private ResourceBundle rb;
  
  public static ObservableList<Customer> customers = FXCollections.observableArrayList();
  public static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    
    this.rb = rb;
    
    nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    phoneCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getAddress().getPhone()));
    addressCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getAddress().getAddress()));
    
    apptCustCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
    apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
    apptDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    apptLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
    apptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
    
  } 
  
  public void getAllCustomers() throws SQLException{
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement(
      "select customer.customerId, customer.customerName, " +
        "address.addressId, address.address, " +
        "address.postalCode, address.phone, " +
        "city.cityId, city.city, city.countryId, " +
        "country.countryId, country.country " +
      "from customer, address, city, country " +
      "where customer.addressId = address.addressId " +
      "and address.cityId = city.cityId " +
      "and city.countryId = country.countryId ");
    
    ResultSet customerSet = stmt.executeQuery();
    
    
    
    while(customerSet.next()){
      
      Country custCountry = new Country();
      custCountry.setCountry(customerSet.getString("country.country"));
      custCountry.setCountryId(Integer.parseInt(customerSet.getString("country.countryId")));
      
      City custCity = new City();
      custCity.setCityId(Integer.parseInt(customerSet.getString("city.cityId")));
      custCity.setCity(customerSet.getString("city.city"));
      custCity.setCountryId(Integer.parseInt(customerSet.getString("city.countryId")));
      
      Address custAddr = new Address();
      custAddr.setAddress(customerSet.getString("address.address"));
      custAddr.setAddressId(Integer.parseInt(customerSet.getString("address.addressId")));
      custAddr.setPhone(customerSet.getString("address.phone"));
      
      Customer cust = new Customer();
      cust.setCustomerId(customerSet.getString("customer.customerId"));
      cust.setCustomerName(customerSet.getString("customer.customerName"));
      cust.setAddress(custAddr);
      cust.setCity(custCity);
      cust.setCountry(custCountry);
      
      customers.add(cust);
      
    }
    
    custTbl.setItems(customers);
    custTbl.getSelectionModel().selectFirst();
    
  }
  
  public void getAllAppts() throws SQLException{
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement(
      "select customer.customerName, customer.customerId, appointment.appointmentId, " +
      "appointment.title, appointment.description, appointment.location, " +
      "appointment.start, appointment.end, appointment.userId " +
      "from appointment, customer where customer.customerId = appointment.customerId");
    
    ResultSet appointmentSet = stmt.executeQuery();
    
    while (appointmentSet.next()){
      
      Appointment appointment = new Appointment();
      
      for(Customer cust : customers){
        if (cust.getCustomerId().equals(appointmentSet.getString("customer.customerId"))){
          appointment.setCustomer(cust);
        }
      }
      
      appointment.setTitle(appointmentSet.getString("appointment.title"));
      appointment.setDescription(appointmentSet.getString("appointment.description"));
      appointment.setLocation(appointmentSet.getString("appointment.location"));
      appointment.setStart(appointmentSet.getString("appointment.start"));
      appointment.setEnd(appointmentSet.getString("appointment.end"));

      appointments.add(appointment);
    }
    
    apptTbl.setItems(appointments);
    apptTbl.getSelectionModel().selectFirst();
    
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
    custController.setCustomer(custTbl.getSelectionModel().getSelectedItem(),
                               custTbl.getSelectionModel().getSelectedIndex());
    
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

  @FXML
  private void viewAddApptStage() throws IOException{
    
    FXMLLoader apptLoader = new FXMLLoader(getClass().getResource("/view/Appointments.fxml"));
    apptLoader.setResources(rb);
    Parent apptParent = (Parent) apptLoader.load();
    Scene apptScene = new Scene(apptParent);

    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(apptScene);
    stage.setTitle("Add Appointment");
    stage.show();
    
  }
  
  @FXML
  private void viewModApptStage() throws IOException{
    
    FXMLLoader apptLoader = new FXMLLoader(getClass().getResource("/view/Appointments.fxml"));
    apptLoader.setResources(rb);
    Parent apptParent = (Parent) apptLoader.load();
    Scene apptScene = new Scene(apptParent);

    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(apptScene);
    stage.setTitle("Modify Appointment");
    stage.show();
    
  }
  
  @FXML
  private void deleteAppt(){
    
  }
  
}
