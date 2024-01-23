package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class AuthDevice implements Serializable {
  @Serial
  private static final long serialVersionUID = -1538823399834806194L;

  private String ip;
  private String name;
  private Location location;
}
