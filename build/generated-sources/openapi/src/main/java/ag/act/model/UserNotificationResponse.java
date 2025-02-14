package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UserPostNotificationResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * UserNotificationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserNotificationResponse {

  private Long id;

  private Long postId = null;

  private String category;

  private String linkUrl;

  private String type;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeStartDate;

  private Boolean isRead;

  private UserPostNotificationResponse post;

  public UserNotificationResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Notification ID
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserNotificationResponse postId(Long postId) {
    this.postId = postId;
    return this;
  }

  /**
   * ID of post
   * @return postId
  */
  
  @JsonProperty("postId")
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public UserNotificationResponse category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Notification category
   * @return category
  */
  
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public UserNotificationResponse linkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * Link Url for the app when notification tapped
   * @return linkUrl
  */
  
  @JsonProperty("linkUrl")
  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public UserNotificationResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Notification type
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UserNotificationResponse createdAt(java.time.Instant createdAt) {
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

  public UserNotificationResponse activeStartDate(java.time.Instant activeStartDate) {
    this.activeStartDate = activeStartDate;
    return this;
  }

  /**
   * Get activeStartDate
   * @return activeStartDate
  */
  @Valid 
  @JsonProperty("activeStartDate")
  public java.time.Instant getActiveStartDate() {
    return activeStartDate;
  }

  public void setActiveStartDate(java.time.Instant activeStartDate) {
    this.activeStartDate = activeStartDate;
  }

  public UserNotificationResponse isRead(Boolean isRead) {
    this.isRead = isRead;
    return this;
  }

  /**
   * Whether the notification is read or not
   * @return isRead
  */
  
  @JsonProperty("isRead")
  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public UserNotificationResponse post(UserPostNotificationResponse post) {
    this.post = post;
    return this;
  }

  /**
   * Get post
   * @return post
  */
  @Valid 
  @JsonProperty("post")
  public UserPostNotificationResponse getPost() {
    return post;
  }

  public void setPost(UserPostNotificationResponse post) {
    this.post = post;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserNotificationResponse userNotificationResponse = (UserNotificationResponse) o;
    return Objects.equals(this.id, userNotificationResponse.id) &&
        Objects.equals(this.postId, userNotificationResponse.postId) &&
        Objects.equals(this.category, userNotificationResponse.category) &&
        Objects.equals(this.linkUrl, userNotificationResponse.linkUrl) &&
        Objects.equals(this.type, userNotificationResponse.type) &&
        Objects.equals(this.createdAt, userNotificationResponse.createdAt) &&
        Objects.equals(this.activeStartDate, userNotificationResponse.activeStartDate) &&
        Objects.equals(this.isRead, userNotificationResponse.isRead) &&
        Objects.equals(this.post, userNotificationResponse.post);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, postId, category, linkUrl, type, createdAt, activeStartDate, isRead, post);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserNotificationResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    activeStartDate: ").append(toIndentedString(activeStartDate)).append("\n");
    sb.append("    isRead: ").append(toIndentedString(isRead)).append("\n");
    sb.append("    post: ").append(toIndentedString(post)).append("\n");
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

