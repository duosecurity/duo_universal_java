package com.duosecurity.model;

import java.io.Serializable;

public class User implements Serializable {
  private static final long serialVersionUID = 8222207159539569949L;

  private String key;
  private String name;

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
}
