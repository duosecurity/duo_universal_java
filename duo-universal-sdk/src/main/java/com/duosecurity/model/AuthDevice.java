package com.duosecurity.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthDevice implements Serializable {
  private static final long serialVersionUID = -1538823399834806194L;

  private String ip;
  private String name;
  private Location location;
}
