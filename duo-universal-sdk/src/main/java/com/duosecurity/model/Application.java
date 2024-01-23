package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class Application implements Serializable {
  @Serial
  private static final long serialVersionUID = -5324896038503981781L;

  private String key;
  private String name;
}
