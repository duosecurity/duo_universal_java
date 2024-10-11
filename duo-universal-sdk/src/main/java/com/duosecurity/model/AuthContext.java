package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

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

  /**
   * Constructor with all properties.
   * 
   * @param result  result
   * @param timestamp timestamp
   * @param authDevice  auth_device
   * @param txid  txid
   * @param eventType event_type
   * @param reason  reason
   * @param accessDevice  access_device
   * @param application application
   * @param factor  factor
   * @param user  user
   */
  public AuthContext(String result, Integer timestamp, AuthDevice authDevice, String txid,
      String eventType, String reason, AccessDevice accessDevice, Application application,
      String factor, User user) {
    this.result = result;
    this.timestamp = timestamp;
    this.auth_device = authDevice;
    this.txid = txid;
    this.event_type = eventType;
    this.reason = reason;
    this.access_device = accessDevice;
    this.application = application;
    this.factor = factor;
    this.user = user;
  }

  public AuthContext() {
  }

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

  @Override
  public String toString() {
    return "AuthContext [result=" + result
        + ", timestamp=" + timestamp
        + ", auth_device=" + auth_device
        + ", txid=" + txid
        + ", event_type=" + event_type
        + ", reason=" + reason
        + ", access_device=" + access_device
        + ", application=" + application
        + ", factor=" + factor
        + ", user=" + user
        + ", getAccess_device()=" + getAccess_device()
        + ", getApplication()=" + getApplication()
        + ", getAuth_device()=" + getAuth_device()
        + ", getEvent_type()=" + getEvent_type()
        + ", getFactor()=" + getFactor()
        + ", getReason()=" + getReason()
        + ", getResult()=" + getResult()
        + ", getTimestamp()=" + getTimestamp()
        + ", getTxid()=" + getTxid()
        + ", getUser()=" + getUser()
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
    AuthContext other = (AuthContext) obj;
    return Objects.equals(result, other.result)
        && Objects.equals(timestamp, other.timestamp)
        && Objects.equals(auth_device, other.auth_device)
        && Objects.equals(txid, other.txid)
        && Objects.equals(event_type, other.event_type)
        && Objects.equals(reason, other.reason)
        && Objects.equals(access_device, other.access_device)
        && Objects.equals(application, other.application)
        && Objects.equals(factor, other.factor)
        && Objects.equals(user, other.user);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((auth_device == null) ? 0 : auth_device.hashCode());
    result = prime * result + ((txid == null) ? 0 : txid.hashCode());
    result = prime * result + ((event_type == null) ? 0 : event_type.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((access_device == null) ? 0 : access_device.hashCode());
    result = prime * result + ((application == null) ? 0 : application.hashCode());
    result = prime * result + ((factor == null) ? 0 : factor.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }
}
