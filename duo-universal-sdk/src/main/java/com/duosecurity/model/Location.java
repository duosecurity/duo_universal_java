package com.duosecurity.model;

import java.io.Serializable;

public class Location implements Serializable {

  private static final long serialVersionUID = 8138286056784653212L;

  private String state;
  private String city;
  private String country;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
