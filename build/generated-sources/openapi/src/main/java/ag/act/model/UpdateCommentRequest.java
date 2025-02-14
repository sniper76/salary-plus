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
 * UpdateCommentRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateCommentRequest implements ag.act.dto.HtmlContent {

  @NotBlank(message = "댓글를 확인해주세요.")

  private String content;

  @IsBoolean(message = "익명여부를 확인해주세요.")

  private Boolean isAnonymous = false;

  private Boolean isEd = false;

  public UpdateCommentRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Content of the comment
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public UpdateCommentRequest isAnonymous(Boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
    return this;
  }

  /**
   * Whether the comment should be anonymous
   * @return isAnonymous
  */
  
  @JsonProperty("isAnonymous")
  public Boolean getIsAnonymous() {
    return isAnonymous;
  }

  public void setIsAnonymous(Boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
  }

  public UpdateCommentRequest isEd(Boolean isEd) {
    this.isEd = isEd;
    return this;
  }

  /**
   * Get isEd
   * @return isEd
  */
  
  @JsonProperty("isEd")
  public Boolean getIsEd() {
    return isEd;
  }

  public void setIsEd(Boolean isEd) {
    this.isEd = isEd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateCommentRequest updateCommentRequest = (UpdateCommentRequest) o;
    return Objects.equals(this.content, updateCommentRequest.content) &&
        Objects.equals(this.isAnonymous, updateCommentRequest.isAnonymous) &&
        Objects.equals(this.isEd, updateCommentRequest.isEd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, isAnonymous, isEd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateCommentRequest {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    isAnonymous: ").append(toIndentedString(isAnonymous)).append("\n");
    sb.append("    isEd: ").append(toIndentedString(isEd)).append("\n");
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

