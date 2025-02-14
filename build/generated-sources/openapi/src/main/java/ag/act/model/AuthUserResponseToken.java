package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * AuthUserResponseToken
 */

@JsonTypeName("AuthUserResponse_token")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class AuthUserResponseToken {

  private String accessToken;

  private String finpongAccessToken = null;

  public AuthUserResponseToken accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * Get accessToken
   * @return accessToken
  */
  
  @JsonProperty("accessToken")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public AuthUserResponseToken finpongAccessToken(String finpongAccessToken) {
    this.finpongAccessToken = finpongAccessToken;
    return this;
  }

  /**
   * Get finpongAccessToken
   * @return finpongAccessToken
  */
  
  @JsonProperty("finpongAccessToken")
  public String getFinpongAccessToken() {
    return finpongAccessToken;
  }

  public void setFinpongAccessToken(String finpongAccessToken) {
    this.finpongAccessToken = finpongAccessToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthUserResponseToken authUserResponseToken = (AuthUserResponseToken) o;
    return Objects.equals(this.accessToken, authUserResponseToken.accessToken) &&
        Objects.equals(this.finpongAccessToken, authUserResponseToken.finpongAccessToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, finpongAccessToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthUserResponseToken {\n");
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
    sb.append("    finpongAccessToken: ").append(toIndentedString(finpongAccessToken)).append("\n");
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

