package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentItemRequest;
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
 * PreviewDigitalDocumentRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PreviewDigitalDocumentRequest implements ag.act.dto.IDigitalDocumentFillRequest {

  @NotBlank(message = "문서 구분을 확인해주세요.")

  private String type;

  @NotBlank(message = "회사명을 확인해주세요.")

  private String companyName;

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

  private Long acceptUserId;

  private Boolean isDisplayStockQuantity = false;

  public PreviewDigitalDocumentRequest type(String type) {
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

  public PreviewDigitalDocumentRequest companyName(String companyName) {
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

  public PreviewDigitalDocumentRequest shareholderMeetingType(String shareholderMeetingType) {
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

  public PreviewDigitalDocumentRequest shareholderMeetingName(String shareholderMeetingName) {
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

  public PreviewDigitalDocumentRequest shareholderMeetingDate(java.time.Instant shareholderMeetingDate) {
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

  public PreviewDigitalDocumentRequest designatedAgentNames(String designatedAgentNames) {
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

  public PreviewDigitalDocumentRequest childItems(List<@Valid DigitalDocumentItemRequest> childItems) {
    this.childItems = childItems;
    return this;
  }

  public PreviewDigitalDocumentRequest addChildItemsItem(DigitalDocumentItemRequest childItemsItem) {
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

  public PreviewDigitalDocumentRequest companyRegistrationNumber(String companyRegistrationNumber) {
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

  public PreviewDigitalDocumentRequest title(String title) {
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

  public PreviewDigitalDocumentRequest content(String content) {
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

  public PreviewDigitalDocumentRequest version(String version) {
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

  public PreviewDigitalDocumentRequest acceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
    return this;
  }

  /**
   * DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT 인 경우 수임인 아이디 필수
   * @return acceptUserId
  */
  
  @JsonProperty("acceptUserId")
  public Long getAcceptUserId() {
    return acceptUserId;
  }

  public void setAcceptUserId(Long acceptUserId) {
    this.acceptUserId = acceptUserId;
  }

  public PreviewDigitalDocumentRequest isDisplayStockQuantity(Boolean isDisplayStockQuantity) {
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
    PreviewDigitalDocumentRequest previewDigitalDocumentRequest = (PreviewDigitalDocumentRequest) o;
    return Objects.equals(this.type, previewDigitalDocumentRequest.type) &&
        Objects.equals(this.companyName, previewDigitalDocumentRequest.companyName) &&
        Objects.equals(this.shareholderMeetingType, previewDigitalDocumentRequest.shareholderMeetingType) &&
        Objects.equals(this.shareholderMeetingName, previewDigitalDocumentRequest.shareholderMeetingName) &&
        Objects.equals(this.shareholderMeetingDate, previewDigitalDocumentRequest.shareholderMeetingDate) &&
        Objects.equals(this.designatedAgentNames, previewDigitalDocumentRequest.designatedAgentNames) &&
        Objects.equals(this.childItems, previewDigitalDocumentRequest.childItems) &&
        Objects.equals(this.companyRegistrationNumber, previewDigitalDocumentRequest.companyRegistrationNumber) &&
        Objects.equals(this.title, previewDigitalDocumentRequest.title) &&
        Objects.equals(this.content, previewDigitalDocumentRequest.content) &&
        Objects.equals(this.version, previewDigitalDocumentRequest.version) &&
        Objects.equals(this.acceptUserId, previewDigitalDocumentRequest.acceptUserId) &&
        Objects.equals(this.isDisplayStockQuantity, previewDigitalDocumentRequest.isDisplayStockQuantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, companyName, shareholderMeetingType, shareholderMeetingName, shareholderMeetingDate, designatedAgentNames, childItems, companyRegistrationNumber, title, content, version, acceptUserId, isDisplayStockQuantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreviewDigitalDocumentRequest {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    shareholderMeetingType: ").append(toIndentedString(shareholderMeetingType)).append("\n");
    sb.append("    shareholderMeetingName: ").append(toIndentedString(shareholderMeetingName)).append("\n");
    sb.append("    shareholderMeetingDate: ").append(toIndentedString(shareholderMeetingDate)).append("\n");
    sb.append("    designatedAgentNames: ").append(toIndentedString(designatedAgentNames)).append("\n");
    sb.append("    childItems: ").append(toIndentedString(childItems)).append("\n");
    sb.append("    companyRegistrationNumber: ").append(toIndentedString(companyRegistrationNumber)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    acceptUserId: ").append(toIndentedString(acceptUserId)).append("\n");
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

