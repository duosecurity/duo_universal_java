package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class AuthResult implements Serializable {
  private static final long serialVersionUID = -1438823399834806194L;

  private String status_msg;
  private String status;
  private String result;

  /**
   * Constructor with all properties.
   * 
   * @param statusMsg status_msg
   * @param status  status
   * @param result  result
   */
  public AuthResult(String statusMsg, String status, String result) {
    this.status_msg = statusMsg;
    this.status = status;
    this.result = result;
  }

  public AuthResult() {
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getStatus_msg() {
    return status_msg;
  }

  public void setStatus_msg(String statusMsg) {
    this.status_msg = statusMsg;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public String toString() {
    return "AuthResult [status_msg=" + status_msg
        + ", status=" + status
        + ", result=" + result
        + ", getResult()=" + getResult()
        + ", getStatus()=" + getStatus()
        + ", getStatus_msg()=" + getStatus_msg()
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
    AuthResult other = (AuthResult) obj;
    return Objects.equals(status_msg, other.status_msg)
        && Objects.equals(status, other.status)
        && Objects.equals(result, other.result);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((status_msg == null) ? 0 : status_msg.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
    return result;
  }
}
