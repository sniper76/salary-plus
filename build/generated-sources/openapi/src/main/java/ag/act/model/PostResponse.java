package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.BoardCategoryResponse;
import ag.act.model.DigitalProxyResponse;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.PollResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.model.Status;
import ag.act.model.StockResponse;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.model.UserProfileResponse;
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
 * PostResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PostResponse {

  private UserProfileResponse userProfile = null;

  private BoardCategoryResponse boardCategory;

  private String boardGroup;

  private StockResponse stock;

  private PollResponse poll;

  @Valid
  private List<@Valid PollResponse> polls;

  private DigitalProxyResponse digitalProxy;

  private UserDigitalDocumentResponse digitalDocument;

  private HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocument;

  private Long id;

  private Long boardId;

  private String title;

  private String content = null;

  private Status status;

  private Long userId;

  private Long likeCount;

  private Long commentCount;

  private Long viewCount;

  private Boolean isActive;

  private Boolean isNotification;

  private Boolean isExclusiveToHolders = false;

  private Boolean isPinned = false;

  private Boolean isPush = false;

  private Boolean isNew;

  private Boolean liked;

  private Boolean deleted;

  private Boolean reported;

  private Boolean isAuthorAdmin;

  private String thumbnailImageUrl;

  @Valid
  private List<@Valid SimpleImageResponse> postImageList;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant activeEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant editedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  public PostResponse userProfile(UserProfileResponse userProfile) {
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

  public PostResponse boardCategory(BoardCategoryResponse boardCategory) {
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

  public PostResponse boardGroup(String boardGroup) {
    this.boardGroup = boardGroup;
    return this;
  }

  /**
   * Get boardGroup
   * @return boardGroup
  */
  
  @JsonProperty("boardGroup")
  public String getBoardGroup() {
    return boardGroup;
  }

  public void setBoardGroup(String boardGroup) {
    this.boardGroup = boardGroup;
  }

  public PostResponse stock(StockResponse stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * @return stock
  */
  @Valid 
  @JsonProperty("stock")
  public StockResponse getStock() {
    return stock;
  }

  public void setStock(StockResponse stock) {
    this.stock = stock;
  }

  public PostResponse poll(PollResponse poll) {
    this.poll = poll;
    return this;
  }

  /**
   * Get poll
   * @return poll
  */
  @Valid 
  @JsonProperty("poll")
  public PollResponse getPoll() {
    return poll;
  }

  public void setPoll(PollResponse poll) {
    this.poll = poll;
  }

  public PostResponse polls(List<@Valid PollResponse> polls) {
    this.polls = polls;
    return this;
  }

  public PostResponse addPollsItem(PollResponse pollsItem) {
    if (this.polls == null) {
      this.polls = new ArrayList<>();
    }
    this.polls.add(pollsItem);
    return this;
  }

  /**
   * Get polls
   * @return polls
  */
  @Valid 
  @JsonProperty("polls")
  public List<@Valid PollResponse> getPolls() {
    return polls;
  }

  public void setPolls(List<@Valid PollResponse> polls) {
    this.polls = polls;
  }

  public PostResponse digitalProxy(DigitalProxyResponse digitalProxy) {
    this.digitalProxy = digitalProxy;
    return this;
  }

  /**
   * Get digitalProxy
   * @return digitalProxy
  */
  @Valid 
  @JsonProperty("digitalProxy")
  public DigitalProxyResponse getDigitalProxy() {
    return digitalProxy;
  }

  public void setDigitalProxy(DigitalProxyResponse digitalProxy) {
    this.digitalProxy = digitalProxy;
  }

  public PostResponse digitalDocument(UserDigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
    return this;
  }

  /**
   * Get digitalDocument
   * @return digitalDocument
  */
  @Valid 
  @JsonProperty("digitalDocument")
  public UserDigitalDocumentResponse getDigitalDocument() {
    return digitalDocument;
  }

  public void setDigitalDocument(UserDigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
  }

  public PostResponse holderListReadAndCopyDigitalDocument(HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocument) {
    this.holderListReadAndCopyDigitalDocument = holderListReadAndCopyDigitalDocument;
    return this;
  }

  /**
   * Get holderListReadAndCopyDigitalDocument
   * @return holderListReadAndCopyDigitalDocument
  */
  @Valid 
  @JsonProperty("holderListReadAndCopyDigitalDocument")
  public HolderListReadAndCopyDigitalDocumentResponse getHolderListReadAndCopyDigitalDocument() {
    return holderListReadAndCopyDigitalDocument;
  }

  public void setHolderListReadAndCopyDigitalDocument(HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocument) {
    this.holderListReadAndCopyDigitalDocument = holderListReadAndCopyDigitalDocument;
  }

  public PostResponse id(Long id) {
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

  public PostResponse boardId(Long boardId) {
    this.boardId = boardId;
    return this;
  }

  /**
   * ID of the board the post belongs to
   * @return boardId
  */
  
  @JsonProperty("boardId")
  public Long getBoardId() {
    return boardId;
  }

  public void setBoardId(Long boardId) {
    this.boardId = boardId;
  }

  public PostResponse title(String title) {
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

  public PostResponse content(String content) {
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

  public PostResponse status(Status status) {
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

  public PostResponse userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * ID of the user who created the post
   * @return userId
  */
  
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public PostResponse likeCount(Long likeCount) {
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

  public PostResponse commentCount(Long commentCount) {
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

  public PostResponse viewCount(Long viewCount) {
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

  public PostResponse isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Get isActive
   * @return isActive
  */
  
  @JsonProperty("isActive")
  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public PostResponse isNotification(Boolean isNotification) {
    this.isNotification = isNotification;
    return this;
  }

  /**
   * Get isNotification
   * @return isNotification
  */
  
  @JsonProperty("isNotification")
  public Boolean getIsNotification() {
    return isNotification;
  }

  public void setIsNotification(Boolean isNotification) {
    this.isNotification = isNotification;
  }

  public PostResponse isExclusiveToHolders(Boolean isExclusiveToHolders) {
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

  public PostResponse isPinned(Boolean isPinned) {
    this.isPinned = isPinned;
    return this;
  }

  /**
   * Get isPinned
   * @return isPinned
  */
  
  @JsonProperty("isPinned")
  public Boolean getIsPinned() {
    return isPinned;
  }

  public void setIsPinned(Boolean isPinned) {
    this.isPinned = isPinned;
  }

  public PostResponse isPush(Boolean isPush) {
    this.isPush = isPush;
    return this;
  }

  /**
   * Get isPush
   * @return isPush
  */
  
  @JsonProperty("isPush")
  public Boolean getIsPush() {
    return isPush;
  }

  public void setIsPush(Boolean isPush) {
    this.isPush = isPush;
  }

  public PostResponse isNew(Boolean isNew) {
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

  public PostResponse liked(Boolean liked) {
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

  public PostResponse deleted(Boolean deleted) {
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

  public PostResponse reported(Boolean reported) {
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

  public PostResponse isAuthorAdmin(Boolean isAuthorAdmin) {
    this.isAuthorAdmin = isAuthorAdmin;
    return this;
  }

  /**
   * Get isAuthorAdmin
   * @return isAuthorAdmin
  */
  
  @JsonProperty("isAuthorAdmin")
  public Boolean getIsAuthorAdmin() {
    return isAuthorAdmin;
  }

  public void setIsAuthorAdmin(Boolean isAuthorAdmin) {
    this.isAuthorAdmin = isAuthorAdmin;
  }

  public PostResponse thumbnailImageUrl(String thumbnailImageUrl) {
    this.thumbnailImageUrl = thumbnailImageUrl;
    return this;
  }

  /**
   * Get thumbnailImageUrl
   * @return thumbnailImageUrl
  */
  
  @JsonProperty("thumbnailImageUrl")
  public String getThumbnailImageUrl() {
    return thumbnailImageUrl;
  }

  public void setThumbnailImageUrl(String thumbnailImageUrl) {
    this.thumbnailImageUrl = thumbnailImageUrl;
  }

  public PostResponse postImageList(List<@Valid SimpleImageResponse> postImageList) {
    this.postImageList = postImageList;
    return this;
  }

  public PostResponse addPostImageListItem(SimpleImageResponse postImageListItem) {
    if (this.postImageList == null) {
      this.postImageList = new ArrayList<>();
    }
    this.postImageList.add(postImageListItem);
    return this;
  }

  /**
   * Get postImageList
   * @return postImageList
  */
  @Valid 
  @JsonProperty("postImageList")
  public List<@Valid SimpleImageResponse> getPostImageList() {
    return postImageList;
  }

  public void setPostImageList(List<@Valid SimpleImageResponse> postImageList) {
    this.postImageList = postImageList;
  }

  public PostResponse activeStartDate(java.time.Instant activeStartDate) {
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

  public PostResponse activeEndDate(java.time.Instant activeEndDate) {
    this.activeEndDate = activeEndDate;
    return this;
  }

  /**
   * Get activeEndDate
   * @return activeEndDate
  */
  @Valid 
  @JsonProperty("activeEndDate")
  public java.time.Instant getActiveEndDate() {
    return activeEndDate;
  }

  public void setActiveEndDate(java.time.Instant activeEndDate) {
    this.activeEndDate = activeEndDate;
  }

  public PostResponse createdAt(java.time.Instant createdAt) {
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

  public PostResponse updatedAt(java.time.Instant updatedAt) {
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

  public PostResponse editedAt(java.time.Instant editedAt) {
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

  public PostResponse deletedAt(java.time.Instant deletedAt) {
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
    PostResponse postResponse = (PostResponse) o;
    return Objects.equals(this.userProfile, postResponse.userProfile) &&
        Objects.equals(this.boardCategory, postResponse.boardCategory) &&
        Objects.equals(this.boardGroup, postResponse.boardGroup) &&
        Objects.equals(this.stock, postResponse.stock) &&
        Objects.equals(this.poll, postResponse.poll) &&
        Objects.equals(this.polls, postResponse.polls) &&
        Objects.equals(this.digitalProxy, postResponse.digitalProxy) &&
        Objects.equals(this.digitalDocument, postResponse.digitalDocument) &&
        Objects.equals(this.holderListReadAndCopyDigitalDocument, postResponse.holderListReadAndCopyDigitalDocument) &&
        Objects.equals(this.id, postResponse.id) &&
        Objects.equals(this.boardId, postResponse.boardId) &&
        Objects.equals(this.title, postResponse.title) &&
        Objects.equals(this.content, postResponse.content) &&
        Objects.equals(this.status, postResponse.status) &&
        Objects.equals(this.userId, postResponse.userId) &&
        Objects.equals(this.likeCount, postResponse.likeCount) &&
        Objects.equals(this.commentCount, postResponse.commentCount) &&
        Objects.equals(this.viewCount, postResponse.viewCount) &&
        Objects.equals(this.isActive, postResponse.isActive) &&
        Objects.equals(this.isNotification, postResponse.isNotification) &&
        Objects.equals(this.isExclusiveToHolders, postResponse.isExclusiveToHolders) &&
        Objects.equals(this.isPinned, postResponse.isPinned) &&
        Objects.equals(this.isPush, postResponse.isPush) &&
        Objects.equals(this.isNew, postResponse.isNew) &&
        Objects.equals(this.liked, postResponse.liked) &&
        Objects.equals(this.deleted, postResponse.deleted) &&
        Objects.equals(this.reported, postResponse.reported) &&
        Objects.equals(this.isAuthorAdmin, postResponse.isAuthorAdmin) &&
        Objects.equals(this.thumbnailImageUrl, postResponse.thumbnailImageUrl) &&
        Objects.equals(this.postImageList, postResponse.postImageList) &&
        Objects.equals(this.activeStartDate, postResponse.activeStartDate) &&
        Objects.equals(this.activeEndDate, postResponse.activeEndDate) &&
        Objects.equals(this.createdAt, postResponse.createdAt) &&
        Objects.equals(this.updatedAt, postResponse.updatedAt) &&
        Objects.equals(this.editedAt, postResponse.editedAt) &&
        Objects.equals(this.deletedAt, postResponse.deletedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userProfile, boardCategory, boardGroup, stock, poll, polls, digitalProxy, digitalDocument, holderListReadAndCopyDigitalDocument, id, boardId, title, content, status, userId, likeCount, commentCount, viewCount, isActive, isNotification, isExclusiveToHolders, isPinned, isPush, isNew, liked, deleted, reported, isAuthorAdmin, thumbnailImageUrl, postImageList, activeStartDate, activeEndDate, createdAt, updatedAt, editedAt, deletedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostResponse {\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    boardCategory: ").append(toIndentedString(boardCategory)).append("\n");
    sb.append("    boardGroup: ").append(toIndentedString(boardGroup)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    poll: ").append(toIndentedString(poll)).append("\n");
    sb.append("    polls: ").append(toIndentedString(polls)).append("\n");
    sb.append("    digitalProxy: ").append(toIndentedString(digitalProxy)).append("\n");
    sb.append("    digitalDocument: ").append(toIndentedString(digitalDocument)).append("\n");
    sb.append("    holderListReadAndCopyDigitalDocument: ").append(toIndentedString(holderListReadAndCopyDigitalDocument)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    boardId: ").append(toIndentedString(boardId)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    likeCount: ").append(toIndentedString(likeCount)).append("\n");
    sb.append("    commentCount: ").append(toIndentedString(commentCount)).append("\n");
    sb.append("    viewCount: ").append(toIndentedString(viewCount)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    isNotification: ").append(toIndentedString(isNotification)).append("\n");
    sb.append("    isExclusiveToHolders: ").append(toIndentedString(isExclusiveToHolders)).append("\n");
    sb.append("    isPinned: ").append(toIndentedString(isPinned)).append("\n");
    sb.append("    isPush: ").append(toIndentedString(isPush)).append("\n");
    sb.append("    isNew: ").append(toIndentedString(isNew)).append("\n");
    sb.append("    liked: ").append(toIndentedString(liked)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    reported: ").append(toIndentedString(reported)).append("\n");
    sb.append("    isAuthorAdmin: ").append(toIndentedString(isAuthorAdmin)).append("\n");
    sb.append("    thumbnailImageUrl: ").append(toIndentedString(thumbnailImageUrl)).append("\n");
    sb.append("    postImageList: ").append(toIndentedString(postImageList)).append("\n");
    sb.append("    activeStartDate: ").append(toIndentedString(activeStartDate)).append("\n");
    sb.append("    activeEndDate: ").append(toIndentedString(activeEndDate)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    editedAt: ").append(toIndentedString(editedAt)).append("\n");
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

