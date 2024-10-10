package com.duosecurity.model;

import java.io.Serializable;

public class Application implements Serializable {
  private static final long serialVersionUID = -5324896038503981781L;

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
