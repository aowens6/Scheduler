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
public class Country {
  
  private Integer countryId;
  private String country;


  public Country() {
  }

  public Country(Integer countryId) {
      this.countryId = countryId;
  }

  public Country(Integer countryId, String country) {
      this.countryId = countryId;
      this.country = country;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

}
