package com.duosecurity.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class TokenResponse implements Serializable {
  private static final long serialVersionUID = -3138823399834806194L;

  private String id_token;
  private String access_token;
  private String token_type;
  private Integer expires_in;
}
