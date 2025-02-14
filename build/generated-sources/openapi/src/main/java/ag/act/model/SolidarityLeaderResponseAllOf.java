package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * SolidarityLeaderResponseAllOf
 */

@JsonTypeName("SolidarityLeaderResponse_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderResponseAllOf {

  private String message = null;

  private Long solidarityId;

  private Long solidarityLeaderId;

  private Long userId;

  private String corporateNo;

  public SolidarityLeaderResponseAllOf message(String message) {
    this.message = message;
    return this;
  }

  /**
   * 주주연대 대표 한마디
   * @return message
  */
  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public SolidarityLeaderResponseAllOf solidarityId(Long solidarityId) {
    this.solidarityId = solidarityId;
    return this;
  }

  /**
   * 주주연대 아이디
   * @return solidarityId
  */
  
  @JsonProperty("solidarityId")
  public Long getSolidarityId() {
    return solidarityId;
  }

  public void setSolidarityId(Long solidarityId) {
    this.solidarityId = solidarityId;
  }

  public SolidarityLeaderResponseAllOf solidarityLeaderId(Long solidarityLeaderId) {
    this.solidarityLeaderId = solidarityLeaderId;
    return this;
  }

  /**
   * 주주연대 대표 아이디
   * @return solidarityLeaderId
  */
  
  @JsonProperty("solidarityLeaderId")
  public Long getSolidarityLeaderId() {
    return solidarityLeaderId;
  }

  public void setSolidarityLeaderId(Long solidarityLeaderId) {
    this.solidarityLeaderId = solidarityLeaderId;
  }

  public SolidarityLeaderResponseAllOf userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * 주주연대 대표 유저 아이디
   * @return userId
  */
  
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public SolidarityLeaderResponseAllOf corporateNo(String corporateNo) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderResponseAllOf solidarityLeaderResponseAllOf = (SolidarityLeaderResponseAllOf) o;
    return Objects.equals(this.message, solidarityLeaderResponseAllOf.message) &&
        Objects.equals(this.solidarityId, solidarityLeaderResponseAllOf.solidarityId) &&
        Objects.equals(this.solidarityLeaderId, solidarityLeaderResponseAllOf.solidarityLeaderId) &&
        Objects.equals(this.userId, solidarityLeaderResponseAllOf.userId) &&
        Objects.equals(this.corporateNo, solidarityLeaderResponseAllOf.corporateNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, solidarityId, solidarityLeaderId, userId, corporateNo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderResponseAllOf {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    solidarityId: ").append(toIndentedString(solidarityId)).append("\n");
    sb.append("    solidarityLeaderId: ").append(toIndentedString(solidarityLeaderId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    corporateNo: ").append(toIndentedString(corporateNo)).append("\n");
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

