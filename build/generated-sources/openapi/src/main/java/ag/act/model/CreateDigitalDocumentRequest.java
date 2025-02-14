package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentItemRequest;
import ag.act.model.HolderListReadAndCopyItemRequest;
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
 * CreateDigitalDocumentRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateDigitalDocumentRequest implements ag.act.dto.IDigitalDocumentFillRequest {

  @NotBlank(message = "문서 구분을 확인해주세요.")

  private String type;

  @NotBlank(message = "회사명을 확인해주세요.")

  private String companyName;

  private Long acceptUserId;

  private Long stockReferenceDateId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private String shareholderMeetingType;

  private String shareholderMeetingName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant shareholderMeetingDate;

  private String designatedAgentNames;

  @Valid
  private List<@Valid DigitalDocumentItemRequest> childItems;

  private String companyRegistrationNumber;

  private String title;

  private String content;

  private String version = "V1";

  private String idCardWatermarkType = "ACT_LOGO";

  private JsonAttachOption attachOptions;

  @Valid
  private List<@Valid HolderListReadAndCopyItemRequest> holderListReadAndCopyItems;

  private Boolean isDisplayStockQuantity = false;

  public CreateDigitalDocumentRequest type(String type) {
    this.type = type;
    return this;
  }

  /**
   * 전자 문서 구분 DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public CreateDigitalDocumentRequest companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * 회사명
   * @return companyName
  */
  
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CreateDigitalDocumentRequest acceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
    return this;
  }

  /**
   * 수임인 아이디
   * @return acceptUserId
  */
  
  @JsonProperty("acceptUserId")
  public Long getAcceptUserId() {
    return acceptUserId;
  }

  public void setAcceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
  }

  public CreateDigitalDocumentRequest stockReferenceDateId(Long stockReferenceDateId) {
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

  public CreateDigitalDocumentRequest targetStartDate(java.time.Instant targetStartDate) {
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

  public CreateDigitalDocumentRequest targetEndDate(java.time.Instant targetEndDate) {
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

  public CreateDigitalDocumentRequest shareholderMeetingType(String shareholderMeetingType) {
    this.shareholderMeetingType = shareholderMeetingType;
    return this;
  }

  /**
   * 주총구분 REGULAR_GENERAL_MEETING_STOCKHOLDERS or EXTRAORDINARY_GENERAL_MEETING_STOCKHOLDERS
   * @return shareholderMeetingType
  */
  
  @JsonProperty("shareholderMeetingType")
  public String getShareholderMeetingType() {
    return shareholderMeetingType;
  }

  public void setShareholderMeetingType(String shareholderMeetingType) {
    this.shareholderMeetingType = shareholderMeetingType;
  }

  public CreateDigitalDocumentRequest shareholderMeetingName(String shareholderMeetingName) {
    this.shareholderMeetingName = shareholderMeetingName;
    return this;
  }

  /**
   * 주총명
   * @return shareholderMeetingName
  */
  
  @JsonProperty("shareholderMeetingName")
  public String getShareholderMeetingName() {
    return shareholderMeetingName;
  }

  public void setShareholderMeetingName(String shareholderMeetingName) {
    this.shareholderMeetingName = shareholderMeetingName;
  }

  public CreateDigitalDocumentRequest shareholderMeetingDate(java.time.Instant shareholderMeetingDate) {
    this.shareholderMeetingDate = shareholderMeetingDate;
    return this;
  }

  /**
   * 주춍일
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

  public CreateDigitalDocumentRequest designatedAgentNames(String designatedAgentNames) {
    this.designatedAgentNames = designatedAgentNames;
    return this;
  }

  /**
   * 수임인지정대리인
   * @return designatedAgentNames
  */
  
  @JsonProperty("designatedAgentNames")
  public String getDesignatedAgentNames() {
    return designatedAgentNames;
  }

  public void setDesignatedAgentNames(String designatedAgentNames) {
    this.designatedAgentNames = designatedAgentNames;
  }

  public CreateDigitalDocumentRequest childItems(List<@Valid DigitalDocumentItemRequest> childItems) {
    this.childItems = childItems;
    return this;
  }

  public CreateDigitalDocumentRequest addChildItemsItem(DigitalDocumentItemRequest childItemsItem) {
    if (this.childItems == null) {
      this.childItems = new ArrayList<>();
    }
    this.childItems.add(childItemsItem);
    return this;
  }

  /**
   * Get childItems
   * @return childItems
  */
  @Valid 
  @JsonProperty("childItems")
  public List<@Valid DigitalDocumentItemRequest> getChildItems() {
    return childItems;
  }

  public void setChildItems(List<@Valid DigitalDocumentItemRequest> childItems) {
    this.childItems = childItems;
  }

  public CreateDigitalDocumentRequest companyRegistrationNumber(String companyRegistrationNumber) {
    this.companyRegistrationNumber = companyRegistrationNumber;
    return this;
  }

  /**
   * 법인등록번호
   * @return companyRegistrationNumber
  */
  
  @JsonProperty("companyRegistrationNumber")
  public String getCompanyRegistrationNumber() {
    return companyRegistrationNumber;
  }

  public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
    this.companyRegistrationNumber = companyRegistrationNumber;
  }

  public CreateDigitalDocumentRequest title(String title) {
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

  public CreateDigitalDocumentRequest content(String content) {
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

  public CreateDigitalDocumentRequest version(String version) {
    this.version = version;
    return this;
  }

  /**
   * 전자문서 버전
   * @return version
  */
  
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public CreateDigitalDocumentRequest idCardWatermarkType(String idCardWatermarkType) {
    this.idCardWatermarkType = idCardWatermarkType;
    return this;
  }

  /**
   * NONE or ACT_LOGO or ACT_LOGO_WITH_DATE
   * @return idCardWatermarkType
  */
  
  @JsonProperty("idCardWatermarkType")
  public String getIdCardWatermarkType() {
    return idCardWatermarkType;
  }

  public void setIdCardWatermarkType(String idCardWatermarkType) {
    this.idCardWatermarkType = idCardWatermarkType;
  }

  public CreateDigitalDocumentRequest attachOptions(JsonAttachOption attachOptions) {
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

  public CreateDigitalDocumentRequest holderListReadAndCopyItems(List<@Valid HolderListReadAndCopyItemRequest> holderListReadAndCopyItems) {
    this.holderListReadAndCopyItems = holderListReadAndCopyItems;
    return this;
  }

  public CreateDigitalDocumentRequest addHolderListReadAndCopyItemsItem(HolderListReadAndCopyItemRequest holderListReadAndCopyItemsItem) {
    if (this.holderListReadAndCopyItems == null) {
      this.holderListReadAndCopyItems = new ArrayList<>();
    }
    this.holderListReadAndCopyItems.add(holderListReadAndCopyItemsItem);
    return this;
  }

  /**
   * Get holderListReadAndCopyItems
   * @return holderListReadAndCopyItems
  */
  @Valid 
  @JsonProperty("holderListReadAndCopyItems")
  public List<@Valid HolderListReadAndCopyItemRequest> getHolderListReadAndCopyItems() {
    return holderListReadAndCopyItems;
  }

  public void setHolderListReadAndCopyItems(List<@Valid HolderListReadAndCopyItemRequest> holderListReadAndCopyItems) {
    this.holderListReadAndCopyItems = holderListReadAndCopyItems;
  }

  public CreateDigitalDocumentRequest isDisplayStockQuantity(Boolean isDisplayStockQuantity) {
    this.isDisplayStockQuantity = isDisplayStockQuantity;
    return this;
  }

  /**
   * 보유주식수 표시 여부
   * @return isDisplayStockQuantity
  */
  
  @JsonProperty("isDisplayStockQuantity")
  public Boolean getIsDisplayStockQuantity() {
    return isDisplayStockQuantity;
  }

  public void setIsDisplayStockQuantity(Boolean isDisplayStockQuantity) {
    this.isDisplayStockQuantity = isDisplayStockQuantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateDigitalDocumentRequest createDigitalDocumentRequest = (CreateDigitalDocumentRequest) o;
    return Objects.equals(this.type, createDigitalDocumentRequest.type) &&
        Objects.equals(this.companyName, createDigitalDocumentRequest.companyName) &&
        Objects.equals(this.acceptUserId, createDigitalDocumentRequest.acceptUserId) &&
        Objects.equals(this.stockReferenceDateId, createDigitalDocumentRequest.stockReferenceDateId) &&
        Objects.equals(this.targetStartDate, createDigitalDocumentRequest.targetStartDate) &&
        Objects.equals(this.targetEndDate, createDigitalDocumentRequest.targetEndDate) &&
        Objects.equals(this.shareholderMeetingType, createDigitalDocumentRequest.shareholderMeetingType) &&
        Objects.equals(this.shareholderMeetingName, createDigitalDocumentRequest.shareholderMeetingName) &&
        Objects.equals(this.shareholderMeetingDate, createDigitalDocumentRequest.shareholderMeetingDate) &&
        Objects.equals(this.designatedAgentNames, createDigitalDocumentRequest.designatedAgentNames) &&
        Objects.equals(this.childItems, createDigitalDocumentRequest.childItems) &&
        Objects.equals(this.companyRegistrationNumber, createDigitalDocumentRequest.companyRegistrationNumber) &&
        Objects.equals(this.title, createDigitalDocumentRequest.title) &&
        Objects.equals(this.content, createDigitalDocumentRequest.content) &&
        Objects.equals(this.version, createDigitalDocumentRequest.version) &&
        Objects.equals(this.idCardWatermarkType, createDigitalDocumentRequest.idCardWatermarkType) &&
        Objects.equals(this.attachOptions, createDigitalDocumentRequest.attachOptions) &&
        Objects.equals(this.holderListReadAndCopyItems, createDigitalDocumentRequest.holderListReadAndCopyItems) &&
        Objects.equals(this.isDisplayStockQuantity, createDigitalDocumentRequest.isDisplayStockQuantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, companyName, acceptUserId, stockReferenceDateId, targetStartDate, targetEndDate, shareholderMeetingType, shareholderMeetingName, shareholderMeetingDate, designatedAgentNames, childItems, companyRegistrationNumber, title, content, version, idCardWatermarkType, attachOptions, holderListReadAndCopyItems, isDisplayStockQuantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateDigitalDocumentRequest {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    acceptUserId: ").append(toIndentedString(acceptUserId)).append("\n");
    sb.append("    stockReferenceDateId: ").append(toIndentedString(stockReferenceDateId)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    shareholderMeetingType: ").append(toIndentedString(shareholderMeetingType)).append("\n");
    sb.append("    shareholderMeetingName: ").append(toIndentedString(shareholderMeetingName)).append("\n");
    sb.append("    shareholderMeetingDate: ").append(toIndentedString(shareholderMeetingDate)).append("\n");
    sb.append("    designatedAgentNames: ").append(toIndentedString(designatedAgentNames)).append("\n");
    sb.append("    childItems: ").append(toIndentedString(childItems)).append("\n");
    sb.append("    companyRegistrationNumber: ").append(toIndentedString(companyRegistrationNumber)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    idCardWatermarkType: ").append(toIndentedString(idCardWatermarkType)).append("\n");
    sb.append("    attachOptions: ").append(toIndentedString(attachOptions)).append("\n");
    sb.append("    holderListReadAndCopyItems: ").append(toIndentedString(holderListReadAndCopyItems)).append("\n");
    sb.append("    isDisplayStockQuantity: ").append(toIndentedString(isDisplayStockQuantity)).append("\n");
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

