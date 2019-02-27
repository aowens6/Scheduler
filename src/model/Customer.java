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
public class Customer {
  
  private String customerId;
  private String customerName;
  private Address address;
  private City city;
  private Country country;
  public Customer(){

  }

  public Customer(String customerId, String customerName, Address address, City city, Country country) {
      this.customerId = customerId;
      this.customerName = customerName;
      this.address = address;
      this.city = city;
      this.country = country;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
  
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public City getCity() {
    return city;
  }

  public void setCity(City city) {
    this.city = city;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

}
