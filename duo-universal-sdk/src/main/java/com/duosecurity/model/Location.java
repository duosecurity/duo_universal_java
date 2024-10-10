package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

  private static final long serialVersionUID = 8138286056784653212L;

  private String state;
  private String city;
  private String country;

  /**
   * Constructor with all properties.
   * 
   * @param state state
   * @param city  city
   * @param country country
   */
  public Location(String state, String city, String country) {
    this.state = state;
    this.city = city;
    this.country = country;
  }

  public Location() {
  }

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

  @Override
  public String toString() {
    return "Location [state=" + state
        + ", city=" + city
        + ", country=" + country
        + ", getCity()=" + getCity()
        + ", getCountry()=" + getCountry()
        + ", getState()=" + getState()
        + ", hashCode()=" + hashCode()
        + ", getClass()=" + getClass()
        + ", toString()=" + super.toString()
        + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Location other = (Location) obj;
    return Objects.equals(state, other.state)
        && Objects.equals(city, other.city)
        && Objects.equals(country, other.country);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    return result;
  }
}
