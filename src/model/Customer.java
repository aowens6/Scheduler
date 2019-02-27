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
  private String addressId;
  private String address;
  private String address2;
  private City city;
  private Country country;
  private String postalCode;
  private String phone;


  public Customer(){

  }

  public Customer(String customerId, String customerName, String addressId, String address, String address2, City city, Country country, String postalCode, String phone) {
      this.customerId = customerId;
      this.customerName = customerName;
      this.addressId = addressId;
      this.address = address;
      this.address2 = address2;
      this.city = city;
      this.country = country;
      this.postalCode = postalCode;
      this.phone = phone;
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

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = address;
  }
  
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address2 = address;
  }
  
  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
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

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
  
  
}
