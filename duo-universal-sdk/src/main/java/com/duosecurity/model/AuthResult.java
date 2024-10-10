package com.duosecurity.model;

import java.io.Serializable;

public class AuthResult implements Serializable {
  private static final long serialVersionUID = -1438823399834806194L;

  private String status_msg;
  private String status;
  private String result;

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
}
