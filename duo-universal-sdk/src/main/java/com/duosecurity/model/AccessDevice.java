package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class AccessDevice implements Serializable {
  private static final long serialVersionUID = -1130960392429229150L;

  private String hostname;
  private String ip;
  private Location location;

  
  /**
   * Constructor with all properties.
   * 
   * @param hostname  hostname
   * @param ip  ip
   * @param location  location
   */
  public AccessDevice(String hostname, String ip, Location location) {
    this.hostname = hostname;
    this.ip = ip;
    this.location = location;
  }

  public AccessDevice() {
  }

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

  @Override
  public String toString() {
    return "AccessDevice [hostname=" + hostname
      + ", ip=" + ip
      + ", location=" + location
      + ", getHostname()=" + getHostname()
      + ", getIp()=" + getIp()
      + ", getLocation()=" + getLocation()
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
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AccessDevice other = (AccessDevice) obj;
    return Objects.equals(hostname, other.hostname)
        && Objects.equals(ip, other.ip)
        && Objects.equals(location, other.location);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
    result = prime * result + ((ip == null) ? 0 : ip.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    return result;
  }
}
