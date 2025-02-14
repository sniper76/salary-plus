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
 * ReportListResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ReportListResponse {

  private Long reportId;

  private String title;

  private Long contentId;

  private String contentType;

  private String boardCategoryName;

  private String boardCategoryDisplayName;

  private String boardGroupName;

  private String stockCode;

  private String stockName;

  private String reportStatus;

  private Long likeCount;

  private Long commentCount;

  private Long replyCount;

  private Long viewCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public ReportListResponse reportId(Long reportId) {
    this.reportId = reportId;
    return this;
  }

  /**
   * Get reportId
   * @return reportId
  */
  
  @JsonProperty("reportId")
  public Long getReportId() {
    return reportId;
  }

  public void setReportId(Long reportId) {
    this.reportId = reportId;
  }

  public ReportListResponse title(String title) {
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

  public ReportListResponse contentId(Long contentId) {
    this.contentId = contentId;
    return this;
  }

  /**
   * Get contentId
   * @return contentId
  */
  
  @JsonProperty("contentId")
  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  public ReportListResponse contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * Get contentType
   * @return contentType
  */
  
  @JsonProperty("contentType")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public ReportListResponse boardCategoryName(String boardCategoryName) {
    this.boardCategoryName = boardCategoryName;
    return this;
  }

  /**
   * Get boardCategoryName
   * @return boardCategoryName
  */
  
  @JsonProperty("boardCategoryName")
  public String getBoardCategoryName() {
    return boardCategoryName;
  }

  public void setBoardCategoryName(String boardCategoryName) {
    this.boardCategoryName = boardCategoryName;
  }

  public ReportListResponse boardCategoryDisplayName(String boardCategoryDisplayName) {
    this.boardCategoryDisplayName = boardCategoryDisplayName;
    return this;
  }

  /**
   * Get boardCategoryDisplayName
   * @return boardCategoryDisplayName
  */
  
  @JsonProperty("boardCategoryDisplayName")
  public String getBoardCategoryDisplayName() {
    return boardCategoryDisplayName;
  }

  public void setBoardCategoryDisplayName(String boardCategoryDisplayName) {
    this.boardCategoryDisplayName = boardCategoryDisplayName;
  }

  public ReportListResponse boardGroupName(String boardGroupName) {
    this.boardGroupName = boardGroupName;
    return this;
  }

  /**
   * Get boardGroupName
   * @return boardGroupName
  */
  
  @JsonProperty("boardGroupName")
  public String getBoardGroupName() {
    return boardGroupName;
  }

  public void setBoardGroupName(String boardGroupName) {
    this.boardGroupName = boardGroupName;
  }

  public ReportListResponse stockCode(String stockCode) {
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

  public ReportListResponse stockName(String stockName) {
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

  public ReportListResponse reportStatus(String reportStatus) {
    this.reportStatus = reportStatus;
    return this;
  }

  /**
   * Get reportStatus
   * @return reportStatus
  */
  
  @JsonProperty("reportStatus")
  public String getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(String reportStatus) {
    this.reportStatus = reportStatus;
  }

  public ReportListResponse likeCount(Long likeCount) {
    this.likeCount = likeCount;
    return this;
  }

  /**
   * Get likeCount
   * @return likeCount
  */
  
  @JsonProperty("likeCount")
  public Long getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Long likeCount) {
    this.likeCount = likeCount;
  }

  public ReportListResponse commentCount(Long commentCount) {
    this.commentCount = commentCount;
    return this;
  }

  /**
   * Get commentCount
   * @return commentCount
  */
  
  @JsonProperty("commentCount")
  public Long getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Long commentCount) {
    this.commentCount = commentCount;
  }

  public ReportListResponse replyCount(Long replyCount) {
    this.replyCount = replyCount;
    return this;
  }

  /**
   * Get replyCount
   * @return replyCount
  */
  
  @JsonProperty("replyCount")
  public Long getReplyCount() {
    return replyCount;
  }

  public void setReplyCount(Long replyCount) {
    this.replyCount = replyCount;
  }

  public ReportListResponse viewCount(Long viewCount) {
    this.viewCount = viewCount;
    return this;
  }

  /**
   * Get viewCount
   * @return viewCount
  */
  
  @JsonProperty("viewCount")
  public Long getViewCount() {
    return viewCount;
  }

  public void setViewCount(Long viewCount) {
    this.viewCount = viewCount;
  }

  public ReportListResponse createdAt(java.time.Instant createdAt) {
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

  public ReportListResponse updatedAt(java.time.Instant updatedAt) {
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
    ReportListResponse reportListResponse = (ReportListResponse) o;
    return Objects.equals(this.reportId, reportListResponse.reportId) &&
        Objects.equals(this.title, reportListResponse.title) &&
        Objects.equals(this.contentId, reportListResponse.contentId) &&
        Objects.equals(this.contentType, reportListResponse.contentType) &&
        Objects.equals(this.boardCategoryName, reportListResponse.boardCategoryName) &&
        Objects.equals(this.boardCategoryDisplayName, reportListResponse.boardCategoryDisplayName) &&
        Objects.equals(this.boardGroupName, reportListResponse.boardGroupName) &&
        Objects.equals(this.stockCode, reportListResponse.stockCode) &&
        Objects.equals(this.stockName, reportListResponse.stockName) &&
        Objects.equals(this.reportStatus, reportListResponse.reportStatus) &&
        Objects.equals(this.likeCount, reportListResponse.likeCount) &&
        Objects.equals(this.commentCount, reportListResponse.commentCount) &&
        Objects.equals(this.replyCount, reportListResponse.replyCount) &&
        Objects.equals(this.viewCount, reportListResponse.viewCount) &&
        Objects.equals(this.createdAt, reportListResponse.createdAt) &&
        Objects.equals(this.updatedAt, reportListResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reportId, title, contentId, contentType, boardCategoryName, boardCategoryDisplayName, boardGroupName, stockCode, stockName, reportStatus, likeCount, commentCount, replyCount, viewCount, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportListResponse {\n");
    sb.append("    reportId: ").append(toIndentedString(reportId)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    contentId: ").append(toIndentedString(contentId)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    boardCategoryName: ").append(toIndentedString(boardCategoryName)).append("\n");
    sb.append("    boardCategoryDisplayName: ").append(toIndentedString(boardCategoryDisplayName)).append("\n");
    sb.append("    boardGroupName: ").append(toIndentedString(boardGroupName)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockName: ").append(toIndentedString(stockName)).append("\n");
    sb.append("    reportStatus: ").append(toIndentedString(reportStatus)).append("\n");
    sb.append("    likeCount: ").append(toIndentedString(likeCount)).append("\n");
    sb.append("    commentCount: ").append(toIndentedString(commentCount)).append("\n");
    sb.append("    replyCount: ").append(toIndentedString(replyCount)).append("\n");
    sb.append("    viewCount: ").append(toIndentedString(viewCount)).append("\n");
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

