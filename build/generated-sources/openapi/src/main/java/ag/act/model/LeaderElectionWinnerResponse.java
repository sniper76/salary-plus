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
 * LeaderElectionWinnerResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class LeaderElectionWinnerResponse {

  private Boolean isElected;

  private String nickname;

  private String profileImageUrl;

  private Boolean isElectedByAdmin;

  public LeaderElectionWinnerResponse isElected(Boolean isElected) {
    this.isElected = isElected;
    return this;
  }

  /**
   * Get isElected
   * @return isElected
  */
  
  @JsonProperty("isElected")
  public Boolean getIsElected() {
    return isElected;
  }

  public void setIsElected(Boolean isElected) {
    this.isElected = isElected;
  }

  public LeaderElectionWinnerResponse nickname(String nickname) {
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

  public LeaderElectionWinnerResponse profileImageUrl(String profileImageUrl) {
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

  public LeaderElectionWinnerResponse isElectedByAdmin(Boolean isElectedByAdmin) {
    this.isElectedByAdmin = isElectedByAdmin;
    return this;
  }

  /**
   * 관리자에 의해 선출된 주주대표인 경우 true
   * @return isElectedByAdmin
  */
  
  @JsonProperty("isElectedByAdmin")
  public Boolean getIsElectedByAdmin() {
    return isElectedByAdmin;
  }

  public void setIsElectedByAdmin(Boolean isElectedByAdmin) {
    this.isElectedByAdmin = isElectedByAdmin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderElectionWinnerResponse leaderElectionWinnerResponse = (LeaderElectionWinnerResponse) o;
    return Objects.equals(this.isElected, leaderElectionWinnerResponse.isElected) &&
        Objects.equals(this.nickname, leaderElectionWinnerResponse.nickname) &&
        Objects.equals(this.profileImageUrl, leaderElectionWinnerResponse.profileImageUrl) &&
        Objects.equals(this.isElectedByAdmin, leaderElectionWinnerResponse.isElectedByAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isElected, nickname, profileImageUrl, isElectedByAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderElectionWinnerResponse {\n");
    sb.append("    isElected: ").append(toIndentedString(isElected)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    isElectedByAdmin: ").append(toIndentedString(isElectedByAdmin)).append("\n");
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

