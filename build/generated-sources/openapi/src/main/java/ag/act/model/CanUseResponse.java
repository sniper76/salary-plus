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
 * CanUseResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CanUseResponse {

  private Boolean canUse;

  public CanUseResponse canUse(Boolean canUse) {
    this.canUse = canUse;
    return this;
  }

  /**
   * Get canUse
   * @return canUse
  */
  
  @JsonProperty("canUse")
  public Boolean getCanUse() {
    return canUse;
  }

  public void setCanUse(Boolean canUse) {
    this.canUse = canUse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CanUseResponse canUseResponse = (CanUseResponse) o;
    return Objects.equals(this.canUse, canUseResponse.canUse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(canUse);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CanUseResponse {\n");
    sb.append("    canUse: ").append(toIndentedString(canUse)).append("\n");
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

