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
 * UnreadStockBoardGroupPostStatusResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UnreadStockBoardGroupPostStatusResponse {

  private Boolean unreadAnalysis = false;

  private Boolean unreadAction = false;

  private Boolean unreadDebate = false;

  public UnreadStockBoardGroupPostStatusResponse unreadAnalysis(Boolean unreadAnalysis) {
    this.unreadAnalysis = unreadAnalysis;
    return this;
  }

  /**
   * Get unreadAnalysis
   * @return unreadAnalysis
  */
  
  @JsonProperty("unreadAnalysis")
  public Boolean getUnreadAnalysis() {
    return unreadAnalysis;
  }

  public void setUnreadAnalysis(Boolean unreadAnalysis) {
    this.unreadAnalysis = unreadAnalysis;
  }

  public UnreadStockBoardGroupPostStatusResponse unreadAction(Boolean unreadAction) {
    this.unreadAction = unreadAction;
    return this;
  }

  /**
   * Get unreadAction
   * @return unreadAction
  */
  
  @JsonProperty("unreadAction")
  public Boolean getUnreadAction() {
    return unreadAction;
  }

  public void setUnreadAction(Boolean unreadAction) {
    this.unreadAction = unreadAction;
  }

  public UnreadStockBoardGroupPostStatusResponse unreadDebate(Boolean unreadDebate) {
    this.unreadDebate = unreadDebate;
    return this;
  }

  /**
   * Get unreadDebate
   * @return unreadDebate
  */
  
  @JsonProperty("unreadDebate")
  public Boolean getUnreadDebate() {
    return unreadDebate;
  }

  public void setUnreadDebate(Boolean unreadDebate) {
    this.unreadDebate = unreadDebate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatusResponse = (UnreadStockBoardGroupPostStatusResponse) o;
    return Objects.equals(this.unreadAnalysis, unreadStockBoardGroupPostStatusResponse.unreadAnalysis) &&
        Objects.equals(this.unreadAction, unreadStockBoardGroupPostStatusResponse.unreadAction) &&
        Objects.equals(this.unreadDebate, unreadStockBoardGroupPostStatusResponse.unreadDebate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unreadAnalysis, unreadAction, unreadDebate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UnreadStockBoardGroupPostStatusResponse {\n");
    sb.append("    unreadAnalysis: ").append(toIndentedString(unreadAnalysis)).append("\n");
    sb.append("    unreadAction: ").append(toIndentedString(unreadAction)).append("\n");
    sb.append("    unreadDebate: ").append(toIndentedString(unreadDebate)).append("\n");
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

