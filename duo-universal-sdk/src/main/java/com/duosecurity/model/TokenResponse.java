package com.duosecurity.model;

import java.io.Serializable;
import java.util.Objects;

public class TokenResponse implements Serializable {
  private static final long serialVersionUID = -3138823399834806194L;

  private String id_token;
  private String access_token;
  private String token_type;
  private Integer expires_in;

  /**
   * Constructor with all properties.
   * 
   * @param idToken id_token
   * @param accessToken access_token
   * @param tokenType token_type
   * @param expiresIn expires_in
   */
  public TokenResponse(String idToken, String accessToken, String tokenType, Integer expiresIn) {
    this.id_token = idToken;
    this.access_token = accessToken;
    this.token_type = tokenType;
    this.expires_in = expiresIn;
  }

  public TokenResponse() {
  }

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

  @Override
  public String toString() {
    return "TokenResponse [id_token=" + id_token
        + ", access_token=" + access_token
        + ", token_type=" + token_type
        + ", expires_in=" + expires_in
        + ", getAccess_token()=" + getAccess_token()
        + ", getExpires_in()=" + getExpires_in()
        + ", getId_token()=" + getId_token()
        + ", getToken_type()=" + getToken_type()
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
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    TokenResponse other = (TokenResponse) obj;
    return Objects.equals(id_token, other.id_token)
        && Objects.equals(access_token, other.access_token)
        && Objects.equals(token_type, other.token_type)
        && Objects.equals(expires_in, other.expires_in);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id_token == null) ? 0 : id_token.hashCode());
    result = prime * result + ((access_token == null) ? 0 : access_token.hashCode());
    result = prime * result + ((token_type == null) ? 0 : token_type.hashCode());
    result = prime * result + ((expires_in == null) ? 0 : expires_in.hashCode());
    return result;
  }
}
