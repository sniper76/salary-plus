package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.ActBestPosts;
import ag.act.model.MySolidarityResponse;
import ag.act.model.UnreadPostStatus;
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
 * HomeResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class HomeResponse {

  private Long unreadNotificationsCount;

  private UnreadPostStatus unreadPostStatus;

  @Valid
  private List<@Valid MySolidarityResponse> mySolidarity;

  private ActBestPosts actBestPosts;

  public HomeResponse unreadNotificationsCount(Long unreadNotificationsCount) {
    this.unreadNotificationsCount = unreadNotificationsCount;
    return this;
  }

  /**
   * Get unreadNotificationsCount
   * @return unreadNotificationsCount
  */
  
  @JsonProperty("unreadNotificationsCount")
  public Long getUnreadNotificationsCount() {
    return unreadNotificationsCount;
  }

  public void setUnreadNotificationsCount(Long unreadNotificationsCount) {
    this.unreadNotificationsCount = unreadNotificationsCount;
  }

  public HomeResponse unreadPostStatus(UnreadPostStatus unreadPostStatus) {
    this.unreadPostStatus = unreadPostStatus;
    return this;
  }

  /**
   * Get unreadPostStatus
   * @return unreadPostStatus
  */
  @Valid 
  @JsonProperty("unreadPostStatus")
  public UnreadPostStatus getUnreadPostStatus() {
    return unreadPostStatus;
  }

  public void setUnreadPostStatus(UnreadPostStatus unreadPostStatus) {
    this.unreadPostStatus = unreadPostStatus;
  }

  public HomeResponse mySolidarity(List<@Valid MySolidarityResponse> mySolidarity) {
    this.mySolidarity = mySolidarity;
    return this;
  }

  public HomeResponse addMySolidarityItem(MySolidarityResponse mySolidarityItem) {
    if (this.mySolidarity == null) {
      this.mySolidarity = new ArrayList<>();
    }
    this.mySolidarity.add(mySolidarityItem);
    return this;
  }

  /**
   * Get mySolidarity
   * @return mySolidarity
  */
  @Valid 
  @JsonProperty("mySolidarity")
  public List<@Valid MySolidarityResponse> getMySolidarity() {
    return mySolidarity;
  }

  public void setMySolidarity(List<@Valid MySolidarityResponse> mySolidarity) {
    this.mySolidarity = mySolidarity;
  }

  public HomeResponse actBestPosts(ActBestPosts actBestPosts) {
    this.actBestPosts = actBestPosts;
    return this;
  }

  /**
   * Get actBestPosts
   * @return actBestPosts
  */
  @Valid 
  @JsonProperty("actBestPosts")
  public ActBestPosts getActBestPosts() {
    return actBestPosts;
  }

  public void setActBestPosts(ActBestPosts actBestPosts) {
    this.actBestPosts = actBestPosts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HomeResponse homeResponse = (HomeResponse) o;
    return Objects.equals(this.unreadNotificationsCount, homeResponse.unreadNotificationsCount) &&
        Objects.equals(this.unreadPostStatus, homeResponse.unreadPostStatus) &&
        Objects.equals(this.mySolidarity, homeResponse.mySolidarity) &&
        Objects.equals(this.actBestPosts, homeResponse.actBestPosts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unreadNotificationsCount, unreadPostStatus, mySolidarity, actBestPosts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HomeResponse {\n");
    sb.append("    unreadNotificationsCount: ").append(toIndentedString(unreadNotificationsCount)).append("\n");
    sb.append("    unreadPostStatus: ").append(toIndentedString(unreadPostStatus)).append("\n");
    sb.append("    mySolidarity: ").append(toIndentedString(mySolidarity)).append("\n");
    sb.append("    actBestPosts: ").append(toIndentedString(actBestPosts)).append("\n");
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

