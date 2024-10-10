package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

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

  /**
   * Constructor with all properties.
   * 
   * @param iss iss
   * @param sub sub
   * @param preferredUsername preferred_username
   * @param aud aud
   * @param exp exp
   * @param iat iat
   * @param authTime  auth_time
   * @param authResult  auth_result
   * @param authContext auth_context
   */
  public Token(String iss, String sub, String preferredUsername, String aud, Integer exp,
      Double iat, Integer authTime, AuthResult authResult, AuthContext authContext) {
    this.iss = iss;
    this.sub = sub;
    this.preferred_username = preferredUsername;
    this.aud = aud;
    this.exp = exp;
    this.iat = iat;
    this.auth_time = authTime;
    this.auth_result = authResult;
    this.auth_context = authContext;
  }

  public Token() {
  }

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

  @Override
  public String toString() {
    return "Token [iss=" + iss
          + ", sub=" + sub
          + ", preferred_username=" + preferred_username
          + ", aud=" + aud
          + ", exp=" + exp
          + ", iat=" + iat
          + ", auth_time=" + auth_time
          + ", auth_result=" + auth_result
          + ", auth_context=" + auth_context
          + ", getAud()=" + getAud()
          + ", getAuth_context()=" + getAuth_context()
          + ", getAuth_result()=" + getAuth_result()
          + ", getAuth_time()=" + getAuth_time()
          + ", getExp()=" + getExp()
          + ", getIat()=" + getIat()
          + ", getIss()=" + getIss()
          + ", getPreferred_username()=" + getPreferred_username()
          + ", getSub()=" + getSub()
          + ", hashCode()=" + hashCode()
          + ", getClass()=" + getClass()
          + ", toString()=" + super.toString()
          + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Token other = (Token) obj;
    return Objects.equals(iss, other.iss)
        && Objects.equals(sub, other.sub)
        && Objects.equals(preferred_username, other.preferred_username)
        && Objects.equals(aud, other.aud)
        && Objects.equals(exp, other.exp)
        && Objects.equals(iat, other.iat)
        && Objects.equals(auth_time, other.auth_time)
        && Objects.equals(auth_result, other.auth_result)
        && Objects.equals(auth_context, other.auth_context);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((iss == null) ? 0 : iss.hashCode());
    result = prime * result + ((sub == null) ? 0 : sub.hashCode());
    result = prime * result + ((preferred_username == null) ? 0 : preferred_username.hashCode());
    result = prime * result + ((aud == null) ? 0 : aud.hashCode());
    result = prime * result + ((exp == null) ? 0 : exp.hashCode());
    result = prime * result + ((iat == null) ? 0 : iat.hashCode());
    result = prime * result + ((auth_time == null) ? 0 : auth_time.hashCode());
    result = prime * result + ((auth_result == null) ? 0 : auth_result.hashCode());
    result = prime * result + ((auth_context == null) ? 0 : auth_context.hashCode());
    return result;
  }
}
