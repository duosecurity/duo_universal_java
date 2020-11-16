package com.duosecurity.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuthDevice implements Serializable {
  private static final long serialVersionUID = -1538823399834806194L;

  private String ip;
  private String name;
  private Location location;
}
