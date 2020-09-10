package com.duosecurity.model;

import lombok.Data;

@Data
public class Token {
  private String iss;
  private String sub;
  private String preferred_username;
  private String aud;
  private Integer exp;
  private Double iat;
  private Integer auth_time;
  private AuthResult auth_result;
  private AuthContext auth_context;
}
