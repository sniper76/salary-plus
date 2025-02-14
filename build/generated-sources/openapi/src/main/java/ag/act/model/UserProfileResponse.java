package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SimpleStockResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UserProfileResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserProfileResponse implements ag.act.entity.ContentUserProfile {

  private String nickname;

  private String profileImageUrl = null;

  private String individualStockCountLabel = null;

  private String totalAssetLabel = null;

  private Boolean deleted = null;

  private Boolean reported = null;

  private Boolean hasStocks = null;

  private String userIp = null;

  private Boolean isSolidarityLeader = null;

  @Valid
  private List<@Valid SimpleStockResponse> leadingStocks;

  public UserProfileResponse nickname(String nickname) {
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

  public UserProfileResponse profileImageUrl(String profileImageUrl) {
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

  public UserProfileResponse individualStockCountLabel(String individualStockCountLabel) {
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

  public UserProfileResponse totalAssetLabel(String totalAssetLabel) {
    this.totalAssetLabel = totalAssetLabel;
    return this;
  }

  /**
   * Get totalAssetLabel
   * @return totalAssetLabel
  */
  
  @JsonProperty("totalAssetLabel")
  public String getTotalAssetLabel() {
    return totalAssetLabel;
  }

  public void setTotalAssetLabel(String totalAssetLabel) {
    this.totalAssetLabel = totalAssetLabel;
  }

  public UserProfileResponse deleted(Boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  /**
   * Get deleted
   * @return deleted
  */
  
  @JsonProperty("deleted")
  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  public UserProfileResponse reported(Boolean reported) {
    this.reported = reported;
    return this;
  }

  /**
   * Get reported
   * @return reported
  */
  
  @JsonProperty("reported")
  public Boolean getReported() {
    return reported;
  }

  public void setReported(Boolean reported) {
    this.reported = reported;
  }

  public UserProfileResponse hasStocks(Boolean hasStocks) {
    this.hasStocks = hasStocks;
    return this;
  }

  /**
   * Get hasStocks
   * @return hasStocks
  */
  
  @JsonProperty("hasStocks")
  public Boolean getHasStocks() {
    return hasStocks;
  }

  public void setHasStocks(Boolean hasStocks) {
    this.hasStocks = hasStocks;
  }

  public UserProfileResponse userIp(String userIp) {
    this.userIp = userIp;
    return this;
  }

  /**
   * Get userIp
   * @return userIp
  */
  
  @JsonProperty("userIp")
  public String getUserIp() {
    return userIp;
  }

  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  public UserProfileResponse isSolidarityLeader(Boolean isSolidarityLeader) {
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

  public UserProfileResponse leadingStocks(List<@Valid SimpleStockResponse> leadingStocks) {
    this.leadingStocks = leadingStocks;
    return this;
  }

  public UserProfileResponse addLeadingStocksItem(SimpleStockResponse leadingStocksItem) {
    if (this.leadingStocks == null) {
      this.leadingStocks = new ArrayList<>();
    }
    this.leadingStocks.add(leadingStocksItem);
    return this;
  }

  /**
   * Get leadingStocks
   * @return leadingStocks
  */
  @Valid 
  @JsonProperty("leadingStocks")
  public List<@Valid SimpleStockResponse> getLeadingStocks() {
    return leadingStocks;
  }

  public void setLeadingStocks(List<@Valid SimpleStockResponse> leadingStocks) {
    this.leadingStocks = leadingStocks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserProfileResponse userProfileResponse = (UserProfileResponse) o;
    return Objects.equals(this.nickname, userProfileResponse.nickname) &&
        Objects.equals(this.profileImageUrl, userProfileResponse.profileImageUrl) &&
        Objects.equals(this.individualStockCountLabel, userProfileResponse.individualStockCountLabel) &&
        Objects.equals(this.totalAssetLabel, userProfileResponse.totalAssetLabel) &&
        Objects.equals(this.deleted, userProfileResponse.deleted) &&
        Objects.equals(this.reported, userProfileResponse.reported) &&
        Objects.equals(this.hasStocks, userProfileResponse.hasStocks) &&
        Objects.equals(this.userIp, userProfileResponse.userIp) &&
        Objects.equals(this.isSolidarityLeader, userProfileResponse.isSolidarityLeader) &&
        Objects.equals(this.leadingStocks, userProfileResponse.leadingStocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nickname, profileImageUrl, individualStockCountLabel, totalAssetLabel, deleted, reported, hasStocks, userIp, isSolidarityLeader, leadingStocks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserProfileResponse {\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    individualStockCountLabel: ").append(toIndentedString(individualStockCountLabel)).append("\n");
    sb.append("    totalAssetLabel: ").append(toIndentedString(totalAssetLabel)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    reported: ").append(toIndentedString(reported)).append("\n");
    sb.append("    hasStocks: ").append(toIndentedString(hasStocks)).append("\n");
    sb.append("    userIp: ").append(toIndentedString(userIp)).append("\n");
    sb.append("    isSolidarityLeader: ").append(toIndentedString(isSolidarityLeader)).append("\n");
    sb.append("    leadingStocks: ").append(toIndentedString(leadingStocks)).append("\n");
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

