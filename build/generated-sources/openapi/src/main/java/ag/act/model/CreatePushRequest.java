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
 * CreatePushRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreatePushRequest {

  @NotBlank(message = "제목을 확인해주세요.")

  private String title = "액트";

  @NotBlank(message = "내용을 확인해주세요.")

  private String content;

  private String stockCode;

  private Long stockGroupId;

  private Long postId;

  private String linkType = "NONE";

  @NotBlank(message = "타겟타입을 확인해주세요.")

  private String stockTargetType;

  @NotBlank(message = "발송타입을 확인해주세요.")

  private String sendType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetDatetime = null;

  public CreatePushRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the push is mandatory
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CreatePushRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Content of the post is mandatory
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public CreatePushRequest stockCode(String stockCode) {
    this.stockCode = stockCode;
    return this;
  }

  /**
   * Stock code of the push
   * @return stockCode
  */
  
  @JsonProperty("stockCode")
  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public CreatePushRequest stockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
    return this;
  }

  /**
   * Stock group id of the push
   * @return stockGroupId
  */
  
  @JsonProperty("stockGroupId")
  public Long getStockGroupId() {
    return stockGroupId;
  }

  public void setStockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
  }

  public CreatePushRequest postId(Long postId) {
    this.postId = postId;
    return this;
  }

  /**
   * postId of where to link to
   * @return postId
  */
  
  @JsonProperty("postId")
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public CreatePushRequest linkType(String linkType) {
    this.linkType = linkType;
    return this;
  }

  /**
   * where should it link to when the push notification is tapped
   * @return linkType
  */
  
  @JsonProperty("linkType")
  public String getLinkType() {
    return linkType;
  }

  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  public CreatePushRequest stockTargetType(String stockTargetType) {
    this.stockTargetType = stockTargetType;
    return this;
  }

  /**
   * Send target. ALL or STOCK or STOCK_GROUP
   * @return stockTargetType
  */
  
  @JsonProperty("stockTargetType")
  public String getStockTargetType() {
    return stockTargetType;
  }

  public void setStockTargetType(String stockTargetType) {
    this.stockTargetType = stockTargetType;
  }

  public CreatePushRequest sendType(String sendType) {
    this.sendType = sendType;
    return this;
  }

  /**
   * Send type. SCHEDULE or IMMEDIATELY
   * @return sendType
  */
  
  @JsonProperty("sendType")
  public String getSendType() {
    return sendType;
  }

  public void setSendType(String sendType) {
    this.sendType = sendType;
  }

  public CreatePushRequest targetDatetime(java.time.Instant targetDatetime) {
    this.targetDatetime = targetDatetime;
    return this;
  }

  /**
   * Send target datetime for SCHEDULE type
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePushRequest createPushRequest = (CreatePushRequest) o;
    return Objects.equals(this.title, createPushRequest.title) &&
        Objects.equals(this.content, createPushRequest.content) &&
        Objects.equals(this.stockCode, createPushRequest.stockCode) &&
        Objects.equals(this.stockGroupId, createPushRequest.stockGroupId) &&
        Objects.equals(this.postId, createPushRequest.postId) &&
        Objects.equals(this.linkType, createPushRequest.linkType) &&
        Objects.equals(this.stockTargetType, createPushRequest.stockTargetType) &&
        Objects.equals(this.sendType, createPushRequest.sendType) &&
        Objects.equals(this.targetDatetime, createPushRequest.targetDatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, content, stockCode, stockGroupId, postId, linkType, stockTargetType, sendType, targetDatetime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePushRequest {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockGroupId: ").append(toIndentedString(stockGroupId)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    linkType: ").append(toIndentedString(linkType)).append("\n");
    sb.append("    stockTargetType: ").append(toIndentedString(stockTargetType)).append("\n");
    sb.append("    sendType: ").append(toIndentedString(sendType)).append("\n");
    sb.append("    targetDatetime: ").append(toIndentedString(targetDatetime)).append("\n");
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

