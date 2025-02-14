package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.PostResponse;
import ag.act.model.SimplePostResponse;
import ag.act.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * CampaignDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CampaignDetailsResponse {

  private Long id;

  private String title;

  private PostResponse sourcePost;

  private Long sourceStockGroupId;

  private String sourceStockGroupName;

  @Valid
  private List<@Valid SimplePostResponse> campaignPosts;

  private Status status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  public CampaignDetailsResponse id(Long id) {
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

  public CampaignDetailsResponse title(String title) {
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

  public CampaignDetailsResponse sourcePost(PostResponse sourcePost) {
    this.sourcePost = sourcePost;
    return this;
  }

  /**
   * Get sourcePost
   * @return sourcePost
  */
  @Valid 
  @JsonProperty("sourcePost")
  public PostResponse getSourcePost() {
    return sourcePost;
  }

  public void setSourcePost(PostResponse sourcePost) {
    this.sourcePost = sourcePost;
  }

  public CampaignDetailsResponse sourceStockGroupId(Long sourceStockGroupId) {
    this.sourceStockGroupId = sourceStockGroupId;
    return this;
  }

  /**
   * Get sourceStockGroupId
   * @return sourceStockGroupId
  */
  
  @JsonProperty("sourceStockGroupId")
  public Long getSourceStockGroupId() {
    return sourceStockGroupId;
  }

  public void setSourceStockGroupId(Long sourceStockGroupId) {
    this.sourceStockGroupId = sourceStockGroupId;
  }

  public CampaignDetailsResponse sourceStockGroupName(String sourceStockGroupName) {
    this.sourceStockGroupName = sourceStockGroupName;
    return this;
  }

  /**
   * Get sourceStockGroupName
   * @return sourceStockGroupName
  */
  
  @JsonProperty("sourceStockGroupName")
  public String getSourceStockGroupName() {
    return sourceStockGroupName;
  }

  public void setSourceStockGroupName(String sourceStockGroupName) {
    this.sourceStockGroupName = sourceStockGroupName;
  }

  public CampaignDetailsResponse campaignPosts(List<@Valid SimplePostResponse> campaignPosts) {
    this.campaignPosts = campaignPosts;
    return this;
  }

  public CampaignDetailsResponse addCampaignPostsItem(SimplePostResponse campaignPostsItem) {
    if (this.campaignPosts == null) {
      this.campaignPosts = new ArrayList<>();
    }
    this.campaignPosts.add(campaignPostsItem);
    return this;
  }

  /**
   * Get campaignPosts
   * @return campaignPosts
  */
  @Valid 
  @JsonProperty("campaignPosts")
  public List<@Valid SimplePostResponse> getCampaignPosts() {
    return campaignPosts;
  }

  public void setCampaignPosts(List<@Valid SimplePostResponse> campaignPosts) {
    this.campaignPosts = campaignPosts;
  }

  public CampaignDetailsResponse status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @JsonProperty("status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public CampaignDetailsResponse createdAt(java.time.Instant createdAt) {
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

  public CampaignDetailsResponse updatedAt(java.time.Instant updatedAt) {
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

  public CampaignDetailsResponse deletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
    return this;
  }

  /**
   * Get deletedAt
   * @return deletedAt
  */
  @Valid 
  @JsonProperty("deletedAt")
  public java.time.Instant getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CampaignDetailsResponse campaignDetailsResponse = (CampaignDetailsResponse) o;
    return Objects.equals(this.id, campaignDetailsResponse.id) &&
        Objects.equals(this.title, campaignDetailsResponse.title) &&
        Objects.equals(this.sourcePost, campaignDetailsResponse.sourcePost) &&
        Objects.equals(this.sourceStockGroupId, campaignDetailsResponse.sourceStockGroupId) &&
        Objects.equals(this.sourceStockGroupName, campaignDetailsResponse.sourceStockGroupName) &&
        Objects.equals(this.campaignPosts, campaignDetailsResponse.campaignPosts) &&
        Objects.equals(this.status, campaignDetailsResponse.status) &&
        Objects.equals(this.createdAt, campaignDetailsResponse.createdAt) &&
        Objects.equals(this.updatedAt, campaignDetailsResponse.updatedAt) &&
        Objects.equals(this.deletedAt, campaignDetailsResponse.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, sourcePost, sourceStockGroupId, sourceStockGroupName, campaignPosts, status, createdAt, updatedAt, deletedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CampaignDetailsResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    sourcePost: ").append(toIndentedString(sourcePost)).append("\n");
    sb.append("    sourceStockGroupId: ").append(toIndentedString(sourceStockGroupId)).append("\n");
    sb.append("    sourceStockGroupName: ").append(toIndentedString(sourceStockGroupName)).append("\n");
    sb.append("    campaignPosts: ").append(toIndentedString(campaignPosts)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
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

