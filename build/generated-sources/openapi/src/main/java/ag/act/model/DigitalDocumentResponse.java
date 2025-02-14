package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.JsonAttachOption;
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
 * DigitalDocumentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentResponse {

  private Long id;

  private String title;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant stockReferenceDate;

  private String companyName;

  private String companyRegistrationNumber;

  private String shareholderMeetingType;

  private String shareholderMeetingName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant shareholderMeetingDate;

  private String designatedAgentNames;

  private Long acceptUserId;

  private String type;

  private String version;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @Valid
  private List<@Valid DigitalDocumentItemResponse> items;

  private JsonAttachOption attachOptions;

  private DigitalDocumentDownloadResponse digitalDocumentDownload;

  public DigitalDocumentResponse id(Long id) {
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

  public DigitalDocumentResponse title(String title) {
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

  public DigitalDocumentResponse content(String content) {
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

  public DigitalDocumentResponse stockReferenceDate(java.time.Instant stockReferenceDate) {
    this.stockReferenceDate = stockReferenceDate;
    return this;
  }

  /**
   * Get stockReferenceDate
   * @return stockReferenceDate
  */
  @Valid 
  @JsonProperty("stockReferenceDate")
  public java.time.Instant getStockReferenceDate() {
    return stockReferenceDate;
  }

  public void setStockReferenceDate(java.time.Instant stockReferenceDate) {
    this.stockReferenceDate = stockReferenceDate;
  }

  public DigitalDocumentResponse companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
  */
  
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public DigitalDocumentResponse companyRegistrationNumber(String companyRegistrationNumber) {
    this.companyRegistrationNumber = companyRegistrationNumber;
    return this;
  }

  /**
   * Get companyRegistrationNumber
   * @return companyRegistrationNumber
  */
  
  @JsonProperty("companyRegistrationNumber")
  public String getCompanyRegistrationNumber() {
    return companyRegistrationNumber;
  }

  public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
    this.companyRegistrationNumber = companyRegistrationNumber;
  }

  public DigitalDocumentResponse shareholderMeetingType(String shareholderMeetingType) {
    this.shareholderMeetingType = shareholderMeetingType;
    return this;
  }

  /**
   * Get shareholderMeetingType
   * @return shareholderMeetingType
  */
  
  @JsonProperty("shareholderMeetingType")
  public String getShareholderMeetingType() {
    return shareholderMeetingType;
  }

  public void setShareholderMeetingType(String shareholderMeetingType) {
    this.shareholderMeetingType = shareholderMeetingType;
  }

  public DigitalDocumentResponse shareholderMeetingName(String shareholderMeetingName) {
    this.shareholderMeetingName = shareholderMeetingName;
    return this;
  }

  /**
   * Get shareholderMeetingName
   * @return shareholderMeetingName
  */
  
  @JsonProperty("shareholderMeetingName")
  public String getShareholderMeetingName() {
    return shareholderMeetingName;
  }

  public void setShareholderMeetingName(String shareholderMeetingName) {
    this.shareholderMeetingName = shareholderMeetingName;
  }

  public DigitalDocumentResponse shareholderMeetingDate(java.time.Instant shareholderMeetingDate) {
    this.shareholderMeetingDate = shareholderMeetingDate;
    return this;
  }

  /**
   * Get shareholderMeetingDate
   * @return shareholderMeetingDate
  */
  @Valid 
  @JsonProperty("shareholderMeetingDate")
  public java.time.Instant getShareholderMeetingDate() {
    return shareholderMeetingDate;
  }

  public void setShareholderMeetingDate(java.time.Instant shareholderMeetingDate) {
    this.shareholderMeetingDate = shareholderMeetingDate;
  }

  public DigitalDocumentResponse designatedAgentNames(String designatedAgentNames) {
    this.designatedAgentNames = designatedAgentNames;
    return this;
  }

  /**
   * Get designatedAgentNames
   * @return designatedAgentNames
  */
  
  @JsonProperty("designatedAgentNames")
  public String getDesignatedAgentNames() {
    return designatedAgentNames;
  }

  public void setDesignatedAgentNames(String designatedAgentNames) {
    this.designatedAgentNames = designatedAgentNames;
  }

  public DigitalDocumentResponse acceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
    return this;
  }

  /**
   * Get acceptUserId
   * @return acceptUserId
  */
  
  @JsonProperty("acceptUserId")
  public Long getAcceptUserId() {
    return acceptUserId;
  }

  public void setAcceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
  }

  public DigitalDocumentResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public DigitalDocumentResponse version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
  */
  
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public DigitalDocumentResponse targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * Get targetStartDate
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

  public DigitalDocumentResponse targetEndDate(java.time.Instant targetEndDate) {
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

  public DigitalDocumentResponse createdAt(java.time.Instant createdAt) {
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

  public DigitalDocumentResponse updatedAt(java.time.Instant updatedAt) {
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

  public DigitalDocumentResponse items(List<@Valid DigitalDocumentItemResponse> items) {
    this.items = items;
    return this;
  }

  public DigitalDocumentResponse addItemsItem(DigitalDocumentItemResponse itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid 
  @JsonProperty("items")
  public List<@Valid DigitalDocumentItemResponse> getItems() {
    return items;
  }

  public void setItems(List<@Valid DigitalDocumentItemResponse> items) {
    this.items = items;
  }

  public DigitalDocumentResponse attachOptions(JsonAttachOption attachOptions) {
    this.attachOptions = attachOptions;
    return this;
  }

  /**
   * Get attachOptions
   * @return attachOptions
  */
  @Valid 
  @JsonProperty("attachOptions")
  public JsonAttachOption getAttachOptions() {
    return attachOptions;
  }

  public void setAttachOptions(JsonAttachOption attachOptions) {
    this.attachOptions = attachOptions;
  }

  public DigitalDocumentResponse digitalDocumentDownload(DigitalDocumentDownloadResponse digitalDocumentDownload) {
    this.digitalDocumentDownload = digitalDocumentDownload;
    return this;
  }

  /**
   * Get digitalDocumentDownload
   * @return digitalDocumentDownload
  */
  @Valid 
  @JsonProperty("digitalDocumentDownload")
  public DigitalDocumentDownloadResponse getDigitalDocumentDownload() {
    return digitalDocumentDownload;
  }

  public void setDigitalDocumentDownload(DigitalDocumentDownloadResponse digitalDocumentDownload) {
    this.digitalDocumentDownload = digitalDocumentDownload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentResponse digitalDocumentResponse = (DigitalDocumentResponse) o;
    return Objects.equals(this.id, digitalDocumentResponse.id) &&
        Objects.equals(this.title, digitalDocumentResponse.title) &&
        Objects.equals(this.content, digitalDocumentResponse.content) &&
        Objects.equals(this.stockReferenceDate, digitalDocumentResponse.stockReferenceDate) &&
        Objects.equals(this.companyName, digitalDocumentResponse.companyName) &&
        Objects.equals(this.companyRegistrationNumber, digitalDocumentResponse.companyRegistrationNumber) &&
        Objects.equals(this.shareholderMeetingType, digitalDocumentResponse.shareholderMeetingType) &&
        Objects.equals(this.shareholderMeetingName, digitalDocumentResponse.shareholderMeetingName) &&
        Objects.equals(this.shareholderMeetingDate, digitalDocumentResponse.shareholderMeetingDate) &&
        Objects.equals(this.designatedAgentNames, digitalDocumentResponse.designatedAgentNames) &&
        Objects.equals(this.acceptUserId, digitalDocumentResponse.acceptUserId) &&
        Objects.equals(this.type, digitalDocumentResponse.type) &&
        Objects.equals(this.version, digitalDocumentResponse.version) &&
        Objects.equals(this.targetStartDate, digitalDocumentResponse.targetStartDate) &&
        Objects.equals(this.targetEndDate, digitalDocumentResponse.targetEndDate) &&
        Objects.equals(this.createdAt, digitalDocumentResponse.createdAt) &&
        Objects.equals(this.updatedAt, digitalDocumentResponse.updatedAt) &&
        Objects.equals(this.items, digitalDocumentResponse.items) &&
        Objects.equals(this.attachOptions, digitalDocumentResponse.attachOptions) &&
        Objects.equals(this.digitalDocumentDownload, digitalDocumentResponse.digitalDocumentDownload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, stockReferenceDate, companyName, companyRegistrationNumber, shareholderMeetingType, shareholderMeetingName, shareholderMeetingDate, designatedAgentNames, acceptUserId, type, version, targetStartDate, targetEndDate, createdAt, updatedAt, items, attachOptions, digitalDocumentDownload);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    stockReferenceDate: ").append(toIndentedString(stockReferenceDate)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    companyRegistrationNumber: ").append(toIndentedString(companyRegistrationNumber)).append("\n");
    sb.append("    shareholderMeetingType: ").append(toIndentedString(shareholderMeetingType)).append("\n");
    sb.append("    shareholderMeetingName: ").append(toIndentedString(shareholderMeetingName)).append("\n");
    sb.append("    shareholderMeetingDate: ").append(toIndentedString(shareholderMeetingDate)).append("\n");
    sb.append("    designatedAgentNames: ").append(toIndentedString(designatedAgentNames)).append("\n");
    sb.append("    acceptUserId: ").append(toIndentedString(acceptUserId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    attachOptions: ").append(toIndentedString(attachOptions)).append("\n");
    sb.append("    digitalDocumentDownload: ").append(toIndentedString(digitalDocumentDownload)).append("\n");
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

