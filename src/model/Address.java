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
public class Address {
  
  private int addressId;
  private String address;
  private int cityId;
  private String postalCode;
  private String phone;

  public Address() {}

  public Address(Integer addressId) {
      this.addressId = addressId;
  }

  public Address(Integer addressId, String address, String address2, int cityId, String postalCode, String phone) {
      this.addressId = addressId;
      this.address = address;
      this.cityId = cityId;
      this.postalCode = postalCode;
      this.phone = phone;
  }

  public Integer getAddressId() {
    return addressId;
  }

  public void setAddressId(Integer addressId) {
    this.addressId = addressId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getCityId() {
    return cityId;
  }

  public void setCityId(int cityId) {
    this.cityId = cityId;
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
