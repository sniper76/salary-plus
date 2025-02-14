package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CreatePostRequest;
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
 * CreateCampaignRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateCampaignRequest implements ag.act.dto.CampaignHtmlContent {

  private CreatePostRequest createPostRequest;

  @NotBlank(message = "캠페인 제목을 확인해주세요.")

  private String title;

  @NotBlank(message = "게시판 그룹을 확인해주세요.")

  private String boardGroupName;

  private Long stockGroupId;

  public CreateCampaignRequest createPostRequest(CreatePostRequest createPostRequest) {
    this.createPostRequest = createPostRequest;
    return this;
  }

  /**
   * Get createPostRequest
   * @return createPostRequest
  */
  @Valid 
  @JsonProperty("createPostRequest")
  public CreatePostRequest getCreatePostRequest() {
    return createPostRequest;
  }

  public void setCreatePostRequest(CreatePostRequest createPostRequest) {
    this.createPostRequest = createPostRequest;
  }

  public CreateCampaignRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Campaign title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CreateCampaignRequest boardGroupName(String boardGroupName) {
    this.boardGroupName = boardGroupName;
    return this;
  }

  /**
   * BoardGroup ACTION or ANALYSIS or DEBATE
   * @return boardGroupName
  */
  
  @JsonProperty("boardGroupName")
  public String getBoardGroupName() {
    return boardGroupName;
  }

  public void setBoardGroupName(String boardGroupName) {
    this.boardGroupName = boardGroupName;
  }

  public CreateCampaignRequest stockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
    return this;
  }

  /**
   * Stock group ID
   * @return stockGroupId
  */
  
  @JsonProperty("stockGroupId")
  public Long getStockGroupId() {
    return stockGroupId;
  }

  public void setStockGroupId(Long stockGroupId) {
    this.stockGroupId = stockGroupId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateCampaignRequest createCampaignRequest = (CreateCampaignRequest) o;
    return Objects.equals(this.createPostRequest, createCampaignRequest.createPostRequest) &&
        Objects.equals(this.title, createCampaignRequest.title) &&
        Objects.equals(this.boardGroupName, createCampaignRequest.boardGroupName) &&
        Objects.equals(this.stockGroupId, createCampaignRequest.stockGroupId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(createPostRequest, title, boardGroupName, stockGroupId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateCampaignRequest {\n");
    sb.append("    createPostRequest: ").append(toIndentedString(createPostRequest)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    boardGroupName: ").append(toIndentedString(boardGroupName)).append("\n");
    sb.append("    stockGroupId: ").append(toIndentedString(stockGroupId)).append("\n");
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

