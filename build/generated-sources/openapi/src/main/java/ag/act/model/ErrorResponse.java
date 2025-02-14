package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * JSON payload with error message
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ErrorResponse {

  private Integer statusCode;

  private Integer errorCode;

  private String message;

  @Valid
  private Map<String, Object> errorData = new HashMap<>();

  public ErrorResponse statusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * HTTP status code
   * @return statusCode
  */
  
  @JsonProperty("statusCode")
  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public ErrorResponse errorCode(Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * Specific error code
   * @return errorCode
  */
  
  @JsonProperty("errorCode")
  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public ErrorResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Human readable error message
   * @return message
  */
  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ErrorResponse errorData(Map<String, Object> errorData) {
    this.errorData = errorData;
    return this;
  }

  public ErrorResponse putErrorDataItem(String key, Object errorDataItem) {
    if (this.errorData == null) {
      this.errorData = new HashMap<>();
    }
    this.errorData.put(key, errorDataItem);
    return this;
  }

  /**
   * Additional error data
   * @return errorData
  */
  
  @JsonProperty("errorData")
  public Map<String, Object> getErrorData() {
    return errorData;
  }

  public void setErrorData(Map<String, Object> errorData) {
    this.errorData = errorData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.statusCode, errorResponse.statusCode) &&
        Objects.equals(this.errorCode, errorResponse.errorCode) &&
        Objects.equals(this.message, errorResponse.message) &&
        Objects.equals(this.errorData, errorResponse.errorData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusCode, errorCode, message, errorData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    errorData: ").append(toIndentedString(errorData)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

