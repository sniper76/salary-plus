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
 * UpdateStopWordRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateStopWordRequest {

  @NotBlank(message = "상태를 확인해주세요.")

  private String beforeStatus;

  @NotBlank(message = "상태를 확인해주세요.")

  private String afterStatus;

  public UpdateStopWordRequest beforeStatus(String beforeStatus) {
    this.beforeStatus = beforeStatus;
    return this;
  }

  /**
   * ACTIVE or INACTIVE_BY_ADMIN
   * @return beforeStatus
  */
  
  @JsonProperty("beforeStatus")
  public String getBeforeStatus() {
    return beforeStatus;
  }

  public void setBeforeStatus(String beforeStatus) {
    this.beforeStatus = beforeStatus;
  }

  public UpdateStopWordRequest afterStatus(String afterStatus) {
    this.afterStatus = afterStatus;
    return this;
  }

  /**
   * ACTIVE or INACTIVE_BY_ADMIN
   * @return afterStatus
  */
  
  @JsonProperty("afterStatus")
  public String getAfterStatus() {
    return afterStatus;
  }

  public void setAfterStatus(String afterStatus) {
    this.afterStatus = afterStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateStopWordRequest updateStopWordRequest = (UpdateStopWordRequest) o;
    return Objects.equals(this.beforeStatus, updateStopWordRequest.beforeStatus) &&
        Objects.equals(this.afterStatus, updateStopWordRequest.afterStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(beforeStatus, afterStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateStopWordRequest {\n");
    sb.append("    beforeStatus: ").append(toIndentedString(beforeStatus)).append("\n");
    sb.append("    afterStatus: ").append(toIndentedString(afterStatus)).append("\n");
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

