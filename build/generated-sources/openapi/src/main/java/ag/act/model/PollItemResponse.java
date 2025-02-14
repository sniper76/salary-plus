package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * Poll item details
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PollItemResponse {

  private Long id;

  private String text;

  private Long pollId;

  private String status;

  private Integer voteItemCount;

  private Long voteItemStockSum;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  public PollItemResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Poll item ID
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PollItemResponse text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Poll item text
   * @return text
  */
  
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public PollItemResponse pollId(Long pollId) {
    this.pollId = pollId;
    return this;
  }

  /**
   * Poll ID associated with the poll item
   * @return pollId
  */
  
  @JsonProperty("pollId")
  public Long getPollId() {
    return pollId;
  }

  public void setPollId(Long pollId) {
    this.pollId = pollId;
  }

  public PollItemResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public PollItemResponse voteItemCount(Integer voteItemCount) {
    this.voteItemCount = voteItemCount;
    return this;
  }

  /**
   * Get voteItemCount
   * @return voteItemCount
  */
  
  @JsonProperty("voteItemCount")
  public Integer getVoteItemCount() {
    return voteItemCount;
  }

  public void setVoteItemCount(Integer voteItemCount) {
    this.voteItemCount = voteItemCount;
  }

  public PollItemResponse voteItemStockSum(Long voteItemStockSum) {
    this.voteItemStockSum = voteItemStockSum;
    return this;
  }

  /**
   * Get voteItemStockSum
   * @return voteItemStockSum
  */
  
  @JsonProperty("voteItemStockSum")
  public Long getVoteItemStockSum() {
    return voteItemStockSum;
  }

  public void setVoteItemStockSum(Long voteItemStockSum) {
    this.voteItemStockSum = voteItemStockSum;
  }

  public PollItemResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Creation timestamp of the poll item
   * @return createdAt
  */
  @Valid 
  @JsonProperty("createdAt")
  public java.time.Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
  }

  public PollItemResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Last update timestamp of the poll item
   * @return updatedAt
  */
  @Valid 
  @JsonProperty("updatedAt")
  public java.time.Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public PollItemResponse deletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
    return this;
  }

  /**
   * Get deletedAt
   * @return deletedAt
  */
  @Valid 
  @JsonProperty("deletedAt")
  public java.time.Instant getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PollItemResponse pollItemResponse = (PollItemResponse) o;
    return Objects.equals(this.id, pollItemResponse.id) &&
        Objects.equals(this.text, pollItemResponse.text) &&
        Objects.equals(this.pollId, pollItemResponse.pollId) &&
        Objects.equals(this.status, pollItemResponse.status) &&
        Objects.equals(this.voteItemCount, pollItemResponse.voteItemCount) &&
        Objects.equals(this.voteItemStockSum, pollItemResponse.voteItemStockSum) &&
        Objects.equals(this.createdAt, pollItemResponse.createdAt) &&
        Objects.equals(this.updatedAt, pollItemResponse.updatedAt) &&
        Objects.equals(this.deletedAt, pollItemResponse.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, text, pollId, status, voteItemCount, voteItemStockSum, createdAt, updatedAt, deletedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PollItemResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    pollId: ").append(toIndentedString(pollId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    voteItemCount: ").append(toIndentedString(voteItemCount)).append("\n");
    sb.append("    voteItemStockSum: ").append(toIndentedString(voteItemStockSum)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
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

