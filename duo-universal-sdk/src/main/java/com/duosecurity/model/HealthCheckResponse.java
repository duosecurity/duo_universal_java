package com.duosecurity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;

public class HealthCheckResponse implements Serializable {

  private static final long serialVersionUID = 8515018216883786859L;

  private String stat;

  @JsonInclude(Include.NON_NULL)
  private Response response;

  @JsonInclude(Include.NON_NULL)
  private String code;

  @JsonInclude(Include.NON_NULL)
  private String timestamp;

  @JsonInclude(Include.NON_NULL)
  private String message;

  @JsonInclude(Include.NON_NULL)
  private String message_detail;

  public Boolean wasSuccess() {
    return stat.equalsIgnoreCase("OK");
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getStat() {
    return stat;
  }

  public void setStat(String stat) {
    this.stat = stat;
  }

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage_detail() {
    return message_detail;
  }

  public void setMessage_detail(String messageDetail) {
    this.message_detail = messageDetail;
  }
}
