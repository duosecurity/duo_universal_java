package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class AuthDevice implements Serializable {
  private static final long serialVersionUID = -1538823399834806194L;

  private String ip;
  private String name;
  private Location location;

  /**
   * Constructor with all properties.
   * 
   * @param ip  ip
   * @param name  name
   * @param location  location
   */
  public AuthDevice(String ip, String name, Location location) {
    this.ip = ip;
    this.name = name;
    this.location = location;
  }

  public AuthDevice() {
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public String toString() {
    return "AuthDevice [ip=" + ip
        + ", name=" + name
        + ", location=" + location
        + ", getIp()=" + getIp()
        + ", getLocation()=" + getLocation()
        + ", getName()=" + getName()
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
    AuthDevice other = (AuthDevice) obj;
    return Objects.equals(ip, other.ip)
        && Objects.equals(name, other.name)
        && Objects.equals(location, other.location);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ip == null) ? 0 : ip.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    return result;
  }
}
