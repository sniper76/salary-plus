package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * SolidarityResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityResponse {

  private Status status;

  private Long id;

  private String name;

  private String code;

  private Integer memberCount = null;

  private Float stake = null;

  private Integer requiredMemberCount = null;

  private Integer minThresholdMemberCount = null;

  private String representativePhoneNumber;

  public SolidarityResponse status(Status status) {
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

  public SolidarityResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Id of Solidarity
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SolidarityResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the Stock
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SolidarityResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Code of the solidarity
   * @return code
  */
  
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public SolidarityResponse memberCount(Integer memberCount) {
    this.memberCount = memberCount;
    return this;
  }

  /**
   * Member count of the solidarity
   * @return memberCount
  */
  
  @JsonProperty("memberCount")
  public Integer getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Integer memberCount) {
    this.memberCount = memberCount;
  }

  public SolidarityResponse stake(Float stake) {
    this.stake = stake;
    return this;
  }

  /**
   * Stake of the solidarity
   * @return stake
  */
  
  @JsonProperty("stake")
  public Float getStake() {
    return stake;
  }

  public void setStake(Float stake) {
    this.stake = stake;
  }

  public SolidarityResponse requiredMemberCount(Integer requiredMemberCount) {
    this.requiredMemberCount = requiredMemberCount;
    return this;
  }

  /**
   * Required member count for the solidarity
   * @return requiredMemberCount
  */
  
  @JsonProperty("requiredMemberCount")
  public Integer getRequiredMemberCount() {
    return requiredMemberCount;
  }

  public void setRequiredMemberCount(Integer requiredMemberCount) {
    this.requiredMemberCount = requiredMemberCount;
  }

  public SolidarityResponse minThresholdMemberCount(Integer minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
    return this;
  }

  /**
   * Get minThresholdMemberCount
   * @return minThresholdMemberCount
  */
  
  @JsonProperty("minThresholdMemberCount")
  public Integer getMinThresholdMemberCount() {
    return minThresholdMemberCount;
  }

  public void setMinThresholdMemberCount(Integer minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
  }

  public SolidarityResponse representativePhoneNumber(String representativePhoneNumber) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityResponse solidarityResponse = (SolidarityResponse) o;
    return Objects.equals(this.status, solidarityResponse.status) &&
        Objects.equals(this.id, solidarityResponse.id) &&
        Objects.equals(this.name, solidarityResponse.name) &&
        Objects.equals(this.code, solidarityResponse.code) &&
        Objects.equals(this.memberCount, solidarityResponse.memberCount) &&
        Objects.equals(this.stake, solidarityResponse.stake) &&
        Objects.equals(this.requiredMemberCount, solidarityResponse.requiredMemberCount) &&
        Objects.equals(this.minThresholdMemberCount, solidarityResponse.minThresholdMemberCount) &&
        Objects.equals(this.representativePhoneNumber, solidarityResponse.representativePhoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, id, name, code, memberCount, stake, requiredMemberCount, minThresholdMemberCount, representativePhoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    memberCount: ").append(toIndentedString(memberCount)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    requiredMemberCount: ").append(toIndentedString(requiredMemberCount)).append("\n");
    sb.append("    minThresholdMemberCount: ").append(toIndentedString(minThresholdMemberCount)).append("\n");
    sb.append("    representativePhoneNumber: ").append(toIndentedString(representativePhoneNumber)).append("\n");
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

