
package controller;

import static controller.SchedulesController.appointments;
import static controller.SchedulesController.customers;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import util.DBConnection;

public class ApptController implements Initializable {
  
  @FXML
  private AnchorPane anchorPane;
  
  @FXML
  public Button saveBtn;
  
  @FXML
  private ComboBox titleCbx, descCbx, locCbx, startCbx, endCbx;
  
  @FXML
  private DatePicker datePicker;
  
  @FXML
  private TextField custTxt;
  
  private Customer customer;
  private Appointment appt;
  private int index;
  
  private final DateTimeFormatter dtFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
  private final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
  private final ZoneId localZoneId = ZoneId.systemDefault();
  
  private List<LocalTime> startIntervals = new ArrayList<>();
  private List<LocalTime> endIntervals = new ArrayList<>();
  private LocalDateTime originalStart, originalEnd;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    titleCbx.getItems().addAll("Meeting","Consulting");
    descCbx.getItems().addAll("First Meeting", "First Consultation", "Follow-up");
    locCbx.getItems().addAll("Phoenix, Arizona", "London, England", "New York, New York", "Online");
    
    LocalDate tomorrow = LocalDate.now().plusDays(1);

    LocalTime startTime = LocalTime.of(9, 00, 0);
    LocalTime endTime = LocalTime.of(17, 0, 0);
    LocalTime indexTime = LocalTime.of(9, 0, 0);
     
    while (indexTime.isBefore(endTime)){
      startIntervals.add(indexTime);
      indexTime = indexTime.plusMinutes(30);
    }
  
    startCbx.getItems().addAll(startIntervals);
    
    indexTime = LocalTime.of(9, 30, 0);
    
    while (indexTime.isAfter(startTime) && indexTime.isBefore(endTime)){
      endIntervals.add(indexTime);
      indexTime = indexTime.plusMinutes(30);
    }
    
    endCbx.getItems().addAll(endIntervals);
    
    titleCbx.getSelectionModel().selectFirst();
    descCbx.getSelectionModel().selectFirst();
    locCbx.getSelectionModel().selectFirst();
    datePicker.setValue(tomorrow);
    startCbx.getSelectionModel().selectFirst();
    endCbx.getSelectionModel().selectFirst();

  }
  
  public void setCustomer(Customer customer){
    this.customer = customer;
    custTxt.setText(customer.getCustomerName());
  }
  
  public void setAppt(Appointment appt, int index){
    
    saveBtn.setOnAction(e ->  {
      try{
        saveModAppt();
      }catch(SQLException | ClassNotFoundException | IOException ex){
        ex.printStackTrace();
      }
    });

    this.appt = appt;
    this.index = index;
    
    originalStart = LocalDateTime.parse(appt.getStart(), dtFormat);
    originalEnd = LocalDateTime.parse(appt.getEnd(), dtFormat);
    
    ZonedDateTime startZDT = originalStart.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));
    ZonedDateTime endZDT = originalEnd.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));

    custTxt.setText(appt.getCustomer().getCustomerName());

    startCbx.getItems().add(0, startZDT.toLocalTime());
    endCbx.getItems().add(0, endZDT.toLocalTime());

    titleCbx.getSelectionModel().select(appt.getTitle());
    descCbx.getSelectionModel().select(appt.getDescription());
    locCbx.getSelectionModel().select(appt.getLocation());
    datePicker.setValue(LocalDate.parse(appt.getStart(), dtFormat));
    startCbx.getSelectionModel().selectFirst();
    endCbx.getSelectionModel().selectFirst();
    
  }
  
  @FXML
  public void saveNewAppt() throws SQLException, ClassNotFoundException, IOException{
    
    ResultSet apptSet = null;
    
    DBConnection.connect();

    LocalDateTime startDateTime = LocalDateTime.of(datePicker.getValue(),
       LocalTime.parse(startCbx.getSelectionModel().getSelectedItem().toString()) );
    
    LocalDateTime endDateTime = LocalDateTime.of(datePicker.getValue(),
      LocalTime.parse(endCbx.getSelectionModel().getSelectedItem().toString()) );

    if (isValidTime(startDateTime, endDateTime)){

      PreparedStatement insertApptStmt = DBConnection.conn.prepareStatement("insert into appointment "
        + "(customerId,title,description,location,contact,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy) "
        + "values(?,?,?,?,?,'',?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?)");
      insertApptStmt.setString(1, customer.getCustomerId());                                  //CUST ID
      insertApptStmt.setString(2, titleCbx.getSelectionModel().getSelectedItem().toString()); //TITLE
      insertApptStmt.setString(3, descCbx.getSelectionModel().getSelectedItem().toString());  //DESC
      insertApptStmt.setString(4, locCbx.getSelectionModel().getSelectedItem().toString());   //LOCATION
      insertApptStmt.setString(5, LoginController.currentUser.getUsername());                 //CONTACT
                                                                                              //URL
      insertApptStmt.setString(6, startDateTime.toString());                                  //START
      insertApptStmt.setString(7, endDateTime.toString());                                    //END
                                                                                              //CREATEDATE
      insertApptStmt.setString(8, LoginController.currentUser.getUsername());                 //CREATEDBY
                                                                                              //LASTUPDATE
      insertApptStmt.setString(9, LoginController.currentUser.getUsername());                 //LASTUPDATEBY

      insertApptStmt.executeUpdate();


      PreparedStatement findNewApptStmt = DBConnection.conn.prepareStatement("select * from appointment "
              + "where appointmentId = (select max(appointmentId) from appointment)");

      apptSet = findNewApptStmt.executeQuery();


      if(apptSet.first()){

        Appointment appointment = new Appointment();

        for(Customer cust : customers){
          if (cust.getCustomerId().equals(apptSet.getString("customerId"))){
            appointment.setCustomer(cust);
          }
        }

        appointment.setAppointmentId(apptSet.getString("appointmentId"));
        appointment.setTitle(apptSet.getString("title"));
        appointment.setDescription(apptSet.getString("description"));
        appointment.setLocation(apptSet.getString("location"));


        Timestamp startTime = apptSet.getTimestamp("start");
        ZonedDateTime startZDT = startTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime localStart = startZDT.withZoneSameInstant(localZoneId);

        Timestamp endTime = apptSet.getTimestamp("end");
        ZonedDateTime endZDT = endTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime localEnd = endZDT.withZoneSameInstant(localZoneId);

        appointment.setStart(localStart.format(dtFormat));
        appointment.setEnd(localEnd.format(dtFormat));

        appointments.add(appointment);

        DBConnection.disconnect();

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
      }
    }
    
  }
  
  @FXML
  public void saveModAppt() throws SQLException, ClassNotFoundException, IOException{
    
    ResultSet apptSet = null;
    boolean isStartModified = false;
    boolean isEndModified = false;
    
    DBConnection.connect();
    
//    //processing start time for timezones in database
//    LocalDate localDate = datePicker.getValue();
//    LocalTime startTime = LocalTime.parse(
//            startCbx.getSelectionModel().getSelectedItem().toString());
//    LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
//    ZonedDateTime startZDT = startDT.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));
//    Timestamp startTimestamp = Timestamp.valueOf(startZDT.toLocalDateTime());
//    
//    //processing end time for timezone in database
//    LocalTime endTime = LocalTime.parse(
//            endCbx.getSelectionModel().getSelectedItem().toString());
//    LocalDateTime endDT = LocalDateTime.of(localDate, endTime);
//    ZonedDateTime endZDT = endDT.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));            
//    Timestamp endTimestamp = Timestamp.valueOf(endZDT.toLocalDateTime());
//    
//    
//    if(!originalStart.toLocalTime().equals(startTime) || 
//       !originalEnd.toLocalTime().equals(endTime) ){
//      isTimeModified = true;
//    }

    LocalDateTime startDT = LocalDateTime.of(datePicker.getValue(),
       LocalTime.parse(startCbx.getSelectionModel().getSelectedItem().toString()) );
    
    LocalDateTime endDT = LocalDateTime.of(datePicker.getValue(),
      LocalTime.parse(endCbx.getSelectionModel().getSelectedItem().toString()) );

    if (isValidTime(startDT, endDT, Integer.parseInt(appt.getAppointmentId()))){

      PreparedStatement updateApptStmt = DBConnection.conn.prepareStatement("update appointment "
        + "set title = ?, description = ?, location = ?, "
        + "start = ?, end = ?, "
        + "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
        + "where appointmentId = ?");

      updateApptStmt.setString(1, titleCbx.getSelectionModel().getSelectedItem().toString());
      updateApptStmt.setString(2, descCbx.getSelectionModel().getSelectedItem().toString());
      updateApptStmt.setString(3, locCbx.getSelectionModel().getSelectedItem().toString());
//      updateApptStmt.setString(4, startTimestamp.toString());
//      updateApptStmt.setString(5, endTimestamp.toString());
      updateApptStmt.setString(4, startDT.toString());
      updateApptStmt.setString(5, endDT.toString());
      updateApptStmt.setString(6, LoginController.currentUser.getUsername());
      updateApptStmt.setString(7, appt.getAppointmentId());

      updateApptStmt.executeUpdate();

      PreparedStatement findUpdApptStmt = DBConnection.conn.prepareStatement("select * from appointment "
              + "where appointmentId = ?");
      findUpdApptStmt.setString(1, appt.getAppointmentId());

      apptSet = findUpdApptStmt.executeQuery();

      if(apptSet.first()){

        appt.setAppointmentId(apptSet.getString("appointmentId"));
        appt.setTitle(apptSet.getString("title"));
        appt.setDescription(apptSet.getString("description"));
        appt.setLocation(apptSet.getString("location"));

        Timestamp localStartTS = apptSet.getTimestamp("start");
        ZonedDateTime localStartZDT = localStartTS.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime localStart = localStartZDT.withZoneSameInstant(localZoneId);

        Timestamp localEndTS = apptSet.getTimestamp("end");
        ZonedDateTime localEndZDT = localEndTS.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime localEnd = localEndZDT.withZoneSameInstant(localZoneId);

        appt.setStart(localStart.format(dtFormat));
        appt.setEnd(localEnd.format(dtFormat));

        appointments.set(index, appt);

        DBConnection.disconnect();

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
      }
    }
  }

//  private boolean isValidData(Appointment newAppt){
//    boolean validData = true;
//    
//    LocalDateTime newStart = LocalDateTime.parse(newAppt.getStart(), dtFormat);
//    LocalDateTime newEnd = LocalDateTime.parse(newAppt.getEnd(), dtFormat);
//    
//    for(Appointment appt : appointments){
//      
//      LocalDateTime existingStart = LocalDateTime.parse(appt.getStart(), dtFormat);
//      LocalDateTime existingEnd = LocalDateTime.parse(appt.getEnd(), dtFormat);
//
//      if(newStart.equals(existingStart) || newEnd.equals(existingEnd)){
//        
//        validData = false;
//        
//        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
//        alert.initModality(Modality.APPLICATION_MODAL);
//        alert.setTitle("Overlapping Appointment");
//        alert.setContentText("This time is already assigned");
//        alert.showAndWait();
//        
//      }
//    }
//    
//    return validData;
//  }
  
  private boolean isValidTime(LocalDateTime startTime, LocalDateTime endTime){
    
    boolean validTime = true;
    
    if(startTime.toLocalTime().equals(endTime.toLocalTime())){
      
      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Invalid Times");
      alert.setContentText("Start and end cannot be equal");
      alert.showAndWait();
      
      return validTime = false;
      
    }
    
    if(startTime.toLocalTime().isAfter(endTime.toLocalTime())){

      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Approaching Appointment");
      alert.setContentText("The starting time needs to precede the ending time");
      alert.showAndWait();
      
      return validTime = false;
    }
    
    if(startTime.toLocalTime().isBefore(LocalTime.of(9, 00, 0)) || 
       endTime.toLocalTime().isAfter(LocalTime.of(17, 00, 0))){

      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Approaching Appointment");
      alert.setContentText("Times need to be after 09:00 and before 17:00");
      alert.showAndWait();
      
      return validTime = false;
    }
    
    for(Appointment appt : appointments){
      
      LocalDateTime existingStart = LocalDateTime.parse(appt.getStart(), dtFormat);
      LocalDateTime existingEnd = LocalDateTime.parse(appt.getEnd(), dtFormat);
      
      ZonedDateTime startZDT = existingStart.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));
      ZonedDateTime endZDT = existingEnd.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));

      if(startTime.equals(startZDT.toLocalDateTime()) || endTime.equals(endZDT.toLocalDateTime())){
        
        validTime = false;
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Overlapping Appointment");
        alert.setContentText("This time is already assigned");
        alert.showAndWait();
        
      }
    }
    
    return validTime;
  }
  
  private boolean isValidTime(LocalDateTime startTime, LocalDateTime endTime, int apptId){
    
    boolean validTime = true;
    
    if(startTime.toLocalTime().equals(endTime.toLocalTime())){
      
      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Invalid Times");
      alert.setContentText("Start and end cannot be equal");
      alert.showAndWait();
      
      return validTime = false;
      
    }
    
    if(startTime.toLocalTime().isAfter(endTime.toLocalTime())){

      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Approaching Appointment");
      alert.setContentText("The starting time needs to precede the ending time");
      alert.showAndWait();
      
      return validTime = false;
    }
    
    if(startTime.toLocalTime().isBefore(LocalTime.of(9, 00, 0)) || 
       endTime.toLocalTime().isAfter(LocalTime.of(17, 00, 0))){

      Alert alert = new Alert(Alert.AlertType.INFORMATION); 
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.setTitle("Approaching Appointment");
      alert.setContentText("Times need to be after 09:00 and before 17:00");
      alert.showAndWait();
      
      return validTime = false;
    }
    
    for(Appointment appt : appointments){
      
      LocalDateTime existingStart = LocalDateTime.parse(appt.getStart(), dtFormat);
      LocalDateTime existingEnd = LocalDateTime.parse(appt.getEnd(), dtFormat);
      
      //converting it to local time zone, timezone will then get truncated
      ZonedDateTime startZDT = existingStart.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));
      ZonedDateTime endZDT = existingEnd.atZone(localZoneId).withZoneSameInstant(ZoneId.of("UTC"));

      if(Integer.parseInt(appt.getAppointmentId()) != apptId && 
         (startTime.equals(startZDT.toLocalDateTime()) || endTime.equals(endZDT.toLocalDateTime()))){
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Overlapping Appointment");
        alert.setContentText("This time is already assigned");
        alert.showAndWait();
        
        return validTime = false;
        
      }
    }
    
    return validTime;
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
