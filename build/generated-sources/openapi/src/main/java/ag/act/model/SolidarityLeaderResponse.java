package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.Gender;
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
 * SolidarityLeaderResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderResponse {

  private Long id;

  private String email;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant birthDate;

  private String phoneNumber = null;

  private Gender gender;

  private String nickname = null;

  private Status status;

  private String profileImageUrl = null;

  private String message = null;

  private Long solidarityId;

  private Long solidarityLeaderId;

  private Long userId;

  private String corporateNo;

  public SolidarityLeaderResponse id(Long id) {
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

  public SolidarityLeaderResponse email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public SolidarityLeaderResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SolidarityLeaderResponse birthDate(java.time.Instant birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * Get birthDate
   * @return birthDate
  */
  @Valid 
  @JsonProperty("birthDate")
  public java.time.Instant getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(java.time.Instant birthDate) {
    this.birthDate = birthDate;
  }

  public SolidarityLeaderResponse phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
  */
  
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public SolidarityLeaderResponse gender(Gender gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
  */
  @Valid 
  @JsonProperty("gender")
  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public SolidarityLeaderResponse nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  /**
   * Get nickname
   * @return nickname
  */
  
  @JsonProperty("nickname")
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public SolidarityLeaderResponse status(Status status) {
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

  public SolidarityLeaderResponse profileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  /**
   * Get profileImageUrl
   * @return profileImageUrl
  */
  
  @JsonProperty("profileImageUrl")
  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public SolidarityLeaderResponse message(String message) {
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

  public SolidarityLeaderResponse solidarityId(Long solidarityId) {
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

  public SolidarityLeaderResponse solidarityLeaderId(Long solidarityLeaderId) {
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

  public SolidarityLeaderResponse userId(Long userId) {
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

  public SolidarityLeaderResponse corporateNo(String corporateNo) {
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
    SolidarityLeaderResponse solidarityLeaderResponse = (SolidarityLeaderResponse) o;
    return Objects.equals(this.id, solidarityLeaderResponse.id) &&
        Objects.equals(this.email, solidarityLeaderResponse.email) &&
        Objects.equals(this.name, solidarityLeaderResponse.name) &&
        Objects.equals(this.birthDate, solidarityLeaderResponse.birthDate) &&
        Objects.equals(this.phoneNumber, solidarityLeaderResponse.phoneNumber) &&
        Objects.equals(this.gender, solidarityLeaderResponse.gender) &&
        Objects.equals(this.nickname, solidarityLeaderResponse.nickname) &&
        Objects.equals(this.status, solidarityLeaderResponse.status) &&
        Objects.equals(this.profileImageUrl, solidarityLeaderResponse.profileImageUrl) &&
        Objects.equals(this.message, solidarityLeaderResponse.message) &&
        Objects.equals(this.solidarityId, solidarityLeaderResponse.solidarityId) &&
        Objects.equals(this.solidarityLeaderId, solidarityLeaderResponse.solidarityLeaderId) &&
        Objects.equals(this.userId, solidarityLeaderResponse.userId) &&
        Objects.equals(this.corporateNo, solidarityLeaderResponse.corporateNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, birthDate, phoneNumber, gender, nickname, status, profileImageUrl, message, solidarityId, solidarityLeaderId, userId, corporateNo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
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

