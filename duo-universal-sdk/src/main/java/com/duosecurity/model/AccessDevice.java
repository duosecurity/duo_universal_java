package com.duosecurity.model;

import lombok.Data;

@Data
public class AccessDevice {
  private String hostname;
  private String ip;
  private Location location;
}
