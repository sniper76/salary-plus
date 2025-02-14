package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * StockResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockResponse {

  private String code;

  private String name;

  private Long totalIssuedQuantity;

  private String representativePhoneNumber;

  private Integer memberCount = null;

  private Float stake = null;

  private Status status;

  private Boolean isPrivate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  public StockResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Code of the stock
   * @return code
  */
  
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public StockResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the stock
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StockResponse totalIssuedQuantity(Long totalIssuedQuantity) {
    this.totalIssuedQuantity = totalIssuedQuantity;
    return this;
  }

  /**
   * Total issued quantity of the stock
   * @return totalIssuedQuantity
  */
  
  @JsonProperty("totalIssuedQuantity")
  public Long getTotalIssuedQuantity() {
    return totalIssuedQuantity;
  }

  public void setTotalIssuedQuantity(Long totalIssuedQuantity) {
    this.totalIssuedQuantity = totalIssuedQuantity;
  }

  public StockResponse representativePhoneNumber(String representativePhoneNumber) {
    this.representativePhoneNumber = representativePhoneNumber;
    return this;
  }

  /**
   * 종목 담당자 전화번호
   * @return representativePhoneNumber
  */
  
  @JsonProperty("representativePhoneNumber")
  public String getRepresentativePhoneNumber() {
    return representativePhoneNumber;
  }

  public void setRepresentativePhoneNumber(String representativePhoneNumber) {
    this.representativePhoneNumber = representativePhoneNumber;
  }

  public StockResponse memberCount(Integer memberCount) {
    this.memberCount = memberCount;
    return this;
  }

  /**
   * Member count of the stock
   * @return memberCount
  */
  
  @JsonProperty("memberCount")
  public Integer getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Integer memberCount) {
    this.memberCount = memberCount;
  }

  public StockResponse stake(Float stake) {
    this.stake = stake;
    return this;
  }

  /**
   * Stake of the stock member
   * @return stake
  */
  
  @JsonProperty("stake")
  public Float getStake() {
    return stake;
  }

  public void setStake(Float stake) {
    this.stake = stake;
  }

  public StockResponse status(Status status) {
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

  public StockResponse isPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
    return this;
  }

  /**
   * Get isPrivate
   * @return isPrivate
  */
  
  @JsonProperty("isPrivate")
  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public StockResponse createdAt(java.time.Instant createdAt) {
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

  public StockResponse updatedAt(java.time.Instant updatedAt) {
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

  public StockResponse deletedAt(java.time.Instant deletedAt) {
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
    StockResponse stockResponse = (StockResponse) o;
    return Objects.equals(this.code, stockResponse.code) &&
        Objects.equals(this.name, stockResponse.name) &&
        Objects.equals(this.totalIssuedQuantity, stockResponse.totalIssuedQuantity) &&
        Objects.equals(this.representativePhoneNumber, stockResponse.representativePhoneNumber) &&
        Objects.equals(this.memberCount, stockResponse.memberCount) &&
        Objects.equals(this.stake, stockResponse.stake) &&
        Objects.equals(this.status, stockResponse.status) &&
        Objects.equals(this.isPrivate, stockResponse.isPrivate) &&
        Objects.equals(this.createdAt, stockResponse.createdAt) &&
        Objects.equals(this.updatedAt, stockResponse.updatedAt) &&
        Objects.equals(this.deletedAt, stockResponse.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, totalIssuedQuantity, representativePhoneNumber, memberCount, stake, status, isPrivate, createdAt, updatedAt, deletedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    totalIssuedQuantity: ").append(toIndentedString(totalIssuedQuantity)).append("\n");
    sb.append("    representativePhoneNumber: ").append(toIndentedString(representativePhoneNumber)).append("\n");
    sb.append("    memberCount: ").append(toIndentedString(memberCount)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    isPrivate: ").append(toIndentedString(isPrivate)).append("\n");
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

