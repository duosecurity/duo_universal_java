package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class Application implements Serializable {
  private static final long serialVersionUID = -5324896038503981781L;

  private String key;
  private String name;
  private String destination_name;

  /**
   * Constructor for the legacy set of properties. Does not set {@code destination_name};
   * use {@link #setDestination_name(String)} for that.
   *
   * @param key key
   * @param name  name
   */
  public Application(String key, String name) {
    this.key = key;
    this.name = name;
  }

  public Application() {
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDestination_name() {
    return destination_name;
  }

  public void setDestination_name(String destinationName) {
    this.destination_name = destinationName;
  }

  @Override
  public String toString() {
    return "Application [key=" + key
        + ", name=" + name
        + ", destination_name=" + destination_name
        + ", getKey()=" + getKey()
        + ", getName()=" + getName()
        + ", getDestination_name()=" + getDestination_name()
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
    Application other = (Application) obj;
    return Objects.equals(key, other.key)
        && Objects.equals(name, other.name)
        && Objects.equals(destination_name, other.destination_name);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((destination_name == null) ? 0 : destination_name.hashCode());
    return result;
  }
}
