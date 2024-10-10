package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
  private static final long serialVersionUID = 8222207159539569949L;

  private String key;
  private String name;

  public User(String key, String name) {
    this.key = key;
    this.name = name;
  }

  public User() {
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

  @Override
  public String toString() {
    return "User [key=" + key
        + ", name=" + name
        + ", getKey()=" + getKey()
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
    User other = (User) obj;
    return Objects.equals(key, other.key)
        && Objects.equals(name, other.name);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }
}
