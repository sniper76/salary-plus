package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * 내 인증수단 변경 리퀘스트
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateMyAuthTypeRequest {

  @IsAuthType(message = "인증수단을 확인해주세요.")

  private String authType;

  public UpdateMyAuthTypeRequest authType(String authType) {
    this.authType = authType;
    return this;
  }

  /**
   * auth type must be PIN or BIO
   * @return authType
  */
  
  @JsonProperty("authType")
  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateMyAuthTypeRequest updateMyAuthTypeRequest = (UpdateMyAuthTypeRequest) o;
    return Objects.equals(this.authType, updateMyAuthTypeRequest.authType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateMyAuthTypeRequest {\n");
    sb.append("    authType: ").append(toIndentedString(authType)).append("\n");
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

