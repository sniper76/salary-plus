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
 * PopupDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PopupDetailsResponse {

  private Long id;

  private String title;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDatetime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDatetime;

  private String popupStatus;

  private String linkType;

  private String linkTitle;

  private String linkUrl;

  private String displayTargetType;

  private String stockCode = null;

  private String stockName = null;

  private Long stockGroupId;

  private String stockGroupName = null;

  private String stockTargetType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  private Long postId;

  public PopupDetailsResponse id(Long id) {
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

  public PopupDetailsResponse title(String title) {
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

  public PopupDetailsResponse content(String content) {
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

  public PopupDetailsResponse targetStartDatetime(java.time.Instant targetStartDatetime) {
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

  public PopupDetailsResponse targetEndDatetime(java.time.Instant targetEndDatetime) {
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

  public PopupDetailsResponse popupStatus(String popupStatus) {
    this.popupStatus = popupStatus;
    return this;
  }

  /**
   * Get popupStatus
   * @return popupStatus
  */
  
  @JsonProperty("popupStatus")
  public String getPopupStatus() {
    return popupStatus;
  }

  public void setPopupStatus(String popupStatus) {
    this.popupStatus = popupStatus;
  }

  public PopupDetailsResponse linkType(String linkType) {
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

  public PopupDetailsResponse linkTitle(String linkTitle) {
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

  public PopupDetailsResponse linkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * linkUrl should be null when linkType is NONE or NOTIFICATION
   * @return linkUrl
  */
  
  @JsonProperty("linkUrl")
  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public PopupDetailsResponse displayTargetType(String displayTargetType) {
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

  public PopupDetailsResponse stockCode(String stockCode) {
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

  public PopupDetailsResponse stockName(String stockName) {
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

  public PopupDetailsResponse stockGroupId(Long stockGroupId) {
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

  public PopupDetailsResponse stockGroupName(String stockGroupName) {
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

  public PopupDetailsResponse stockTargetType(String stockTargetType) {
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

  public PopupDetailsResponse createdAt(java.time.Instant createdAt) {
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

  public PopupDetailsResponse updatedAt(java.time.Instant updatedAt) {
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

  public PopupDetailsResponse postId(Long postId) {
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
    PopupDetailsResponse popupDetailsResponse = (PopupDetailsResponse) o;
    return Objects.equals(this.id, popupDetailsResponse.id) &&
        Objects.equals(this.title, popupDetailsResponse.title) &&
        Objects.equals(this.content, popupDetailsResponse.content) &&
        Objects.equals(this.targetStartDatetime, popupDetailsResponse.targetStartDatetime) &&
        Objects.equals(this.targetEndDatetime, popupDetailsResponse.targetEndDatetime) &&
        Objects.equals(this.popupStatus, popupDetailsResponse.popupStatus) &&
        Objects.equals(this.linkType, popupDetailsResponse.linkType) &&
        Objects.equals(this.linkTitle, popupDetailsResponse.linkTitle) &&
        Objects.equals(this.linkUrl, popupDetailsResponse.linkUrl) &&
        Objects.equals(this.displayTargetType, popupDetailsResponse.displayTargetType) &&
        Objects.equals(this.stockCode, popupDetailsResponse.stockCode) &&
        Objects.equals(this.stockName, popupDetailsResponse.stockName) &&
        Objects.equals(this.stockGroupId, popupDetailsResponse.stockGroupId) &&
        Objects.equals(this.stockGroupName, popupDetailsResponse.stockGroupName) &&
        Objects.equals(this.stockTargetType, popupDetailsResponse.stockTargetType) &&
        Objects.equals(this.createdAt, popupDetailsResponse.createdAt) &&
        Objects.equals(this.updatedAt, popupDetailsResponse.updatedAt) &&
        Objects.equals(this.postId, popupDetailsResponse.postId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, targetStartDatetime, targetEndDatetime, popupStatus, linkType, linkTitle, linkUrl, displayTargetType, stockCode, stockName, stockGroupId, stockGroupName, stockTargetType, createdAt, updatedAt, postId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PopupDetailsResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    targetStartDatetime: ").append(toIndentedString(targetStartDatetime)).append("\n");
    sb.append("    targetEndDatetime: ").append(toIndentedString(targetEndDatetime)).append("\n");
    sb.append("    popupStatus: ").append(toIndentedString(popupStatus)).append("\n");
    sb.append("    linkType: ").append(toIndentedString(linkType)).append("\n");
    sb.append("    linkTitle: ").append(toIndentedString(linkTitle)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    displayTargetType: ").append(toIndentedString(displayTargetType)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockName: ").append(toIndentedString(stockName)).append("\n");
    sb.append("    stockGroupId: ").append(toIndentedString(stockGroupId)).append("\n");
    sb.append("    stockGroupName: ").append(toIndentedString(stockGroupName)).append("\n");
    sb.append("    stockTargetType: ").append(toIndentedString(stockTargetType)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

