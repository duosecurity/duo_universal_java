package com.duosecurity.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class AccessDevice implements Serializable {
  private static final long serialVersionUID = -1130960392429229150L;

  private String hostname;
  private String ip;
  private Location location;
}
