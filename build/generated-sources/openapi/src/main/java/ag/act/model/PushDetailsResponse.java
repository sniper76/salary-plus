package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UserResponse;
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
 * PushDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PushDetailsResponse {

  private Long id;

  private UserResponse user;

  private String title;

  private String content;

  private String linkType;

  private String stockTargetType;

  private String linkUrl;

  private String stockCode = null;

  private String stockName = null;

  private Long stockGroupId;

  private String stockGroupName = null;

  private Long postId;

  private String sendType;

  private String sendStatus;

  private String topic = null;

  private String result;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetDatetime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant sentStartDatetime = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant sentEndDatetime = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public PushDetailsResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PushDetailsResponse user(UserResponse user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
  */
  @Valid 
  @JsonProperty("user")
  public UserResponse getUser() {
    return user;
  }

  public void setUser(UserResponse user) {
    this.user = user;
  }

  public PushDetailsResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PushDetailsResponse content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public PushDetailsResponse linkType(String linkType) {
    this.linkType = linkType;
    return this;
  }

  /**
   * Get linkType
   * @return linkType
  */
  
  @JsonProperty("linkType")
  public String getLinkType() {
    return linkType;
  }

  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  public PushDetailsResponse stockTargetType(String stockTargetType) {
    this.stockTargetType = stockTargetType;
    return this;
  }

  /**
   * Get stockTargetType
   * @return stockTargetType
  */
  
  @JsonProperty("stockTargetType")
  public String getStockTargetType() {
    return stockTargetType;
  }

  public void setStockTargetType(String stockTargetType) {
    this.stockTargetType = stockTargetType;
  }

  public PushDetailsResponse linkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * Get linkUrl
   * @return linkUrl
  */
  
  @JsonProperty("linkUrl")
  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public PushDetailsResponse stockCode(String stockCode) {
    this.stockCode = stockCode;
    return this;
  }

  /**
   * Get stockCode
   * @return stockCode
  */
  
  @JsonProperty("stockCode")
  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public PushDetailsResponse stockName(String stockName) {
    this.stockName = stockName;
    return this;
  }

  /**
   * Get stockName
   * @return stockName
  */
  
  @JsonProperty("stockName")
  public String getStockName() {
    return stockName;
  }

  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  public PushDetailsResponse stockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
    return this;
  }

  /**
   * Get stockGroupId
   * @return stockGroupId
  */
  
  @JsonProperty("stockGroupId")
  public Long getStockGroupId() {
    return stockGroupId;
  }

  public void setStockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
  }

  public PushDetailsResponse stockGroupName(String stockGroupName) {
    this.stockGroupName = stockGroupName;
    return this;
  }

  /**
   * Get stockGroupName
   * @return stockGroupName
  */
  
  @JsonProperty("stockGroupName")
  public String getStockGroupName() {
    return stockGroupName;
  }

  public void setStockGroupName(String stockGroupName) {
    this.stockGroupName = stockGroupName;
  }

  public PushDetailsResponse postId(Long postId) {
    this.postId = postId;
    return this;
  }

  /**
   * Get postId
   * @return postId
  */
  
  @JsonProperty("postId")
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public PushDetailsResponse sendType(String sendType) {
    this.sendType = sendType;
    return this;
  }

  /**
   * Get sendType
   * @return sendType
  */
  
  @JsonProperty("sendType")
  public String getSendType() {
    return sendType;
  }

  public void setSendType(String sendType) {
    this.sendType = sendType;
  }

  public PushDetailsResponse sendStatus(String sendStatus) {
    this.sendStatus = sendStatus;
    return this;
  }

  /**
   * Get sendStatus
   * @return sendStatus
  */
  
  @JsonProperty("sendStatus")
  public String getSendStatus() {
    return sendStatus;
  }

  public void setSendStatus(String sendStatus) {
    this.sendStatus = sendStatus;
  }

  public PushDetailsResponse topic(String topic) {
    this.topic = topic;
    return this;
  }

  /**
   * Get topic
   * @return topic
  */
  
  @JsonProperty("topic")
  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public PushDetailsResponse result(String result) {
    this.result = result;
    return this;
  }

  /**
   * Get result
   * @return result
  */
  
  @JsonProperty("result")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public PushDetailsResponse targetDatetime(java.time.Instant targetDatetime) {
    this.targetDatetime = targetDatetime;
    return this;
  }

  /**
   * Get targetDatetime
   * @return targetDatetime
  */
  @Valid 
  @JsonProperty("targetDatetime")
  public java.time.Instant getTargetDatetime() {
    return targetDatetime;
  }

  public void setTargetDatetime(java.time.Instant targetDatetime) {
    this.targetDatetime = targetDatetime;
  }

  public PushDetailsResponse sentStartDatetime(java.time.Instant sentStartDatetime) {
    this.sentStartDatetime = sentStartDatetime;
    return this;
  }

  /**
   * Get sentStartDatetime
   * @return sentStartDatetime
  */
  @Valid 
  @JsonProperty("sentStartDatetime")
  public java.time.Instant getSentStartDatetime() {
    return sentStartDatetime;
  }

  public void setSentStartDatetime(java.time.Instant sentStartDatetime) {
    this.sentStartDatetime = sentStartDatetime;
  }

  public PushDetailsResponse sentEndDatetime(java.time.Instant sentEndDatetime) {
    this.sentEndDatetime = sentEndDatetime;
    return this;
  }

  /**
   * Get sentEndDatetime
   * @return sentEndDatetime
  */
  @Valid 
  @JsonProperty("sentEndDatetime")
  public java.time.Instant getSentEndDatetime() {
    return sentEndDatetime;
  }

  public void setSentEndDatetime(java.time.Instant sentEndDatetime) {
    this.sentEndDatetime = sentEndDatetime;
  }

  public PushDetailsResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
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

  public PushDetailsResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PushDetailsResponse pushDetailsResponse = (PushDetailsResponse) o;
    return Objects.equals(this.id, pushDetailsResponse.id) &&
        Objects.equals(this.user, pushDetailsResponse.user) &&
        Objects.equals(this.title, pushDetailsResponse.title) &&
        Objects.equals(this.content, pushDetailsResponse.content) &&
        Objects.equals(this.linkType, pushDetailsResponse.linkType) &&
        Objects.equals(this.stockTargetType, pushDetailsResponse.stockTargetType) &&
        Objects.equals(this.linkUrl, pushDetailsResponse.linkUrl) &&
        Objects.equals(this.stockCode, pushDetailsResponse.stockCode) &&
        Objects.equals(this.stockName, pushDetailsResponse.stockName) &&
        Objects.equals(this.stockGroupId, pushDetailsResponse.stockGroupId) &&
        Objects.equals(this.stockGroupName, pushDetailsResponse.stockGroupName) &&
        Objects.equals(this.postId, pushDetailsResponse.postId) &&
        Objects.equals(this.sendType, pushDetailsResponse.sendType) &&
        Objects.equals(this.sendStatus, pushDetailsResponse.sendStatus) &&
        Objects.equals(this.topic, pushDetailsResponse.topic) &&
        Objects.equals(this.result, pushDetailsResponse.result) &&
        Objects.equals(this.targetDatetime, pushDetailsResponse.targetDatetime) &&
        Objects.equals(this.sentStartDatetime, pushDetailsResponse.sentStartDatetime) &&
        Objects.equals(this.sentEndDatetime, pushDetailsResponse.sentEndDatetime) &&
        Objects.equals(this.createdAt, pushDetailsResponse.createdAt) &&
        Objects.equals(this.updatedAt, pushDetailsResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, title, content, linkType, stockTargetType, linkUrl, stockCode, stockName, stockGroupId, stockGroupName, postId, sendType, sendStatus, topic, result, targetDatetime, sentStartDatetime, sentEndDatetime, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PushDetailsResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    linkType: ").append(toIndentedString(linkType)).append("\n");
    sb.append("    stockTargetType: ").append(toIndentedString(stockTargetType)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockName: ").append(toIndentedString(stockName)).append("\n");
    sb.append("    stockGroupId: ").append(toIndentedString(stockGroupId)).append("\n");
    sb.append("    stockGroupName: ").append(toIndentedString(stockGroupName)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    sendType: ").append(toIndentedString(sendType)).append("\n");
    sb.append("    sendStatus: ").append(toIndentedString(sendStatus)).append("\n");
    sb.append("    topic: ").append(toIndentedString(topic)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    targetDatetime: ").append(toIndentedString(targetDatetime)).append("\n");
    sb.append("    sentStartDatetime: ").append(toIndentedString(sentStartDatetime)).append("\n");
    sb.append("    sentEndDatetime: ").append(toIndentedString(sentEndDatetime)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

