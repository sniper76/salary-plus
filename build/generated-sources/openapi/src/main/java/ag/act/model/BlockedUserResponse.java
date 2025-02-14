package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * BlockedUserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class BlockedUserResponse {

  private Long id;

  private Long blockedUserId;

  private String nickname;

  private String profileImageUrl;

  private Boolean isSolidarityLeader;

  @Valid
  private List<String> leadingSolidarityStockNames;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  public BlockedUserResponse id(Long id) {
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

  public BlockedUserResponse blockedUserId(Long blockedUserId) {
    this.blockedUserId = blockedUserId;
    return this;
  }

  /**
   * Get blockedUserId
   * @return blockedUserId
  */
  
  @JsonProperty("blockedUserId")
  public Long getBlockedUserId() {
    return blockedUserId;
  }

  public void setBlockedUserId(Long blockedUserId) {
    this.blockedUserId = blockedUserId;
  }

  public BlockedUserResponse nickname(String nickname) {
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

  public BlockedUserResponse profileImageUrl(String profileImageUrl) {
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

  public BlockedUserResponse isSolidarityLeader(Boolean isSolidarityLeader) {
    this.isSolidarityLeader = isSolidarityLeader;
    return this;
  }

  /**
   * Get isSolidarityLeader
   * @return isSolidarityLeader
  */
  
  @JsonProperty("isSolidarityLeader")
  public Boolean getIsSolidarityLeader() {
    return isSolidarityLeader;
  }

  public void setIsSolidarityLeader(Boolean isSolidarityLeader) {
    this.isSolidarityLeader = isSolidarityLeader;
  }

  public BlockedUserResponse leadingSolidarityStockNames(List<String> leadingSolidarityStockNames) {
    this.leadingSolidarityStockNames = leadingSolidarityStockNames;
    return this;
  }

  public BlockedUserResponse addLeadingSolidarityStockNamesItem(String leadingSolidarityStockNamesItem) {
    if (this.leadingSolidarityStockNames == null) {
      this.leadingSolidarityStockNames = new ArrayList<>();
    }
    this.leadingSolidarityStockNames.add(leadingSolidarityStockNamesItem);
    return this;
  }

  /**
   * Get leadingSolidarityStockNames
   * @return leadingSolidarityStockNames
  */
  
  @JsonProperty("leadingSolidarityStockNames")
  public List<String> getLeadingSolidarityStockNames() {
    return leadingSolidarityStockNames;
  }

  public void setLeadingSolidarityStockNames(List<String> leadingSolidarityStockNames) {
    this.leadingSolidarityStockNames = leadingSolidarityStockNames;
  }

  public BlockedUserResponse createdAt(java.time.Instant createdAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockedUserResponse blockedUserResponse = (BlockedUserResponse) o;
    return Objects.equals(this.id, blockedUserResponse.id) &&
        Objects.equals(this.blockedUserId, blockedUserResponse.blockedUserId) &&
        Objects.equals(this.nickname, blockedUserResponse.nickname) &&
        Objects.equals(this.profileImageUrl, blockedUserResponse.profileImageUrl) &&
        Objects.equals(this.isSolidarityLeader, blockedUserResponse.isSolidarityLeader) &&
        Objects.equals(this.leadingSolidarityStockNames, blockedUserResponse.leadingSolidarityStockNames) &&
        Objects.equals(this.createdAt, blockedUserResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, blockedUserId, nickname, profileImageUrl, isSolidarityLeader, leadingSolidarityStockNames, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockedUserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    blockedUserId: ").append(toIndentedString(blockedUserId)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    isSolidarityLeader: ").append(toIndentedString(isSolidarityLeader)).append("\n");
    sb.append("    leadingSolidarityStockNames: ").append(toIndentedString(leadingSolidarityStockNames)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

