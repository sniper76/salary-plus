package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CreatePollItemRequest;
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
 * Poll for the post. It can be null.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreatePollRequest {

  private String title;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private String selectionOption;

  private String voteType;

  @Valid
  private List<@Valid CreatePollItemRequest> pollItems;

  public CreatePollRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the poll is mandatory
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CreatePollRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Content of the poll is mandatory
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public CreatePollRequest targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * The start date for the poll is mandatory
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

  public CreatePollRequest targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * The end date for the poll is mandatory
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

  public CreatePollRequest selectionOption(String selectionOption) {
    this.selectionOption = selectionOption;
    return this;
  }

  /**
   * Selection options for the poll ex) SINGLE_ITEM, MULTIPLE_ITEMS is mandatory
   * @return selectionOption
  */
  
  @JsonProperty("selectionOption")
  public String getSelectionOption() {
    return selectionOption;
  }

  public void setSelectionOption(String selectionOption) {
    this.selectionOption = selectionOption;
  }

  public CreatePollRequest voteType(String voteType) {
    this.voteType = voteType;
    return this;
  }

  /**
   * Vote type for the poll ex) PERSON_BASED, SHAREHOLDER_BASED is mandatory
   * @return voteType
  */
  
  @JsonProperty("voteType")
  public String getVoteType() {
    return voteType;
  }

  public void setVoteType(String voteType) {
    this.voteType = voteType;
  }

  public CreatePollRequest pollItems(List<@Valid CreatePollItemRequest> pollItems) {
    this.pollItems = pollItems;
    return this;
  }

  public CreatePollRequest addPollItemsItem(CreatePollItemRequest pollItemsItem) {
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
  public List<@Valid CreatePollItemRequest> getPollItems() {
    return pollItems;
  }

  public void setPollItems(List<@Valid CreatePollItemRequest> pollItems) {
    this.pollItems = pollItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePollRequest createPollRequest = (CreatePollRequest) o;
    return Objects.equals(this.title, createPollRequest.title) &&
        Objects.equals(this.content, createPollRequest.content) &&
        Objects.equals(this.targetStartDate, createPollRequest.targetStartDate) &&
        Objects.equals(this.targetEndDate, createPollRequest.targetEndDate) &&
        Objects.equals(this.selectionOption, createPollRequest.selectionOption) &&
        Objects.equals(this.voteType, createPollRequest.voteType) &&
        Objects.equals(this.pollItems, createPollRequest.pollItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, content, targetStartDate, targetEndDate, selectionOption, voteType, pollItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePollRequest {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    selectionOption: ").append(toIndentedString(selectionOption)).append("\n");
    sb.append("    voteType: ").append(toIndentedString(voteType)).append("\n");
    sb.append("    pollItems: ").append(toIndentedString(pollItems)).append("\n");
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

