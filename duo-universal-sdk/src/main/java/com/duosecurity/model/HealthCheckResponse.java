package com.duosecurity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.Objects;

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

  /**
   * Constructor with all properties.
   * 
   * @param stat  stat
   * @param response  response
   * @param code  code
   * @param timestamp timestamp
   * @param message message
   * @param messageDetail message_detail
   */
  public HealthCheckResponse(String stat, Response response, String code, String timestamp,
      String message, String messageDetail) {
    this.stat = stat;
    this.response = response;
    this.code = code;
    this.timestamp = timestamp;
    this.message = message;
    this.message_detail = messageDetail;
  }

  public HealthCheckResponse() {
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

  @Override
  public String toString() {
    return "HealthCheckResponse [stat=" + stat
        + ", response=" + response
        + ", code=" + code
        + ", timestamp=" + timestamp
        + ", message=" + message
        + ", message_detail=" + message_detail
        + ", getCode()=" + getCode()
        + ", getMessage()=" + getMessage()
        + ", getMessage_detail()=" + getMessage_detail()
        + ", getResponse()=" + getResponse()
        + ", getStat()=" + getStat()
        + ", getTimestamp()=" + getTimestamp()
        + ", hashCode()=" + hashCode()
        + ", wasSuccess()=" + wasSuccess()
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
    HealthCheckResponse other = (HealthCheckResponse) obj;
    return Objects.equals(stat, other.stat)
        && Objects.equals(response, other.response)
        && Objects.equals(code, other.code)
        && Objects.equals(timestamp, other.timestamp)
        && Objects.equals(message, other.message)
        && Objects.equals(message_detail, other.message_detail);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((stat == null) ? 0 : stat.hashCode());
    result = prime * result + ((response == null) ? 0 : response.hashCode());
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((message_detail == null) ? 0 : message_detail.hashCode());
    return result;
  }
}
