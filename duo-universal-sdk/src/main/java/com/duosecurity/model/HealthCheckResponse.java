package com.duosecurity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class HealthCheckResponse {

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
