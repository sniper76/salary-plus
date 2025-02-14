package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * CorporateUserDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CorporateUserDetailsResponse {

  private Long id;

  private Long userId;

  private String corporateNo;

  private String corporateName;

  private Status status;

  @Valid
  private List<String> leadingSolidarityStockCodes;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  public CorporateUserDetailsResponse id(Long id) {
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

  public CorporateUserDetailsResponse userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public CorporateUserDetailsResponse corporateNo(String corporateNo) {
    this.corporateNo = corporateNo;
    return this;
  }

  /**
   * Get corporateNo
   * @return corporateNo
  */
  
  @JsonProperty("corporateNo")
  public String getCorporateNo() {
    return corporateNo;
  }

  public void setCorporateNo(String corporateNo) {
    this.corporateNo = corporateNo;
  }

  public CorporateUserDetailsResponse corporateName(String corporateName) {
    this.corporateName = corporateName;
    return this;
  }

  /**
   * Get corporateName
   * @return corporateName
  */
  
  @JsonProperty("corporateName")
  public String getCorporateName() {
    return corporateName;
  }

  public void setCorporateName(String corporateName) {
    this.corporateName = corporateName;
  }

  public CorporateUserDetailsResponse status(Status status) {
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

  public CorporateUserDetailsResponse leadingSolidarityStockCodes(List<String> leadingSolidarityStockCodes) {
    this.leadingSolidarityStockCodes = leadingSolidarityStockCodes;
    return this;
  }

  public CorporateUserDetailsResponse addLeadingSolidarityStockCodesItem(String leadingSolidarityStockCodesItem) {
    if (this.leadingSolidarityStockCodes == null) {
      this.leadingSolidarityStockCodes = new ArrayList<>();
    }
    this.leadingSolidarityStockCodes.add(leadingSolidarityStockCodesItem);
    return this;
  }

  /**
   * Get leadingSolidarityStockCodes
   * @return leadingSolidarityStockCodes
  */
  
  @JsonProperty("leadingSolidarityStockCodes")
  public List<String> getLeadingSolidarityStockCodes() {
    return leadingSolidarityStockCodes;
  }

  public void setLeadingSolidarityStockCodes(List<String> leadingSolidarityStockCodes) {
    this.leadingSolidarityStockCodes = leadingSolidarityStockCodes;
  }

  public CorporateUserDetailsResponse createdAt(java.time.Instant createdAt) {
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

  public CorporateUserDetailsResponse updatedAt(java.time.Instant updatedAt) {
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

  public CorporateUserDetailsResponse deletedAt(java.time.Instant deletedAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CorporateUserDetailsResponse corporateUserDetailsResponse = (CorporateUserDetailsResponse) o;
    return Objects.equals(this.id, corporateUserDetailsResponse.id) &&
        Objects.equals(this.userId, corporateUserDetailsResponse.userId) &&
        Objects.equals(this.corporateNo, corporateUserDetailsResponse.corporateNo) &&
        Objects.equals(this.corporateName, corporateUserDetailsResponse.corporateName) &&
        Objects.equals(this.status, corporateUserDetailsResponse.status) &&
        Objects.equals(this.leadingSolidarityStockCodes, corporateUserDetailsResponse.leadingSolidarityStockCodes) &&
        Objects.equals(this.createdAt, corporateUserDetailsResponse.createdAt) &&
        Objects.equals(this.updatedAt, corporateUserDetailsResponse.updatedAt) &&
        Objects.equals(this.deletedAt, corporateUserDetailsResponse.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, corporateNo, corporateName, status, leadingSolidarityStockCodes, createdAt, updatedAt, deletedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorporateUserDetailsResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    corporateNo: ").append(toIndentedString(corporateNo)).append("\n");
    sb.append("    corporateName: ").append(toIndentedString(corporateName)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    leadingSolidarityStockCodes: ").append(toIndentedString(leadingSolidarityStockCodes)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
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

