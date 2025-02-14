package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UserResponse;
import ag.act.model.WebVerificationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * WebVerificationCodeResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class WebVerificationCodeResponse {

  private WebVerificationStatus status;

  private String token;

  private UserResponse user;

  public WebVerificationCodeResponse status(WebVerificationStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @JsonProperty("status")
  public WebVerificationStatus getStatus() {
    return status;
  }

  public void setStatus(WebVerificationStatus status) {
    this.status = status;
  }

  public WebVerificationCodeResponse token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
  */
  
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public WebVerificationCodeResponse user(UserResponse user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
  */
  @Valid 
  @JsonProperty("user")
  public UserResponse getUser() {
    return user;
  }

  public void setUser(UserResponse user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebVerificationCodeResponse webVerificationCodeResponse = (WebVerificationCodeResponse) o;
    return Objects.equals(this.status, webVerificationCodeResponse.status) &&
        Objects.equals(this.token, webVerificationCodeResponse.token) &&
        Objects.equals(this.user, webVerificationCodeResponse.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, token, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebVerificationCodeResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
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

