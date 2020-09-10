package com.duosecurity.model;

import lombok.Data;

@Data
public class AuthDevice {
  private String ip;
  private String name;
  private Location location;
}
