package com.duosecurity.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class Response implements Serializable {
  @Serial
  private static final long serialVersionUID = -1372743752722717159L;

  private Integer timestamp;
}
