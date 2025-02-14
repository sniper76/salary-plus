package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.UpdatePostRequest;
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
 * UpdateCampaignRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateCampaignRequest implements ag.act.dto.CampaignHtmlContent {

  private UpdatePostRequest updatePostRequest;

  @NotBlank(message = "캠페인 제목을 확인해주세요.")

  private String title;

  public UpdateCampaignRequest updatePostRequest(UpdatePostRequest updatePostRequest) {
    this.updatePostRequest = updatePostRequest;
    return this;
  }

  /**
   * Get updatePostRequest
   * @return updatePostRequest
  */
  @Valid 
  @JsonProperty("updatePostRequest")
  public UpdatePostRequest getUpdatePostRequest() {
    return updatePostRequest;
  }

  public void setUpdatePostRequest(UpdatePostRequest updatePostRequest) {
    this.updatePostRequest = updatePostRequest;
  }

  public UpdateCampaignRequest title(String title) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateCampaignRequest updateCampaignRequest = (UpdateCampaignRequest) o;
    return Objects.equals(this.updatePostRequest, updateCampaignRequest.updatePostRequest) &&
        Objects.equals(this.title, updateCampaignRequest.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updatePostRequest, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateCampaignRequest {\n");
    sb.append("    updatePostRequest: ").append(toIndentedString(updatePostRequest)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

