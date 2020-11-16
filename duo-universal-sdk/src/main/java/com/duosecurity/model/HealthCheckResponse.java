package com.duosecurity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.Data;

@Data
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
}
