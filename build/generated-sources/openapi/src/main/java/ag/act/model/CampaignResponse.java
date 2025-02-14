package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * CampaignResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CampaignResponse {

  private Long id;

  private String title;

  private Long sourcePostId;

  private Long sourceStockGroupId;

  private String sourceStockGroupName;

  private Long mappedStocksCount;

  private Integer joinUserCount;

  private Long joinStockCount;

  private Status status;

  private String boardCategory;

  private Long stockQuantity;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  private Boolean isPoll = false;

  private Boolean isDigitalDocument = false;

  private DigitalDocumentDownloadResponse campaignDownload;

  public CampaignResponse id(Long id) {
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

  public CampaignResponse title(String title) {
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

  public CampaignResponse sourcePostId(Long sourcePostId) {
    this.sourcePostId = sourcePostId;
    return this;
  }

  /**
   * Get sourcePostId
   * @return sourcePostId
  */
  
  @JsonProperty("sourcePostId")
  public Long getSourcePostId() {
    return sourcePostId;
  }

  public void setSourcePostId(Long sourcePostId) {
    this.sourcePostId = sourcePostId;
  }

  public CampaignResponse sourceStockGroupId(Long sourceStockGroupId) {
    this.sourceStockGroupId = sourceStockGroupId;
    return this;
  }

  /**
   * Get sourceStockGroupId
   * @return sourceStockGroupId
  */
  
  @JsonProperty("sourceStockGroupId")
  public Long getSourceStockGroupId() {
    return sourceStockGroupId;
  }

  public void setSourceStockGroupId(Long sourceStockGroupId) {
    this.sourceStockGroupId = sourceStockGroupId;
  }

  public CampaignResponse sourceStockGroupName(String sourceStockGroupName) {
    this.sourceStockGroupName = sourceStockGroupName;
    return this;
  }

  /**
   * Get sourceStockGroupName
   * @return sourceStockGroupName
  */
  
  @JsonProperty("sourceStockGroupName")
  public String getSourceStockGroupName() {
    return sourceStockGroupName;
  }

  public void setSourceStockGroupName(String sourceStockGroupName) {
    this.sourceStockGroupName = sourceStockGroupName;
  }

  public CampaignResponse mappedStocksCount(Long mappedStocksCount) {
    this.mappedStocksCount = mappedStocksCount;
    return this;
  }

  /**
   * Get mappedStocksCount
   * @return mappedStocksCount
  */
  
  @JsonProperty("mappedStocksCount")
  public Long getMappedStocksCount() {
    return mappedStocksCount;
  }

  public void setMappedStocksCount(Long mappedStocksCount) {
    this.mappedStocksCount = mappedStocksCount;
  }

  public CampaignResponse joinUserCount(Integer joinUserCount) {
    this.joinUserCount = joinUserCount;
    return this;
  }

  /**
   * Get joinUserCount
   * @return joinUserCount
  */
  
  @JsonProperty("joinUserCount")
  public Integer getJoinUserCount() {
    return joinUserCount;
  }

  public void setJoinUserCount(Integer joinUserCount) {
    this.joinUserCount = joinUserCount;
  }

  public CampaignResponse joinStockCount(Long joinStockCount) {
    this.joinStockCount = joinStockCount;
    return this;
  }

  /**
   * Get joinStockCount
   * @return joinStockCount
  */
  
  @JsonProperty("joinStockCount")
  public Long getJoinStockCount() {
    return joinStockCount;
  }

  public void setJoinStockCount(Long joinStockCount) {
    this.joinStockCount = joinStockCount;
  }

  public CampaignResponse status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @JsonProperty("status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public CampaignResponse boardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
    return this;
  }

  /**
   * Get boardCategory
   * @return boardCategory
  */
  
  @JsonProperty("boardCategory")
  public String getBoardCategory() {
    return boardCategory;
  }

  public void setBoardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
  }

  public CampaignResponse stockQuantity(Long stockQuantity) {
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

  public CampaignResponse targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * Get targetEndDate
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

  public CampaignResponse createdAt(java.time.Instant createdAt) {
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

  public CampaignResponse updatedAt(java.time.Instant updatedAt) {
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

  public CampaignResponse deletedAt(java.time.Instant deletedAt) {
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

  public CampaignResponse isPoll(Boolean isPoll) {
    this.isPoll = isPoll;
    return this;
  }

  /**
   * Get isPoll
   * @return isPoll
  */
  
  @JsonProperty("isPoll")
  public Boolean getIsPoll() {
    return isPoll;
  }

  public void setIsPoll(Boolean isPoll) {
    this.isPoll = isPoll;
  }

  public CampaignResponse isDigitalDocument(Boolean isDigitalDocument) {
    this.isDigitalDocument = isDigitalDocument;
    return this;
  }

  /**
   * Get isDigitalDocument
   * @return isDigitalDocument
  */
  
  @JsonProperty("isDigitalDocument")
  public Boolean getIsDigitalDocument() {
    return isDigitalDocument;
  }

  public void setIsDigitalDocument(Boolean isDigitalDocument) {
    this.isDigitalDocument = isDigitalDocument;
  }

  public CampaignResponse campaignDownload(DigitalDocumentDownloadResponse campaignDownload) {
    this.campaignDownload = campaignDownload;
    return this;
  }

  /**
   * Get campaignDownload
   * @return campaignDownload
  */
  @Valid 
  @JsonProperty("campaignDownload")
  public DigitalDocumentDownloadResponse getCampaignDownload() {
    return campaignDownload;
  }

  public void setCampaignDownload(DigitalDocumentDownloadResponse campaignDownload) {
    this.campaignDownload = campaignDownload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CampaignResponse campaignResponse = (CampaignResponse) o;
    return Objects.equals(this.id, campaignResponse.id) &&
        Objects.equals(this.title, campaignResponse.title) &&
        Objects.equals(this.sourcePostId, campaignResponse.sourcePostId) &&
        Objects.equals(this.sourceStockGroupId, campaignResponse.sourceStockGroupId) &&
        Objects.equals(this.sourceStockGroupName, campaignResponse.sourceStockGroupName) &&
        Objects.equals(this.mappedStocksCount, campaignResponse.mappedStocksCount) &&
        Objects.equals(this.joinUserCount, campaignResponse.joinUserCount) &&
        Objects.equals(this.joinStockCount, campaignResponse.joinStockCount) &&
        Objects.equals(this.status, campaignResponse.status) &&
        Objects.equals(this.boardCategory, campaignResponse.boardCategory) &&
        Objects.equals(this.stockQuantity, campaignResponse.stockQuantity) &&
        Objects.equals(this.targetEndDate, campaignResponse.targetEndDate) &&
        Objects.equals(this.createdAt, campaignResponse.createdAt) &&
        Objects.equals(this.updatedAt, campaignResponse.updatedAt) &&
        Objects.equals(this.deletedAt, campaignResponse.deletedAt) &&
        Objects.equals(this.isPoll, campaignResponse.isPoll) &&
        Objects.equals(this.isDigitalDocument, campaignResponse.isDigitalDocument) &&
        Objects.equals(this.campaignDownload, campaignResponse.campaignDownload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, sourcePostId, sourceStockGroupId, sourceStockGroupName, mappedStocksCount, joinUserCount, joinStockCount, status, boardCategory, stockQuantity, targetEndDate, createdAt, updatedAt, deletedAt, isPoll, isDigitalDocument, campaignDownload);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CampaignResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    sourcePostId: ").append(toIndentedString(sourcePostId)).append("\n");
    sb.append("    sourceStockGroupId: ").append(toIndentedString(sourceStockGroupId)).append("\n");
    sb.append("    sourceStockGroupName: ").append(toIndentedString(sourceStockGroupName)).append("\n");
    sb.append("    mappedStocksCount: ").append(toIndentedString(mappedStocksCount)).append("\n");
    sb.append("    joinUserCount: ").append(toIndentedString(joinUserCount)).append("\n");
    sb.append("    joinStockCount: ").append(toIndentedString(joinStockCount)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    boardCategory: ").append(toIndentedString(boardCategory)).append("\n");
    sb.append("    stockQuantity: ").append(toIndentedString(stockQuantity)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
    sb.append("    isPoll: ").append(toIndentedString(isPoll)).append("\n");
    sb.append("    isDigitalDocument: ").append(toIndentedString(isDigitalDocument)).append("\n");
    sb.append("    campaignDownload: ").append(toIndentedString(campaignDownload)).append("\n");
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

