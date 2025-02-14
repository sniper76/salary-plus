package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.AuthUserResponseToken;
import ag.act.model.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * AuthUserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class AuthUserResponse {

  private AuthUserResponseToken token;

  private UserResponse user;

  public AuthUserResponse token(AuthUserResponseToken token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
  */
  @Valid 
  @JsonProperty("token")
  public AuthUserResponseToken getToken() {
    return token;
  }

  public void setToken(AuthUserResponseToken token) {
    this.token = token;
  }

  public AuthUserResponse user(UserResponse user) {
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
    AuthUserResponse authUserResponse = (AuthUserResponse) o;
    return Objects.equals(this.token, authUserResponse.token) &&
        Objects.equals(this.user, authUserResponse.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthUserResponse {\n");
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

