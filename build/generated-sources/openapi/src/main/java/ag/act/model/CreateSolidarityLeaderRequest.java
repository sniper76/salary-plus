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
 * CreateSolidarityLeaderRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateSolidarityLeaderRequest {

  private Long solidarityApplicantId;

  public CreateSolidarityLeaderRequest solidarityApplicantId(Long solidarityApplicantId) {
    this.solidarityApplicantId = solidarityApplicantId;
    return this;
  }

  /**
   * Get solidarityApplicantId
   * @return solidarityApplicantId
  */
  
  @JsonProperty("solidarityApplicantId")
  public Long getSolidarityApplicantId() {
    return solidarityApplicantId;
  }

  public void setSolidarityApplicantId(Long solidarityApplicantId) {
    this.solidarityApplicantId = solidarityApplicantId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateSolidarityLeaderRequest createSolidarityLeaderRequest = (CreateSolidarityLeaderRequest) o;
    return Objects.equals(this.solidarityApplicantId, createSolidarityLeaderRequest.solidarityApplicantId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityApplicantId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateSolidarityLeaderRequest {\n");
    sb.append("    solidarityApplicantId: ").append(toIndentedString(solidarityApplicantId)).append("\n");
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

