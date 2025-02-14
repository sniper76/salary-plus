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
 * StockHomeLeaderResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockHomeLeaderResponse {

  private String status;

  private String message = null;

  private Boolean applied = null;

  private Long solidarityId;

  public StockHomeLeaderResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * EJECTED
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public StockHomeLeaderResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public StockHomeLeaderResponse applied(Boolean applied) {
    this.applied = applied;
    return this;
  }

  /**
   * Get applied
   * @return applied
  */
  
  @JsonProperty("applied")
  public Boolean getApplied() {
    return applied;
  }

  public void setApplied(Boolean applied) {
    this.applied = applied;
  }

  public StockHomeLeaderResponse solidarityId(Long solidarityId) {
    this.solidarityId = solidarityId;
    return this;
  }

  /**
   * Get solidarityId
   * @return solidarityId
  */
  
  @JsonProperty("solidarityId")
  public Long getSolidarityId() {
    return solidarityId;
  }

  public void setSolidarityId(Long solidarityId) {
    this.solidarityId = solidarityId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockHomeLeaderResponse stockHomeLeaderResponse = (StockHomeLeaderResponse) o;
    return Objects.equals(this.status, stockHomeLeaderResponse.status) &&
        Objects.equals(this.message, stockHomeLeaderResponse.message) &&
        Objects.equals(this.applied, stockHomeLeaderResponse.applied) &&
        Objects.equals(this.solidarityId, stockHomeLeaderResponse.solidarityId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, applied, solidarityId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockHomeLeaderResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    applied: ").append(toIndentedString(applied)).append("\n");
    sb.append("    solidarityId: ").append(toIndentedString(solidarityId)).append("\n");
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

