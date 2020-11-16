package com.duosecurity.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthResult implements Serializable {
  private static final long serialVersionUID = -1438823399834806194L;

  private String status_msg;
  private String status;
  private String result;
}
