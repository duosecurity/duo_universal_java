package com.duosecurity.model;

import java.io.Serializable;

public class AuthDevice implements Serializable {
  private static final long serialVersionUID = -1538823399834806194L;

  private String ip;
  private String name;
  private Location location;

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
}
