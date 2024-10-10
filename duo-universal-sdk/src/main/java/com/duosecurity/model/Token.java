package com.duosecurity.model;

import java.io.Serializable;

public class Token implements Serializable {
  private static final long serialVersionUID = -8768823399834806194L;

  private String iss;
  private String sub;
  private String preferred_username;
  private String aud;
  private Integer exp;
  private Double iat;
  private Integer auth_time;
  private AuthResult auth_result;
  private AuthContext auth_context;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getIss() {
    return iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getPreferred_username() {
    return preferred_username;
  }

  public void setPreferred_username(String preferredUsername) {
    this.preferred_username = preferredUsername;
  }

  public String getAud() {
    return aud;
  }

  public void setAud(String aud) {
    this.aud = aud;
  }

  public Integer getExp() {
    return exp;
  }

  public void setExp(Integer exp) {
    this.exp = exp;
  }

  public Double getIat() {
    return iat;
  }

  public void setIat(Double iat) {
    this.iat = iat;
  }

  public Integer getAuth_time() {
    return auth_time;
  }

  public void setAuth_time(Integer authTime) {
    this.auth_time = authTime;
  }

  public AuthResult getAuth_result() {
    return auth_result;
  }

  public void setAuth_result(AuthResult authResult) {
    this.auth_result = authResult;
  }

  public AuthContext getAuth_context() {
    return auth_context;
  }

  public void setAuth_context(AuthContext authContext) {
    this.auth_context = authContext;
  }
}
