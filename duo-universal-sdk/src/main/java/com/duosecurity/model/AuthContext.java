package com.duosecurity.model;

import java.io.Serializable;

public class AuthContext implements Serializable {
  private static final long serialVersionUID = -2431823399834806194L;

  private String result;
  private Integer timestamp;
  private AuthDevice auth_device;
  private String txid;
  private String event_type;
  private String reason;
  private AccessDevice access_device;
  private Application application;
  private String factor;
  private User user;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public AuthDevice getAuth_device() {
    return auth_device;
  }

  public void setAuth_device(AuthDevice authDevice) {
    this.auth_device = authDevice;
  }

  public String getTxid() {
    return txid;
  }

  public void setTxid(String txid) {
    this.txid = txid;
  }

  public String getEvent_type() {
    return event_type;
  }

  public void setEvent_type(String eventType) {
    this.event_type = eventType;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public AccessDevice getAccess_device() {
    return access_device;
  }

  public void setAccess_device(AccessDevice accessDevice) {
    this.access_device = accessDevice;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public String getFactor() {
    return factor;
  }

  public void setFactor(String factor) {
    this.factor = factor;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
