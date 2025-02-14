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
 * DismissSolidarityLeaderRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DismissSolidarityLeaderRequest {

  private Long solidarityLeaderId;

  public DismissSolidarityLeaderRequest solidarityLeaderId(Long solidarityLeaderId) {
    this.solidarityLeaderId = solidarityLeaderId;
    return this;
  }

  /**
   * Get solidarityLeaderId
   * @return solidarityLeaderId
  */
  
  @JsonProperty("solidarityLeaderId")
  public Long getSolidarityLeaderId() {
    return solidarityLeaderId;
  }

  public void setSolidarityLeaderId(Long solidarityLeaderId) {
    this.solidarityLeaderId = solidarityLeaderId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DismissSolidarityLeaderRequest dismissSolidarityLeaderRequest = (DismissSolidarityLeaderRequest) o;
    return Objects.equals(this.solidarityLeaderId, dismissSolidarityLeaderRequest.solidarityLeaderId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityLeaderId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DismissSolidarityLeaderRequest {\n");
    sb.append("    solidarityLeaderId: ").append(toIndentedString(solidarityLeaderId)).append("\n");
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

