package com.duosecurity.model;

import java.io.Serializable;

public class AccessDevice implements Serializable {
  private static final long serialVersionUID = -1130960392429229150L;

  private String hostname;
  private String ip;
  private Location location;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }
}
