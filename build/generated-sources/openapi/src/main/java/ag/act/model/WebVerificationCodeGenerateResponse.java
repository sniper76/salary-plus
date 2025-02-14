package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * WebVerificationCodeGenerateResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class WebVerificationCodeGenerateResponse {

  private String verificationCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant expirationDateTime;

  public WebVerificationCodeGenerateResponse verificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
    return this;
  }

  /**
   * Get verificationCode
   * @return verificationCode
  */
  
  @JsonProperty("verificationCode")
  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public WebVerificationCodeGenerateResponse expirationDateTime(java.time.Instant expirationDateTime) {
    this.expirationDateTime = expirationDateTime;
    return this;
  }

  /**
   * Get expirationDateTime
   * @return expirationDateTime
  */
  @Valid 
  @JsonProperty("expirationDateTime")
  public java.time.Instant getExpirationDateTime() {
    return expirationDateTime;
  }

  public void setExpirationDateTime(java.time.Instant expirationDateTime) {
    this.expirationDateTime = expirationDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebVerificationCodeGenerateResponse webVerificationCodeGenerateResponse = (WebVerificationCodeGenerateResponse) o;
    return Objects.equals(this.verificationCode, webVerificationCodeGenerateResponse.verificationCode) &&
        Objects.equals(this.expirationDateTime, webVerificationCodeGenerateResponse.expirationDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(verificationCode, expirationDateTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebVerificationCodeGenerateResponse {\n");
    sb.append("    verificationCode: ").append(toIndentedString(verificationCode)).append("\n");
    sb.append("    expirationDateTime: ").append(toIndentedString(expirationDateTime)).append("\n");
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

