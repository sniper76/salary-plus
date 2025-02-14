package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * UpdateDigitalDocumentRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateDigitalDocumentRequest {

  private String postTitle;

  private String postContent;

  private Long stockReferenceDateId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  @Valid
  private List<Long> imageIds;

  public UpdateDigitalDocumentRequest postTitle(String postTitle) {
    this.postTitle = postTitle;
    return this;
  }

  /**
   * 게시글 제목
   * @return postTitle
  */
  
  @JsonProperty("postTitle")
  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
  }

  public UpdateDigitalDocumentRequest postContent(String postContent) {
    this.postContent = postContent;
    return this;
  }

  /**
   * 게시글 내용
   * @return postContent
  */
  
  @JsonProperty("postContent")
  public String getPostContent() {
    return postContent;
  }

  public void setPostContent(String postContent) {
    this.postContent = postContent;
  }

  public UpdateDigitalDocumentRequest stockReferenceDateId(Long stockReferenceDateId) {
    this.stockReferenceDateId = stockReferenceDateId;
    return this;
  }

  /**
   * 기준일 정보 아이디
   * @return stockReferenceDateId
  */
  
  @JsonProperty("stockReferenceDateId")
  public Long getStockReferenceDateId() {
    return stockReferenceDateId;
  }

  public void setStockReferenceDateId(Long stockReferenceDateId) {
    this.stockReferenceDateId = stockReferenceDateId;
  }

  public UpdateDigitalDocumentRequest targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * 종료일
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

  public UpdateDigitalDocumentRequest imageIds(List<Long> imageIds) {
    this.imageIds = imageIds;
    return this;
  }

  public UpdateDigitalDocumentRequest addImageIdsItem(Long imageIdsItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateDigitalDocumentRequest updateDigitalDocumentRequest = (UpdateDigitalDocumentRequest) o;
    return Objects.equals(this.postTitle, updateDigitalDocumentRequest.postTitle) &&
        Objects.equals(this.postContent, updateDigitalDocumentRequest.postContent) &&
        Objects.equals(this.stockReferenceDateId, updateDigitalDocumentRequest.stockReferenceDateId) &&
        Objects.equals(this.targetEndDate, updateDigitalDocumentRequest.targetEndDate) &&
        Objects.equals(this.imageIds, updateDigitalDocumentRequest.imageIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postTitle, postContent, stockReferenceDateId, targetEndDate, imageIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateDigitalDocumentRequest {\n");
    sb.append("    postTitle: ").append(toIndentedString(postTitle)).append("\n");
    sb.append("    postContent: ").append(toIndentedString(postContent)).append("\n");
    sb.append("    stockReferenceDateId: ").append(toIndentedString(stockReferenceDateId)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    imageIds: ").append(toIndentedString(imageIds)).append("\n");
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

