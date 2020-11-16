package com.duosecurity.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class AccessDevice implements Serializable {
  private static final long serialVersionUID = -1130960392429229150L;

  private String hostname;
  private String ip;
  private Location location;
}
