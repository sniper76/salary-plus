package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * SolidarityLeaderApplicantResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderApplicantResponse {

  private Long id;

  private String name;

  private String phoneNumber = null;

  private String nickname = null;

  private String profileImageUrl = null;

  private String individualStockCountLabel;

  private String commentsForStockHolder;

  private Long solidarityApplicantId;

  public SolidarityLeaderApplicantResponse id(Long id) {
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

  public SolidarityLeaderApplicantResponse name(String name) {
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

  public SolidarityLeaderApplicantResponse phoneNumber(String phoneNumber) {
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

  public SolidarityLeaderApplicantResponse nickname(String nickname) {
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

  public SolidarityLeaderApplicantResponse profileImageUrl(String profileImageUrl) {
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

  public SolidarityLeaderApplicantResponse individualStockCountLabel(String individualStockCountLabel) {
    this.individualStockCountLabel = individualStockCountLabel;
    return this;
  }

  /**
   * Get individualStockCountLabel
   * @return individualStockCountLabel
  */
  
  @JsonProperty("individualStockCountLabel")
  public String getIndividualStockCountLabel() {
    return individualStockCountLabel;
  }

  public void setIndividualStockCountLabel(String individualStockCountLabel) {
    this.individualStockCountLabel = individualStockCountLabel;
  }

  public SolidarityLeaderApplicantResponse commentsForStockHolder(String commentsForStockHolder) {
    this.commentsForStockHolder = commentsForStockHolder;
    return this;
  }

  /**
   * Get commentsForStockHolder
   * @return commentsForStockHolder
  */
  
  @JsonProperty("commentsForStockHolder")
  public String getCommentsForStockHolder() {
    return commentsForStockHolder;
  }

  public void setCommentsForStockHolder(String commentsForStockHolder) {
    this.commentsForStockHolder = commentsForStockHolder;
  }

  public SolidarityLeaderApplicantResponse solidarityApplicantId(Long solidarityApplicantId) {
    this.solidarityApplicantId = solidarityApplicantId;
    return this;
  }

  /**
   * Get solidarityApplicantId
   * @return solidarityApplicantId
  */
  
  @JsonProperty("solidarityApplicantId")
  public Long getSolidarityApplicantId() {
    return solidarityApplicantId;
  }

  public void setSolidarityApplicantId(Long solidarityApplicantId) {
    this.solidarityApplicantId = solidarityApplicantId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderApplicantResponse solidarityLeaderApplicantResponse = (SolidarityLeaderApplicantResponse) o;
    return Objects.equals(this.id, solidarityLeaderApplicantResponse.id) &&
        Objects.equals(this.name, solidarityLeaderApplicantResponse.name) &&
        Objects.equals(this.phoneNumber, solidarityLeaderApplicantResponse.phoneNumber) &&
        Objects.equals(this.nickname, solidarityLeaderApplicantResponse.nickname) &&
        Objects.equals(this.profileImageUrl, solidarityLeaderApplicantResponse.profileImageUrl) &&
        Objects.equals(this.individualStockCountLabel, solidarityLeaderApplicantResponse.individualStockCountLabel) &&
        Objects.equals(this.commentsForStockHolder, solidarityLeaderApplicantResponse.commentsForStockHolder) &&
        Objects.equals(this.solidarityApplicantId, solidarityLeaderApplicantResponse.solidarityApplicantId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, phoneNumber, nickname, profileImageUrl, individualStockCountLabel, commentsForStockHolder, solidarityApplicantId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderApplicantResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    individualStockCountLabel: ").append(toIndentedString(individualStockCountLabel)).append("\n");
    sb.append("    commentsForStockHolder: ").append(toIndentedString(commentsForStockHolder)).append("\n");
    sb.append("    solidarityApplicantId: ").append(toIndentedString(solidarityApplicantId)).append("\n");
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

