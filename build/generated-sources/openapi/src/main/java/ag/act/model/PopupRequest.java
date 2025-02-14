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
 * PopupRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PopupRequest {

  private Long id;

  private String title;

  private String content;

  private String displayTargetType;

  private String stockTargetType;

  private String stockCode;

  private Long stockGroupId;

  private String linkType;

  private String linkTitle;

  private String status = "ACTIVE";

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDatetime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDatetime;

  private Long postId;

  public PopupRequest id(Long id) {
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

  public PopupRequest title(String title) {
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

  public PopupRequest content(String content) {
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

  public PopupRequest displayTargetType(String displayTargetType) {
    this.displayTargetType = displayTargetType;
    return this;
  }

  /**
   * Get displayTargetType
   * @return displayTargetType
  */
  
  @JsonProperty("displayTargetType")
  public String getDisplayTargetType() {
    return displayTargetType;
  }

  public void setDisplayTargetType(String displayTargetType) {
    this.displayTargetType = displayTargetType;
  }

  public PopupRequest stockTargetType(String stockTargetType) {
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

  public PopupRequest stockCode(String stockCode) {
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

  public PopupRequest stockGroupId(Long stockGroupId) {
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

  public PopupRequest linkType(String linkType) {
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

  public PopupRequest linkTitle(String linkTitle) {
    this.linkTitle = linkTitle;
    return this;
  }

  /**
   * Get linkTitle
   * @return linkTitle
  */
  
  @JsonProperty("linkTitle")
  public String getLinkTitle() {
    return linkTitle;
  }

  public void setLinkTitle(String linkTitle) {
    this.linkTitle = linkTitle;
  }

  public PopupRequest status(String status) {
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

  public PopupRequest targetStartDatetime(java.time.Instant targetStartDatetime) {
    this.targetStartDatetime = targetStartDatetime;
    return this;
  }

  /**
   * Get targetStartDatetime
   * @return targetStartDatetime
  */
  @Valid 
  @JsonProperty("targetStartDatetime")
  public java.time.Instant getTargetStartDatetime() {
    return targetStartDatetime;
  }

  public void setTargetStartDatetime(java.time.Instant targetStartDatetime) {
    this.targetStartDatetime = targetStartDatetime;
  }

  public PopupRequest targetEndDatetime(java.time.Instant targetEndDatetime) {
    this.targetEndDatetime = targetEndDatetime;
    return this;
  }

  /**
   * Get targetEndDatetime
   * @return targetEndDatetime
  */
  @Valid 
  @JsonProperty("targetEndDatetime")
  public java.time.Instant getTargetEndDatetime() {
    return targetEndDatetime;
  }

  public void setTargetEndDatetime(java.time.Instant targetEndDatetime) {
    this.targetEndDatetime = targetEndDatetime;
  }

  public PopupRequest postId(Long postId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PopupRequest popupRequest = (PopupRequest) o;
    return Objects.equals(this.id, popupRequest.id) &&
        Objects.equals(this.title, popupRequest.title) &&
        Objects.equals(this.content, popupRequest.content) &&
        Objects.equals(this.displayTargetType, popupRequest.displayTargetType) &&
        Objects.equals(this.stockTargetType, popupRequest.stockTargetType) &&
        Objects.equals(this.stockCode, popupRequest.stockCode) &&
        Objects.equals(this.stockGroupId, popupRequest.stockGroupId) &&
        Objects.equals(this.linkType, popupRequest.linkType) &&
        Objects.equals(this.linkTitle, popupRequest.linkTitle) &&
        Objects.equals(this.status, popupRequest.status) &&
        Objects.equals(this.targetStartDatetime, popupRequest.targetStartDatetime) &&
        Objects.equals(this.targetEndDatetime, popupRequest.targetEndDatetime) &&
        Objects.equals(this.postId, popupRequest.postId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, displayTargetType, stockTargetType, stockCode, stockGroupId, linkType, linkTitle, status, targetStartDatetime, targetEndDatetime, postId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PopupRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    displayTargetType: ").append(toIndentedString(displayTargetType)).append("\n");
    sb.append("    stockTargetType: ").append(toIndentedString(stockTargetType)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockGroupId: ").append(toIndentedString(stockGroupId)).append("\n");
    sb.append("    linkType: ").append(toIndentedString(linkType)).append("\n");
    sb.append("    linkTitle: ").append(toIndentedString(linkTitle)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    targetStartDatetime: ").append(toIndentedString(targetStartDatetime)).append("\n");
    sb.append("    targetEndDatetime: ").append(toIndentedString(targetEndDatetime)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
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

