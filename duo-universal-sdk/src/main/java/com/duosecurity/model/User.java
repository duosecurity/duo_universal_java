package com.duosecurity.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {
  private static final long serialVersionUID = 8222207159539569949L;

  private String key;
  private String name;
}
