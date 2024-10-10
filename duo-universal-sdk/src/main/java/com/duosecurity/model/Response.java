package com.duosecurity.model;

import java.io.Serializable;

public class Response implements Serializable {
  private static final long serialVersionUID = -1372743752722717159L;

  private Integer timestamp;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }
}
