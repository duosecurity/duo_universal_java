package com.duosecurity.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Application implements Serializable {
  private static final long serialVersionUID = -5324896038503981781L;

  private String key;
  private String name;
}
