package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class Location implements Serializable {

  @Serial
  private static final long serialVersionUID = 8138286056784653212L;

  private String state;
  private String city;
  private String country;
}
