package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * 전자문서 위임장 공동보유 기타문서 입력 정보
 */

@JsonTypeName("UpdatePostRequest_digitalDocument")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdatePostRequestDigitalDocument implements ag.act.dto.TargetStartAndEndDatePeriod {

  private Long stockReferenceDateId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private String title;

  private String content;

  public UpdatePostRequestDigitalDocument stockReferenceDateId(Long stockReferenceDateId) {
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

  public UpdatePostRequestDigitalDocument targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * 시작일
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

  public UpdatePostRequestDigitalDocument targetEndDate(java.time.Instant targetEndDate) {
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

  public UpdatePostRequestDigitalDocument title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 전자문서 제목
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UpdatePostRequestDigitalDocument content(String content) {
    this.content = content;
    return this;
  }

  /**
   * 전자문서 내용
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdatePostRequestDigitalDocument updatePostRequestDigitalDocument = (UpdatePostRequestDigitalDocument) o;
    return Objects.equals(this.stockReferenceDateId, updatePostRequestDigitalDocument.stockReferenceDateId) &&
        Objects.equals(this.targetStartDate, updatePostRequestDigitalDocument.targetStartDate) &&
        Objects.equals(this.targetEndDate, updatePostRequestDigitalDocument.targetEndDate) &&
        Objects.equals(this.title, updatePostRequestDigitalDocument.title) &&
        Objects.equals(this.content, updatePostRequestDigitalDocument.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stockReferenceDateId, targetStartDate, targetEndDate, title, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdatePostRequestDigitalDocument {\n");
    sb.append("    stockReferenceDateId: ").append(toIndentedString(stockReferenceDateId)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

