package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.PushRequest;
import ag.act.model.TargetDateRequest;
import ag.act.model.UpdatePollRequest;
import ag.act.model.UpdatePostRequestDigitalDocument;
import ag.act.model.UpdatePostRequestDigitalProxy;
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
 * UpdatePostRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdatePostRequest implements ag.act.dto.HtmlContent {

  private String boardCategory;

  @NotBlank(message = "제목을 확인해주세요.")

  private String title;

  @NotBlank(message = "내용을 확인해주세요.")

  private String content;

  private Boolean isAnonymous = false;

  private Boolean isActive = true;

  private Boolean isNotification = false;

  private Boolean isExclusiveToHolders = false;

  private TargetDateRequest updateTargetDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeEndDate;

  private PushRequest push;

  @Valid
  private List<@Valid UpdatePollRequest> polls;

  private UpdatePostRequestDigitalProxy digitalProxy;

  private UpdatePostRequestDigitalDocument digitalDocument;

  @Valid
  private List<Long> imageIds;

  private Boolean isEd = false;

  public UpdatePostRequest boardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
    return this;
  }

  /**
   * The category of the post. This field is mandatory.
   * @return boardCategory
  */
  
  @JsonProperty("boardCategory")
  public String getBoardCategory() {
    return boardCategory;
  }

  public void setBoardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
  }

  public UpdatePostRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The title of the post. This field is mandatory.
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UpdatePostRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * The content of the post. This field is mandatory.
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public UpdatePostRequest isAnonymous(Boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
    return this;
  }

  /**
   * Indicates whether the post is written anonymously. By default, this is set to 'false'.
   * @return isAnonymous
  */
  
  @JsonProperty("isAnonymous")
  public Boolean getIsAnonymous() {
    return isAnonymous;
  }

  public void setIsAnonymous(Boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
  }

  public UpdatePostRequest isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Indicates whether the post is active. By default, this is set to 'true'. This field is mandatory.
   * @return isActive
  */
  
  @JsonProperty("isActive")
  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public UpdatePostRequest isNotification(Boolean isNotification) {
    this.isNotification = isNotification;
    return this;
  }

  /**
   * Indicates whether a notification should be sent for the post. By default, this is set to 'false'.
   * @return isNotification
  */
  
  @JsonProperty("isNotification")
  public Boolean getIsNotification() {
    return isNotification;
  }

  public void setIsNotification(Boolean isNotification) {
    this.isNotification = isNotification;
  }

  public UpdatePostRequest isExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
    return this;
  }

  /**
   * Indicates whether the post is exclusive to stock holders. By default, this is set to 'false'. This field is optional.
   * @return isExclusiveToHolders
  */
  
  @JsonProperty("isExclusiveToHolders")
  public Boolean getIsExclusiveToHolders() {
    return isExclusiveToHolders;
  }

  public void setIsExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
  }

  public UpdatePostRequest updateTargetDate(TargetDateRequest updateTargetDate) {
    this.updateTargetDate = updateTargetDate;
    return this;
  }

  /**
   * Get updateTargetDate
   * @return updateTargetDate
  */
  @Valid 
  @JsonProperty("updateTargetDate")
  public TargetDateRequest getUpdateTargetDate() {
    return updateTargetDate;
  }

  public void setUpdateTargetDate(TargetDateRequest updateTargetDate) {
    this.updateTargetDate = updateTargetDate;
  }

  public UpdatePostRequest activeStartDate(java.time.Instant activeStartDate) {
    this.activeStartDate = activeStartDate;
    return this;
  }

  /**
   * Get activeStartDate
   * @return activeStartDate
  */
  @Valid 
  @JsonProperty("activeStartDate")
  public java.time.Instant getActiveStartDate() {
    return activeStartDate;
  }

  public void setActiveStartDate(java.time.Instant activeStartDate) {
    this.activeStartDate = activeStartDate;
  }

  public UpdatePostRequest activeEndDate(java.time.Instant activeEndDate) {
    this.activeEndDate = activeEndDate;
    return this;
  }

  /**
   * Get activeEndDate
   * @return activeEndDate
  */
  @Valid 
  @JsonProperty("activeEndDate")
  public java.time.Instant getActiveEndDate() {
    return activeEndDate;
  }

  public void setActiveEndDate(java.time.Instant activeEndDate) {
    this.activeEndDate = activeEndDate;
  }

  public UpdatePostRequest push(PushRequest push) {
    this.push = push;
    return this;
  }

  /**
   * Get push
   * @return push
  */
  @Valid 
  @JsonProperty("push")
  public PushRequest getPush() {
    return push;
  }

  public void setPush(PushRequest push) {
    this.push = push;
  }

  public UpdatePostRequest polls(List<@Valid UpdatePollRequest> polls) {
    this.polls = polls;
    return this;
  }

  public UpdatePostRequest addPollsItem(UpdatePollRequest pollsItem) {
    if (this.polls == null) {
      this.polls = new ArrayList<>();
    }
    this.polls.add(pollsItem);
    return this;
  }

  /**
   * 멀티 설문 입력 정보
   * @return polls
  */
  @Valid 
  @JsonProperty("polls")
  public List<@Valid UpdatePollRequest> getPolls() {
    return polls;
  }

  public void setPolls(List<@Valid UpdatePollRequest> polls) {
    this.polls = polls;
  }

  public UpdatePostRequest digitalProxy(UpdatePostRequestDigitalProxy digitalProxy) {
    this.digitalProxy = digitalProxy;
    return this;
  }

  /**
   * Get digitalProxy
   * @return digitalProxy
  */
  @Valid 
  @JsonProperty("digitalProxy")
  public UpdatePostRequestDigitalProxy getDigitalProxy() {
    return digitalProxy;
  }

  public void setDigitalProxy(UpdatePostRequestDigitalProxy digitalProxy) {
    this.digitalProxy = digitalProxy;
  }

  public UpdatePostRequest digitalDocument(UpdatePostRequestDigitalDocument digitalDocument) {
    this.digitalDocument = digitalDocument;
    return this;
  }

  /**
   * Get digitalDocument
   * @return digitalDocument
  */
  @Valid 
  @JsonProperty("digitalDocument")
  public UpdatePostRequestDigitalDocument getDigitalDocument() {
    return digitalDocument;
  }

  public void setDigitalDocument(UpdatePostRequestDigitalDocument digitalDocument) {
    this.digitalDocument = digitalDocument;
  }

  public UpdatePostRequest imageIds(List<Long> imageIds) {
    this.imageIds = imageIds;
    return this;
  }

  public UpdatePostRequest addImageIdsItem(Long imageIdsItem) {
    if (this.imageIds == null) {
      this.imageIds = new ArrayList<>();
    }
    this.imageIds.add(imageIdsItem);
    return this;
  }

  /**
   * Get imageIds
   * @return imageIds
  */
  
  @JsonProperty("imageIds")
  public List<Long> getImageIds() {
    return imageIds;
  }

  public void setImageIds(List<Long> imageIds) {
    this.imageIds = imageIds;
  }

  public UpdatePostRequest isEd(Boolean isEd) {
    this.isEd = isEd;
    return this;
  }

  /**
   * Get isEd
   * @return isEd
  */
  
  @JsonProperty("isEd")
  public Boolean getIsEd() {
    return isEd;
  }

  public void setIsEd(Boolean isEd) {
    this.isEd = isEd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdatePostRequest updatePostRequest = (UpdatePostRequest) o;
    return Objects.equals(this.boardCategory, updatePostRequest.boardCategory) &&
        Objects.equals(this.title, updatePostRequest.title) &&
        Objects.equals(this.content, updatePostRequest.content) &&
        Objects.equals(this.isAnonymous, updatePostRequest.isAnonymous) &&
        Objects.equals(this.isActive, updatePostRequest.isActive) &&
        Objects.equals(this.isNotification, updatePostRequest.isNotification) &&
        Objects.equals(this.isExclusiveToHolders, updatePostRequest.isExclusiveToHolders) &&
        Objects.equals(this.updateTargetDate, updatePostRequest.updateTargetDate) &&
        Objects.equals(this.activeStartDate, updatePostRequest.activeStartDate) &&
        Objects.equals(this.activeEndDate, updatePostRequest.activeEndDate) &&
        Objects.equals(this.push, updatePostRequest.push) &&
        Objects.equals(this.polls, updatePostRequest.polls) &&
        Objects.equals(this.digitalProxy, updatePostRequest.digitalProxy) &&
        Objects.equals(this.digitalDocument, updatePostRequest.digitalDocument) &&
        Objects.equals(this.imageIds, updatePostRequest.imageIds) &&
        Objects.equals(this.isEd, updatePostRequest.isEd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(boardCategory, title, content, isAnonymous, isActive, isNotification, isExclusiveToHolders, updateTargetDate, activeStartDate, activeEndDate, push, polls, digitalProxy, digitalDocument, imageIds, isEd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdatePostRequest {\n");
    sb.append("    boardCategory: ").append(toIndentedString(boardCategory)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    isAnonymous: ").append(toIndentedString(isAnonymous)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    isNotification: ").append(toIndentedString(isNotification)).append("\n");
    sb.append("    isExclusiveToHolders: ").append(toIndentedString(isExclusiveToHolders)).append("\n");
    sb.append("    updateTargetDate: ").append(toIndentedString(updateTargetDate)).append("\n");
    sb.append("    activeStartDate: ").append(toIndentedString(activeStartDate)).append("\n");
    sb.append("    activeEndDate: ").append(toIndentedString(activeEndDate)).append("\n");
    sb.append("    push: ").append(toIndentedString(push)).append("\n");
    sb.append("    polls: ").append(toIndentedString(polls)).append("\n");
    sb.append("    digitalProxy: ").append(toIndentedString(digitalProxy)).append("\n");
    sb.append("    digitalDocument: ").append(toIndentedString(digitalDocument)).append("\n");
    sb.append("    imageIds: ").append(toIndentedString(imageIds)).append("\n");
    sb.append("    isEd: ").append(toIndentedString(isEd)).append("\n");
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

