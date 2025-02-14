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
 * SimpleUserProfileResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SimpleUserProfileResponse {

  private String nickname;

  private String profileImageUrl = null;

  private String individualStockCountLabel = null;

  private String totalAssetLabel = null;

  public SimpleUserProfileResponse nickname(String nickname) {
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

  public SimpleUserProfileResponse profileImageUrl(String profileImageUrl) {
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

  public SimpleUserProfileResponse individualStockCountLabel(String individualStockCountLabel) {
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

  public SimpleUserProfileResponse totalAssetLabel(String totalAssetLabel) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleUserProfileResponse simpleUserProfileResponse = (SimpleUserProfileResponse) o;
    return Objects.equals(this.nickname, simpleUserProfileResponse.nickname) &&
        Objects.equals(this.profileImageUrl, simpleUserProfileResponse.profileImageUrl) &&
        Objects.equals(this.individualStockCountLabel, simpleUserProfileResponse.individualStockCountLabel) &&
        Objects.equals(this.totalAssetLabel, simpleUserProfileResponse.totalAssetLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nickname, profileImageUrl, individualStockCountLabel, totalAssetLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimpleUserProfileResponse {\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    individualStockCountLabel: ").append(toIndentedString(individualStockCountLabel)).append("\n");
    sb.append("    totalAssetLabel: ").append(toIndentedString(totalAssetLabel)).append("\n");
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

