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
 * NotificationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class NotificationResponse {

  private Long id;

  private Long postId = null;

  private String category;

  private String linkUrl;

  private String type;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  private UserPostNotificationResponse post;

  public NotificationResponse id(Long id) {
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

  public NotificationResponse postId(Long postId) {
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

  public NotificationResponse category(String category) {
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

  public NotificationResponse linkUrl(String linkUrl) {
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

  public NotificationResponse type(String type) {
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

  public NotificationResponse createdAt(java.time.Instant createdAt) {
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

  public NotificationResponse post(UserPostNotificationResponse post) {
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
    NotificationResponse notificationResponse = (NotificationResponse) o;
    return Objects.equals(this.id, notificationResponse.id) &&
        Objects.equals(this.postId, notificationResponse.postId) &&
        Objects.equals(this.category, notificationResponse.category) &&
        Objects.equals(this.linkUrl, notificationResponse.linkUrl) &&
        Objects.equals(this.type, notificationResponse.type) &&
        Objects.equals(this.createdAt, notificationResponse.createdAt) &&
        Objects.equals(this.post, notificationResponse.post);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, postId, category, linkUrl, type, createdAt, post);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

