package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * WebVerificationCodeGenerateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class WebVerificationCodeGenerateRequest {

  @NotNull(message = "인증참조키를 확인하세요.")

  private UUID authenticationReference;

  public WebVerificationCodeGenerateRequest authenticationReference(UUID authenticationReference) {
    this.authenticationReference = authenticationReference;
    return this;
  }

  /**
   * Get authenticationReference
   * @return authenticationReference
  */
  @Valid 
  @JsonProperty("authenticationReference")
  public UUID getAuthenticationReference() {
    return authenticationReference;
  }

  public void setAuthenticationReference(UUID authenticationReference) {
    this.authenticationReference = authenticationReference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebVerificationCodeGenerateRequest webVerificationCodeGenerateRequest = (WebVerificationCodeGenerateRequest) o;
    return Objects.equals(this.authenticationReference, webVerificationCodeGenerateRequest.authenticationReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authenticationReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebVerificationCodeGenerateRequest {\n");
    sb.append("    authenticationReference: ").append(toIndentedString(authenticationReference)).append("\n");
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

