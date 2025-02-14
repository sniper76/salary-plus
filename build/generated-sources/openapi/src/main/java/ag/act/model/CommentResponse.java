package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UserProfileResponse;
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
 * CommentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CommentResponse {

  private UserProfileResponse userProfile = null;

  private Long id;

  private Long userId;

  private String content;

  private Boolean liked;

  private Boolean deleted;

  private Boolean reported;

  private Long likeCount;

  private Long replyCommentCount;

  @Valid
  private List<@Valid CommentResponse> replyComments;

  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant editedAt;

  public CommentResponse userProfile(UserProfileResponse userProfile) {
    this.userProfile = userProfile;
    return this;
  }

  /**
   * Get userProfile
   * @return userProfile
  */
  @Valid 
  @JsonProperty("userProfile")
  public UserProfileResponse getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfileResponse userProfile) {
    this.userProfile = userProfile;
  }

  public CommentResponse id(Long id) {
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

  public CommentResponse userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public CommentResponse content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public CommentResponse liked(Boolean liked) {
    this.liked = liked;
    return this;
  }

  /**
   * Get liked
   * @return liked
  */
  
  @JsonProperty("liked")
  public Boolean getLiked() {
    return liked;
  }

  public void setLiked(Boolean liked) {
    this.liked = liked;
  }

  public CommentResponse deleted(Boolean deleted) {
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

  public CommentResponse reported(Boolean reported) {
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

  public CommentResponse likeCount(Long likeCount) {
    this.likeCount = likeCount;
    return this;
  }

  /**
   * Get likeCount
   * @return likeCount
  */
  
  @JsonProperty("likeCount")
  public Long getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Long likeCount) {
    this.likeCount = likeCount;
  }

  public CommentResponse replyCommentCount(Long replyCommentCount) {
    this.replyCommentCount = replyCommentCount;
    return this;
  }

  /**
   * Get replyCommentCount
   * @return replyCommentCount
  */
  
  @JsonProperty("replyCommentCount")
  public Long getReplyCommentCount() {
    return replyCommentCount;
  }

  public void setReplyCommentCount(Long replyCommentCount) {
    this.replyCommentCount = replyCommentCount;
  }

  public CommentResponse replyComments(List<@Valid CommentResponse> replyComments) {
    this.replyComments = replyComments;
    return this;
  }

  public CommentResponse addReplyCommentsItem(CommentResponse replyCommentsItem) {
    if (this.replyComments == null) {
      this.replyComments = new ArrayList<>();
    }
    this.replyComments.add(replyCommentsItem);
    return this;
  }

  /**
   * Get replyComments
   * @return replyComments
  */
  @Valid 
  @JsonProperty("replyComments")
  public List<@Valid CommentResponse> getReplyComments() {
    return replyComments;
  }

  public void setReplyComments(List<@Valid CommentResponse> replyComments) {
    this.replyComments = replyComments;
  }

  public CommentResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CommentResponse createdAt(java.time.Instant createdAt) {
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

  public CommentResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  */
  @Valid 
  @JsonProperty("updatedAt")
  public java.time.Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public CommentResponse editedAt(java.time.Instant editedAt) {
    this.editedAt = editedAt;
    return this;
  }

  /**
   * Get editedAt
   * @return editedAt
  */
  @Valid 
  @JsonProperty("editedAt")
  public java.time.Instant getEditedAt() {
    return editedAt;
  }

  public void setEditedAt(java.time.Instant editedAt) {
    this.editedAt = editedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentResponse commentResponse = (CommentResponse) o;
    return Objects.equals(this.userProfile, commentResponse.userProfile) &&
        Objects.equals(this.id, commentResponse.id) &&
        Objects.equals(this.userId, commentResponse.userId) &&
        Objects.equals(this.content, commentResponse.content) &&
        Objects.equals(this.liked, commentResponse.liked) &&
        Objects.equals(this.deleted, commentResponse.deleted) &&
        Objects.equals(this.reported, commentResponse.reported) &&
        Objects.equals(this.likeCount, commentResponse.likeCount) &&
        Objects.equals(this.replyCommentCount, commentResponse.replyCommentCount) &&
        Objects.equals(this.replyComments, commentResponse.replyComments) &&
        Objects.equals(this.status, commentResponse.status) &&
        Objects.equals(this.createdAt, commentResponse.createdAt) &&
        Objects.equals(this.updatedAt, commentResponse.updatedAt) &&
        Objects.equals(this.editedAt, commentResponse.editedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userProfile, id, userId, content, liked, deleted, reported, likeCount, replyCommentCount, replyComments, status, createdAt, updatedAt, editedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommentResponse {\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    liked: ").append(toIndentedString(liked)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    reported: ").append(toIndentedString(reported)).append("\n");
    sb.append("    likeCount: ").append(toIndentedString(likeCount)).append("\n");
    sb.append("    replyCommentCount: ").append(toIndentedString(replyCommentCount)).append("\n");
    sb.append("    replyComments: ").append(toIndentedString(replyComments)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    editedAt: ").append(toIndentedString(editedAt)).append("\n");
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

