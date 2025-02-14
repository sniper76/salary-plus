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
 * SolidarityLeaderElectionVoteItemDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionVoteItemDataResponse {

  private Long pollItemId;

  private Boolean isVoted;

  private String title;

  private String type;

  private Integer voteCount;

  private Long stockQuantity;

  private Long stockQuantityPercentage;

  public SolidarityLeaderElectionVoteItemDataResponse pollItemId(Long pollItemId) {
    this.pollItemId = pollItemId;
    return this;
  }

  /**
   * Get pollItemId
   * @return pollItemId
  */
  
  @JsonProperty("pollItemId")
  public Long getPollItemId() {
    return pollItemId;
  }

  public void setPollItemId(Long pollItemId) {
    this.pollItemId = pollItemId;
  }

  public SolidarityLeaderElectionVoteItemDataResponse isVoted(Boolean isVoted) {
    this.isVoted = isVoted;
    return this;
  }

  /**
   * Get isVoted
   * @return isVoted
  */
  
  @JsonProperty("isVoted")
  public Boolean getIsVoted() {
    return isVoted;
  }

  public void setIsVoted(Boolean isVoted) {
    this.isVoted = isVoted;
  }

  public SolidarityLeaderElectionVoteItemDataResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 찬성/반대
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public SolidarityLeaderElectionVoteItemDataResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * APPROVAL/REJECTION
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public SolidarityLeaderElectionVoteItemDataResponse voteCount(Integer voteCount) {
    this.voteCount = voteCount;
    return this;
  }

  /**
   * Get voteCount
   * @return voteCount
  */
  
  @JsonProperty("voteCount")
  public Integer getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(Integer voteCount) {
    this.voteCount = voteCount;
  }

  public SolidarityLeaderElectionVoteItemDataResponse stockQuantity(Long stockQuantity) {
    this.stockQuantity = stockQuantity;
    return this;
  }

  /**
   * Get stockQuantity
   * @return stockQuantity
  */
  
  @JsonProperty("stockQuantity")
  public Long getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(Long stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public SolidarityLeaderElectionVoteItemDataResponse stockQuantityPercentage(Long stockQuantityPercentage) {
    this.stockQuantityPercentage = stockQuantityPercentage;
    return this;
  }

  /**
   * Get stockQuantityPercentage
   * @return stockQuantityPercentage
  */
  
  @JsonProperty("stockQuantityPercentage")
  public Long getStockQuantityPercentage() {
    return stockQuantityPercentage;
  }

  public void setStockQuantityPercentage(Long stockQuantityPercentage) {
    this.stockQuantityPercentage = stockQuantityPercentage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionVoteItemDataResponse solidarityLeaderElectionVoteItemDataResponse = (SolidarityLeaderElectionVoteItemDataResponse) o;
    return Objects.equals(this.pollItemId, solidarityLeaderElectionVoteItemDataResponse.pollItemId) &&
        Objects.equals(this.isVoted, solidarityLeaderElectionVoteItemDataResponse.isVoted) &&
        Objects.equals(this.title, solidarityLeaderElectionVoteItemDataResponse.title) &&
        Objects.equals(this.type, solidarityLeaderElectionVoteItemDataResponse.type) &&
        Objects.equals(this.voteCount, solidarityLeaderElectionVoteItemDataResponse.voteCount) &&
        Objects.equals(this.stockQuantity, solidarityLeaderElectionVoteItemDataResponse.stockQuantity) &&
        Objects.equals(this.stockQuantityPercentage, solidarityLeaderElectionVoteItemDataResponse.stockQuantityPercentage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pollItemId, isVoted, title, type, voteCount, stockQuantity, stockQuantityPercentage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionVoteItemDataResponse {\n");
    sb.append("    pollItemId: ").append(toIndentedString(pollItemId)).append("\n");
    sb.append("    isVoted: ").append(toIndentedString(isVoted)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    voteCount: ").append(toIndentedString(voteCount)).append("\n");
    sb.append("    stockQuantity: ").append(toIndentedString(stockQuantity)).append("\n");
    sb.append("    stockQuantityPercentage: ").append(toIndentedString(stockQuantityPercentage)).append("\n");
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

