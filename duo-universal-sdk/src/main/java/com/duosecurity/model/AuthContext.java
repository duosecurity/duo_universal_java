package com.duosecurity.model;

import lombok.Data;

@Data
public class AuthContext {
  private String result;
  private Integer timestamp;
  private AuthDevice auth_device;
  private String txid;
  private String event_type;
  private String reason;
  private AccessDevice access_device;
  private Application application;
  private String factor;
  private User  user;
}
