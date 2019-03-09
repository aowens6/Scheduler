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
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
  public TableView<Customer> custTbl;
  
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
  
  @FXML
  private ToggleGroup filterTG;
  
  @FXML
  private RadioButton monthRb, weekRb, allRb, checkedBtn;
  
  private ResourceBundle rb;
  
  public static ObservableList<Customer> customers = FXCollections.observableArrayList();
  public static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
  public static FilteredList<Appointment> filteredAppts;

  
  private final DateTimeFormatter dtFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
  private final ZoneId localZoneId = ZoneId.systemDefault();
  private LocalDate sunday;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    
    getLastWeekDay();
    
    try{
      getAllCustomers();
      getAllAppts();
    }catch (SQLException e){
      e.printStackTrace();
    }
    
    
    filteredAppts = new FilteredList<>(appointments, p -> true);
    
    filterTG.selectedToggleProperty().addListener( new ChangeListener<Toggle>() {
      public void changed(ObservableValue<? extends Toggle> ov,
          Toggle oldToggle, Toggle newToggle) {
        
        checkedBtn = (RadioButton) newToggle.getToggleGroup().getSelectedToggle(); 

        filteredAppts.setPredicate(appt -> {
          if(checkedBtn.equals(allRb)){
            return true;
          } else if (checkedBtn.equals(monthRb) && LocalDate.parse(appt.getStart(), dtFormat)
                                                .isAfter(LocalDate.now().withDayOfMonth(1)) &&
                                        LocalDate.parse(appt.getStart(), dtFormat)
                                                .isBefore(LocalDate.now().withDayOfMonth(31))){
            
            return true;
          } else if(checkedBtn.equals(weekRb) && LocalDate.parse(appt.getStart(), dtFormat)
                                                .isAfter(LocalDate.now()) &&
                                        LocalDate.parse(appt.getStart(), dtFormat)
                                                .isBefore(sunday)){
            return true;
          }

          return false;
        });

      }
      
       
    });
    
    SortedList<Appointment> sortedAppts = new SortedList<>(filteredAppts);

    sortedAppts.comparatorProperty().bind(apptTbl.comparatorProperty());

    apptTbl.setItems(sortedAppts);
    
//    TimeZone.setDefault(TimeZone.getTimeZone("PST"));
    
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
    
    apptWarning();
    
  } 

  public void apptWarning(){
    
    LocalDateTime now = LocalDateTime.now(localZoneId);
    LocalDateTime later = LocalDateTime.now(localZoneId).plusMinutes(15);
    
    for(Appointment appt : appointments){
      if(LocalDateTime.parse(appt.getStart(), dtFormat).isAfter(now) &&
              LocalDateTime.parse(appt.getStart(), dtFormat).isBefore(later)){
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Approaching Appointment");
        alert.setContentText("An appointment for " + appt.getCustomer().getCustomerName() + " will happen in 15 minutes");
        alert.showAndWait();
        return;
      }
    }

  }
  
  public void getLastWeekDay(){
    LocalDate today = LocalDate.now();

    // Go forward to get Sunday
    sunday = today;
    while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
      sunday = sunday.plusDays(1);
    }

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
      
      appointment.setAppointmentId(appointmentSet.getString("appointment.appointmentId"));
      appointment.setTitle(appointmentSet.getString("appointment.title"));
      appointment.setDescription(appointmentSet.getString("appointment.description"));
      appointment.setLocation(appointmentSet.getString("appointment.location"));
      
      
      Timestamp startTime = appointmentSet.getTimestamp("appointment.start");
      ZonedDateTime startZDT = startTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
      ZonedDateTime localStart = startZDT.withZoneSameInstant(localZoneId);
      
      Timestamp endTime = appointmentSet.getTimestamp("appointment.end");
      ZonedDateTime endZDT = endTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
      ZonedDateTime localEnd = endZDT.withZoneSameInstant(localZoneId);

      appointment.setStart(localStart.format(dtFormat));
      appointment.setEnd(localEnd.format(dtFormat));
      appointment.setUserID(appointmentSet.getInt("appointment.userId"));

      appointments.add(appointment);
    }
    
    apptTbl.setItems(appointments);
    apptTbl.getSelectionModel().selectFirst();
    
  }

  @FXML
  private void viewAddCustStage() throws IOException{
    FXMLLoader addCustLoader = new FXMLLoader(getClass().getResource("/view/Customers.fxml"));
    addCustLoader.setResources(rb);
    Parent addCustParent = (Parent) addCustLoader.load();
    Scene addCustScene = new Scene(addCustParent);

    Stage stage = new Stage();
    
    CustController custController = addCustLoader.getController();
    stage.setOnCloseRequest(event -> custController.cancel(event));

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(addCustScene);
    stage.setTitle("Add Customer");
    stage.show();

  }
  
  @FXML
  private void viewModCustStage() throws IOException{
    
    FXMLLoader modCustLoader = new FXMLLoader(getClass().getResource("/view/Customers.fxml"));
    modCustLoader.setResources(rb);
    Parent modCustParent = (Parent) modCustLoader.load();
    Scene modCustScene = new Scene(modCustParent);

    Stage stage = new Stage();
    
    CustController custController = modCustLoader.getController();
    custController.setCustomer(custTbl.getSelectionModel().getSelectedItem(),
                               custTbl.getSelectionModel().getSelectedIndex());
    stage.setOnCloseRequest(event -> custController.cancel(event));

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(modCustScene);
    stage.setTitle("Modify Customer");
    stage.show();

  }
  
  @FXML
  public void deleteCust() throws SQLException, ClassNotFoundException, IOException {
    
    Customer currentCust = custTbl.getSelectionModel().getSelectedItem();
    ObservableList<Appointment> removeAppts = FXCollections.observableArrayList();
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); 
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle("Confirm Delete");
    alert.setContentText("Are you sure you want to delete this customer?");
    alert.showAndWait();

    if(alert.getResult() == ButtonType.OK){
      
      DBConnection.connect();
      
      PreparedStatement deleteApptStmt = DBConnection.conn.prepareStatement("delete from appointment "
              + "where customerId = ?");
      deleteApptStmt.setString(1, currentCust.getCustomerId());
      deleteApptStmt.executeUpdate();
      
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
      
      
      for(Appointment appt : appointments){
        if(appt.getCustomer().getCustomerId().equals(currentCust.getCustomerId())){
          removeAppts.add(appt);
        }
      }
      
      appointments.removeAll(removeAppts);
      
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
    
    ApptController apptController = apptLoader.getController();
    apptController.setCustomer(custTbl.getSelectionModel().getSelectedItem());

    Stage stage = new Stage();
    
    ApptController addController = apptLoader.getController();
    stage.setOnCloseRequest(event -> addController.cancel(event));

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
    
    ApptController apptController = apptLoader.getController();
    apptController.setAppt(apptTbl.getSelectionModel().getSelectedItem(),
                           apptTbl.getSelectionModel().getSelectedIndex());
    
    Stage stage = new Stage();
    
    ApptController modController = apptLoader.getController();
    stage.setOnCloseRequest(event -> modController.cancel(event));

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(apptScene);
    stage.setTitle("Modify Appointment");
    stage.show();
    
  }
  
  @FXML
  private void deleteAppt() throws SQLException, ClassNotFoundException, IOException{
    
    Appointment currAppt = apptTbl.getSelectionModel().getSelectedItem();
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); 
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle("Confirm Delete");
    alert.setContentText("Are you sure you want to delete this appointment?");
    alert.showAndWait();

    if(alert.getResult() == ButtonType.OK){
      
      DBConnection.connect();

      PreparedStatement deleteCustStmt = DBConnection.conn.prepareStatement("delete from appointment "
              + "where appointmentId = ?");
      deleteCustStmt.setString(1, currAppt.getAppointmentId());
      deleteCustStmt.executeUpdate();
      
      
      DBConnection.disconnect();
      
      appointments.remove(currAppt);
      
    }else{
      alert.close();
    }
  }
  
  @FXML
  private void viewApptByType() throws SQLException{
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement(
      "select month(start), count(*), description " +
      "from appointment " +
      "group by month(start), description");
    
    ResultSet rs = stmt.executeQuery();

    Stage stage = new Stage();

    stage.setTitle("Appointments by Type and Month");
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final BarChart<String,Number> bc = 
        new BarChart<String,Number>(xAxis,yAxis);
    bc.setTitle("Appointment Summary");
    xAxis.setLabel("Month");       
    yAxis.setLabel("Total");

    XYChart.Series series1 = new XYChart.Series();
    series1.setName("First Meeting");          

    XYChart.Series series2 = new XYChart.Series();
    series2.setName("First Consultation");  

    XYChart.Series series3 = new XYChart.Series();
    series3.setName("Follow-up");

    while(rs.next()){
      
      if(rs.getString("description").trim().equalsIgnoreCase("first meeting")){
        
        series1.getData().add(new XYChart.Data(
                Month.of(rs.getInt("month(start)")).toString(), 
                rs.getInt("count(*)")));
        
      } else if(rs.getString("description").trim().equalsIgnoreCase("first consultation")){
        
        series2.getData().add(new XYChart.Data(
                Month.of(rs.getInt("month(start)")).toString(), 
                rs.getInt("count(*)")));
        
      }else if(rs.getString("description").trim().equalsIgnoreCase("follow-up")){
        
        series3.getData().add(new XYChart.Data(
                Month.of(rs.getInt("month(start)")).toString(), 
                rs.getInt("count(*)")));
      }
      
    }
    
    Scene scene  = new Scene(bc,800,600);
    bc.getData().addAll(series1, series2, series3);
    stage.setScene(scene);
    stage.show();
    
  }
  
  @FXML
  private void viewConsultSchedules() throws IOException{
    FXMLLoader consultLoader = new FXMLLoader(getClass().getResource("/view/Consultants.fxml"));
    consultLoader.setResources(rb);
    Parent consultParent = (Parent) consultLoader.load();
    Scene consultScene = new Scene(consultParent);
    
    Stage stage = new Stage();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(consultScene);
    stage.setTitle("Modify Appointment");
    stage.show();
  }
  
  @FXML
  private void viewApptByCountry() throws SQLException {
    
    PreparedStatement stmt = DBConnection.conn.prepareStatement(
      "select country.country, count(appointmentId) " +
      "from country, appointment, customer, address, city " +
      "where appointment.customerId = customer.customerId " +
        "and customer.addressId = address.addressId " +
        "and address.cityId = city.cityId " +
        "and city.countryId = country.countryId " +
      "group by country");
    
    ResultSet rs = stmt.executeQuery();

    ObservableList<PieChart.Data> pieChartData =
            FXCollections.observableArrayList();
    
    while(rs.next()){
      PieChart.Data piece = new PieChart.Data(rs.getString("country"), rs.getInt("count(appointmentId)"));
      pieChartData.add(piece);
    }
    
    pieChartData.forEach(data ->
      data.nameProperty().bind(
        Bindings.concat(
                data.getName(), " ", data.pieValueProperty().intValue()
        )
      )
    );
    
    final PieChart chart = new PieChart(pieChartData);
    chart.setTitle("Appointments by Customer Country");
    
    Stage stage = new Stage();
    Scene scene = new Scene(new Group());

    ((Group) scene.getRoot()).getChildren().add(chart);
    stage.setScene(scene);
    stage.setTitle("Appointments by Customer Country");
    stage.setWidth(500);
    stage.setHeight(500);
    stage.show();
    
  }
  
  @FXML
  public void exitProgram(Event e){
    
    e.consume();
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); 
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setTitle("Confirm exit");
    alert.setContentText("Are you sure you want to exit?");
    alert.showAndWait();
    
    if(alert.getResult() == ButtonType.OK){
      Platform.exit();
    }else{
      alert.close();
    }
    
  }
  
}
