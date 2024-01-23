package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthResult implements Serializable {
  @Serial
  private static final long serialVersionUID = -1438823399834806194L;

  private String status_msg;
  private String status;
  private String result;
}
