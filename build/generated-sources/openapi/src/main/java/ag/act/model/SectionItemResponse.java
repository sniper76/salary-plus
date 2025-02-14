package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.BoardCategoryResponse;
import ag.act.model.UserProfileResponse;
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
 * SectionItemResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SectionItemResponse {

  private String title;

  private UserProfileResponse userProfile = null;

  private BoardCategoryResponse boardCategory;

  private Long viewCount;

  private String link;

  private Long likeCount;

  private Boolean reported;

  private Boolean deleted;

  private Boolean isNew;

  private Boolean isExclusiveToHolders = false;

  private Long commentCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public SectionItemResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public SectionItemResponse userProfile(UserProfileResponse userProfile) {
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

  public SectionItemResponse boardCategory(BoardCategoryResponse boardCategory) {
    this.boardCategory = boardCategory;
    return this;
  }

  /**
   * Get boardCategory
   * @return boardCategory
  */
  @Valid 
  @JsonProperty("boardCategory")
  public BoardCategoryResponse getBoardCategory() {
    return boardCategory;
  }

  public void setBoardCategory(BoardCategoryResponse boardCategory) {
    this.boardCategory = boardCategory;
  }

  public SectionItemResponse viewCount(Long viewCount) {
    this.viewCount = viewCount;
    return this;
  }

  /**
   * Get viewCount
   * @return viewCount
  */
  
  @JsonProperty("viewCount")
  public Long getViewCount() {
    return viewCount;
  }

  public void setViewCount(Long viewCount) {
    this.viewCount = viewCount;
  }

  public SectionItemResponse link(String link) {
    this.link = link;
    return this;
  }

  /**
   * Get link
   * @return link
  */
  
  @JsonProperty("link")
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public SectionItemResponse likeCount(Long likeCount) {
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

  public SectionItemResponse reported(Boolean reported) {
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

  public SectionItemResponse deleted(Boolean deleted) {
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

  public SectionItemResponse isNew(Boolean isNew) {
    this.isNew = isNew;
    return this;
  }

  /**
   * Get isNew
   * @return isNew
  */
  
  @JsonProperty("isNew")
  public Boolean getIsNew() {
    return isNew;
  }

  public void setIsNew(Boolean isNew) {
    this.isNew = isNew;
  }

  public SectionItemResponse isExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
    return this;
  }

  /**
   * Get isExclusiveToHolders
   * @return isExclusiveToHolders
  */
  
  @JsonProperty("isExclusiveToHolders")
  public Boolean getIsExclusiveToHolders() {
    return isExclusiveToHolders;
  }

  public void setIsExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
  }

  public SectionItemResponse commentCount(Long commentCount) {
    this.commentCount = commentCount;
    return this;
  }

  /**
   * Get commentCount
   * @return commentCount
  */
  
  @JsonProperty("commentCount")
  public Long getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Long commentCount) {
    this.commentCount = commentCount;
  }

  public SectionItemResponse createdAt(java.time.Instant createdAt) {
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

  public SectionItemResponse updatedAt(java.time.Instant updatedAt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SectionItemResponse sectionItemResponse = (SectionItemResponse) o;
    return Objects.equals(this.title, sectionItemResponse.title) &&
        Objects.equals(this.userProfile, sectionItemResponse.userProfile) &&
        Objects.equals(this.boardCategory, sectionItemResponse.boardCategory) &&
        Objects.equals(this.viewCount, sectionItemResponse.viewCount) &&
        Objects.equals(this.link, sectionItemResponse.link) &&
        Objects.equals(this.likeCount, sectionItemResponse.likeCount) &&
        Objects.equals(this.reported, sectionItemResponse.reported) &&
        Objects.equals(this.deleted, sectionItemResponse.deleted) &&
        Objects.equals(this.isNew, sectionItemResponse.isNew) &&
        Objects.equals(this.isExclusiveToHolders, sectionItemResponse.isExclusiveToHolders) &&
        Objects.equals(this.commentCount, sectionItemResponse.commentCount) &&
        Objects.equals(this.createdAt, sectionItemResponse.createdAt) &&
        Objects.equals(this.updatedAt, sectionItemResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, userProfile, boardCategory, viewCount, link, likeCount, reported, deleted, isNew, isExclusiveToHolders, commentCount, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SectionItemResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    boardCategory: ").append(toIndentedString(boardCategory)).append("\n");
    sb.append("    viewCount: ").append(toIndentedString(viewCount)).append("\n");
    sb.append("    link: ").append(toIndentedString(link)).append("\n");
    sb.append("    likeCount: ").append(toIndentedString(likeCount)).append("\n");
    sb.append("    reported: ").append(toIndentedString(reported)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    isNew: ").append(toIndentedString(isNew)).append("\n");
    sb.append("    isExclusiveToHolders: ").append(toIndentedString(isExclusiveToHolders)).append("\n");
    sb.append("    commentCount: ").append(toIndentedString(commentCount)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

