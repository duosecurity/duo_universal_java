package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class Response implements Serializable {
  private static final long serialVersionUID = -1372743752722717159L;

  private Integer timestamp;

  public Response(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public Response() {
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "Response [timestamp=" + timestamp
        + ", getTimestamp()=" + getTimestamp()
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
    Response other = (Response) obj;
    return Objects.equals(timestamp, other.timestamp);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    return result;
  }
}
