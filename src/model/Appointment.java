/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Austyn
 */
public class Appointment {
  
  private String appointmentId;
  private Customer customer;
  private String title;
  private String description;
  private String location;
  private String start;
  private String end;
  private int userID;

  public Appointment() {
  }

  public Appointment(String appointmentId) {
    this.appointmentId = appointmentId;
  }

  public Appointment(String appointmentId, String start, String end, String title, String description, Customer customer, int userID) {
    this.appointmentId = appointmentId;    
    this.start = start;
    this.end = end;
    this.title = title;
    this.description = description;
    this.customer = customer;
    this.userID = userID;
  }

  public String getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(String appointmentId) {
    this.appointmentId = appointmentId;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }
 
}
