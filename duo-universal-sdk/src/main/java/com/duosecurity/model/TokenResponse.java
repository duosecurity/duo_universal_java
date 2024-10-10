package com.duosecurity.model;

import java.io.Serializable;

public class TokenResponse implements Serializable {
  private static final long serialVersionUID = -3138823399834806194L;

  private String id_token;
  private String access_token;
  private String token_type;
  private Integer expires_in;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getId_token() {
    return id_token;
  }

  public void setId_token(String idToken) {
    this.id_token = idToken;
  }

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String accessToken) {
    this.access_token = accessToken;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String tokenType) {
    this.token_type = tokenType;
  }

  public Integer getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(Integer expiresIn) {
    this.expires_in = expiresIn;
  }
}
