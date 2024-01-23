package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthContext implements Serializable {
  @Serial
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
  private User  user;
}
