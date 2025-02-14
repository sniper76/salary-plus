package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
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
 * UnreadStockBoardGroupPostStatusDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UnreadStockBoardGroupPostStatusDataResponse {

  private UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus;

  public UnreadStockBoardGroupPostStatusDataResponse unreadStockBoardGroupPostStatus(UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus) {
    this.unreadStockBoardGroupPostStatus = unreadStockBoardGroupPostStatus;
    return this;
  }

  /**
   * Get unreadStockBoardGroupPostStatus
   * @return unreadStockBoardGroupPostStatus
  */
  @Valid 
  @JsonProperty("unreadStockBoardGroupPostStatus")
  public UnreadStockBoardGroupPostStatusResponse getUnreadStockBoardGroupPostStatus() {
    return unreadStockBoardGroupPostStatus;
  }

  public void setUnreadStockBoardGroupPostStatus(UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus) {
    this.unreadStockBoardGroupPostStatus = unreadStockBoardGroupPostStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnreadStockBoardGroupPostStatusDataResponse unreadStockBoardGroupPostStatusDataResponse = (UnreadStockBoardGroupPostStatusDataResponse) o;
    return Objects.equals(this.unreadStockBoardGroupPostStatus, unreadStockBoardGroupPostStatusDataResponse.unreadStockBoardGroupPostStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unreadStockBoardGroupPostStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UnreadStockBoardGroupPostStatusDataResponse {\n");
    sb.append("    unreadStockBoardGroupPostStatus: ").append(toIndentedString(unreadStockBoardGroupPostStatus)).append("\n");
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

