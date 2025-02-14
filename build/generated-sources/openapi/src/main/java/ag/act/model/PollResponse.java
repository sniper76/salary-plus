package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.PollAnswerResponse;
import ag.act.model.PollItemResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * PollResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PollResponse {

  private Long id;

  private String title;

  private String content;

  private Long postId;

  private String voteType;

  private String selectionOption;

  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private Integer voteTotalCount;

  private Long voteTotalStockSum;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  @Valid
  private List<@Valid PollItemResponse> pollItems;

  @Valid
  private List<@Valid PollAnswerResponse> answers;

  public PollResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Poll ID
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PollResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Poll title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PollResponse content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Poll content
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public PollResponse postId(Long postId) {
    this.postId = postId;
    return this;
  }

  /**
   * Post ID associated with the poll
   * @return postId
  */
  
  @JsonProperty("postId")
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public PollResponse voteType(String voteType) {
    this.voteType = voteType;
    return this;
  }

  /**
   * Get voteType
   * @return voteType
  */
  
  @JsonProperty("voteType")
  public String getVoteType() {
    return voteType;
  }

  public void setVoteType(String voteType) {
    this.voteType = voteType;
  }

  public PollResponse selectionOption(String selectionOption) {
    this.selectionOption = selectionOption;
    return this;
  }

  /**
   * Get selectionOption
   * @return selectionOption
  */
  
  @JsonProperty("selectionOption")
  public String getSelectionOption() {
    return selectionOption;
  }

  public void setSelectionOption(String selectionOption) {
    this.selectionOption = selectionOption;
  }

  public PollResponse status(String status) {
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

  public PollResponse targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * Target start date of the poll
   * @return targetStartDate
  */
  @Valid 
  @JsonProperty("targetStartDate")
  public java.time.Instant getTargetStartDate() {
    return targetStartDate;
  }

  public void setTargetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
  }

  public PollResponse targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * Target end date of the poll
   * @return targetEndDate
  */
  @Valid 
  @JsonProperty("targetEndDate")
  public java.time.Instant getTargetEndDate() {
    return targetEndDate;
  }

  public void setTargetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
  }

  public PollResponse voteTotalCount(Integer voteTotalCount) {
    this.voteTotalCount = voteTotalCount;
    return this;
  }

  /**
   * Get voteTotalCount
   * @return voteTotalCount
  */
  
  @JsonProperty("voteTotalCount")
  public Integer getVoteTotalCount() {
    return voteTotalCount;
  }

  public void setVoteTotalCount(Integer voteTotalCount) {
    this.voteTotalCount = voteTotalCount;
  }

  public PollResponse voteTotalStockSum(Long voteTotalStockSum) {
    this.voteTotalStockSum = voteTotalStockSum;
    return this;
  }

  /**
   * Get voteTotalStockSum
   * @return voteTotalStockSum
  */
  
  @JsonProperty("voteTotalStockSum")
  public Long getVoteTotalStockSum() {
    return voteTotalStockSum;
  }

  public void setVoteTotalStockSum(Long voteTotalStockSum) {
    this.voteTotalStockSum = voteTotalStockSum;
  }

  public PollResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Creation timestamp of the poll
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

  public PollResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Last update timestamp of the poll
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

  public PollResponse deletedAt(java.time.Instant deletedAt) {
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

  public PollResponse pollItems(List<@Valid PollItemResponse> pollItems) {
    this.pollItems = pollItems;
    return this;
  }

  public PollResponse addPollItemsItem(PollItemResponse pollItemsItem) {
    if (this.pollItems == null) {
      this.pollItems = new ArrayList<>();
    }
    this.pollItems.add(pollItemsItem);
    return this;
  }

  /**
   * Get pollItems
   * @return pollItems
  */
  @Valid 
  @JsonProperty("pollItems")
  public List<@Valid PollItemResponse> getPollItems() {
    return pollItems;
  }

  public void setPollItems(List<@Valid PollItemResponse> pollItems) {
    this.pollItems = pollItems;
  }

  public PollResponse answers(List<@Valid PollAnswerResponse> answers) {
    this.answers = answers;
    return this;
  }

  public PollResponse addAnswersItem(PollAnswerResponse answersItem) {
    if (this.answers == null) {
      this.answers = new ArrayList<>();
    }
    this.answers.add(answersItem);
    return this;
  }

  /**
   * Get answers
   * @return answers
  */
  @Valid 
  @JsonProperty("answers")
  public List<@Valid PollAnswerResponse> getAnswers() {
    return answers;
  }

  public void setAnswers(List<@Valid PollAnswerResponse> answers) {
    this.answers = answers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PollResponse pollResponse = (PollResponse) o;
    return Objects.equals(this.id, pollResponse.id) &&
        Objects.equals(this.title, pollResponse.title) &&
        Objects.equals(this.content, pollResponse.content) &&
        Objects.equals(this.postId, pollResponse.postId) &&
        Objects.equals(this.voteType, pollResponse.voteType) &&
        Objects.equals(this.selectionOption, pollResponse.selectionOption) &&
        Objects.equals(this.status, pollResponse.status) &&
        Objects.equals(this.targetStartDate, pollResponse.targetStartDate) &&
        Objects.equals(this.targetEndDate, pollResponse.targetEndDate) &&
        Objects.equals(this.voteTotalCount, pollResponse.voteTotalCount) &&
        Objects.equals(this.voteTotalStockSum, pollResponse.voteTotalStockSum) &&
        Objects.equals(this.createdAt, pollResponse.createdAt) &&
        Objects.equals(this.updatedAt, pollResponse.updatedAt) &&
        Objects.equals(this.deletedAt, pollResponse.deletedAt) &&
        Objects.equals(this.pollItems, pollResponse.pollItems) &&
        Objects.equals(this.answers, pollResponse.answers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, postId, voteType, selectionOption, status, targetStartDate, targetEndDate, voteTotalCount, voteTotalStockSum, createdAt, updatedAt, deletedAt, pollItems, answers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PollResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    voteType: ").append(toIndentedString(voteType)).append("\n");
    sb.append("    selectionOption: ").append(toIndentedString(selectionOption)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    voteTotalCount: ").append(toIndentedString(voteTotalCount)).append("\n");
    sb.append("    voteTotalStockSum: ").append(toIndentedString(voteTotalStockSum)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
    sb.append("    pollItems: ").append(toIndentedString(pollItems)).append("\n");
    sb.append("    answers: ").append(toIndentedString(answers)).append("\n");
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

