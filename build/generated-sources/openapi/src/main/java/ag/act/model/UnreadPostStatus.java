package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.StockResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UnreadPostStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UnreadPostStatus {

  private Boolean unreadGlobalBoard = false;

  private Boolean unreadGlobalCommunity = false;

  private Boolean unreadGlobalEvent = false;

  private Boolean unreadDigitalDelegation = false;

  @Valid
  private List<@Valid StockResponse> unreadStocks;

  public UnreadPostStatus unreadGlobalBoard(Boolean unreadGlobalBoard) {
    this.unreadGlobalBoard = unreadGlobalBoard;
    return this;
  }

  /**
   * Get unreadGlobalBoard
   * @return unreadGlobalBoard
  */
  
  @JsonProperty("unreadGlobalBoard")
  public Boolean getUnreadGlobalBoard() {
    return unreadGlobalBoard;
  }

  public void setUnreadGlobalBoard(Boolean unreadGlobalBoard) {
    this.unreadGlobalBoard = unreadGlobalBoard;
  }

  public UnreadPostStatus unreadGlobalCommunity(Boolean unreadGlobalCommunity) {
    this.unreadGlobalCommunity = unreadGlobalCommunity;
    return this;
  }

  /**
   * Get unreadGlobalCommunity
   * @return unreadGlobalCommunity
  */
  
  @JsonProperty("unreadGlobalCommunity")
  public Boolean getUnreadGlobalCommunity() {
    return unreadGlobalCommunity;
  }

  public void setUnreadGlobalCommunity(Boolean unreadGlobalCommunity) {
    this.unreadGlobalCommunity = unreadGlobalCommunity;
  }

  public UnreadPostStatus unreadGlobalEvent(Boolean unreadGlobalEvent) {
    this.unreadGlobalEvent = unreadGlobalEvent;
    return this;
  }

  /**
   * Get unreadGlobalEvent
   * @return unreadGlobalEvent
  */
  
  @JsonProperty("unreadGlobalEvent")
  public Boolean getUnreadGlobalEvent() {
    return unreadGlobalEvent;
  }

  public void setUnreadGlobalEvent(Boolean unreadGlobalEvent) {
    this.unreadGlobalEvent = unreadGlobalEvent;
  }

  public UnreadPostStatus unreadDigitalDelegation(Boolean unreadDigitalDelegation) {
    this.unreadDigitalDelegation = unreadDigitalDelegation;
    return this;
  }

  /**
   * Get unreadDigitalDelegation
   * @return unreadDigitalDelegation
  */
  
  @JsonProperty("unreadDigitalDelegation")
  public Boolean getUnreadDigitalDelegation() {
    return unreadDigitalDelegation;
  }

  public void setUnreadDigitalDelegation(Boolean unreadDigitalDelegation) {
    this.unreadDigitalDelegation = unreadDigitalDelegation;
  }

  public UnreadPostStatus unreadStocks(List<@Valid StockResponse> unreadStocks) {
    this.unreadStocks = unreadStocks;
    return this;
  }

  public UnreadPostStatus addUnreadStocksItem(StockResponse unreadStocksItem) {
    if (this.unreadStocks == null) {
      this.unreadStocks = new ArrayList<>();
    }
    this.unreadStocks.add(unreadStocksItem);
    return this;
  }

  /**
   * Get unreadStocks
   * @return unreadStocks
  */
  @Valid 
  @JsonProperty("unreadStocks")
  public List<@Valid StockResponse> getUnreadStocks() {
    return unreadStocks;
  }

  public void setUnreadStocks(List<@Valid StockResponse> unreadStocks) {
    this.unreadStocks = unreadStocks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnreadPostStatus unreadPostStatus = (UnreadPostStatus) o;
    return Objects.equals(this.unreadGlobalBoard, unreadPostStatus.unreadGlobalBoard) &&
        Objects.equals(this.unreadGlobalCommunity, unreadPostStatus.unreadGlobalCommunity) &&
        Objects.equals(this.unreadGlobalEvent, unreadPostStatus.unreadGlobalEvent) &&
        Objects.equals(this.unreadDigitalDelegation, unreadPostStatus.unreadDigitalDelegation) &&
        Objects.equals(this.unreadStocks, unreadPostStatus.unreadStocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unreadGlobalBoard, unreadGlobalCommunity, unreadGlobalEvent, unreadDigitalDelegation, unreadStocks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UnreadPostStatus {\n");
    sb.append("    unreadGlobalBoard: ").append(toIndentedString(unreadGlobalBoard)).append("\n");
    sb.append("    unreadGlobalCommunity: ").append(toIndentedString(unreadGlobalCommunity)).append("\n");
    sb.append("    unreadGlobalEvent: ").append(toIndentedString(unreadGlobalEvent)).append("\n");
    sb.append("    unreadDigitalDelegation: ").append(toIndentedString(unreadDigitalDelegation)).append("\n");
    sb.append("    unreadStocks: ").append(toIndentedString(unreadStocks)).append("\n");
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

