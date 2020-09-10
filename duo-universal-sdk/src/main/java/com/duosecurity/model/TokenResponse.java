package com.duosecurity.model;

import lombok.Data;

@Data
public class TokenResponse {
  private String id_token;
  private String access_token;
  private String token_type;
  private Integer expires_in;
}
