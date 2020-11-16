package com.duosecurity.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuthResult implements Serializable {
  private static final long serialVersionUID = -1438823399834806194L;

  private String status_msg;
  private String status;
  private String result;
}
