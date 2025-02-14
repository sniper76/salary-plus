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
 * 유저 차단 리퀘스트
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateBlockUserRequest {

  @NotNull(message = "차단할 유저 아이디를 확인해주세요.")

  private Long targetUserId;

  public CreateBlockUserRequest targetUserId(Long targetUserId) {
    this.targetUserId = targetUserId;
    return this;
  }

  /**
   * 차단할 유저 아이디
   * @return targetUserId
  */
  
  @JsonProperty("targetUserId")
  public Long getTargetUserId() {
    return targetUserId;
  }

  public void setTargetUserId(Long targetUserId) {
    this.targetUserId = targetUserId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBlockUserRequest createBlockUserRequest = (CreateBlockUserRequest) o;
    return Objects.equals(this.targetUserId, createBlockUserRequest.targetUserId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetUserId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateBlockUserRequest {\n");
    sb.append("    targetUserId: ").append(toIndentedString(targetUserId)).append("\n");
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

