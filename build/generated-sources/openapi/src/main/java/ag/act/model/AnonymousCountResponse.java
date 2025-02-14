package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.AnonymousCountResponsePost;
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
 * AnonymousCountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class AnonymousCountResponse {

  private AnonymousCountResponsePost post;

  private AnonymousCountResponsePost comment;

  public AnonymousCountResponse post(AnonymousCountResponsePost post) {
    this.post = post;
    return this;
  }

  /**
   * Get post
   * @return post
  */
  @Valid 
  @JsonProperty("post")
  public AnonymousCountResponsePost getPost() {
    return post;
  }

  public void setPost(AnonymousCountResponsePost post) {
    this.post = post;
  }

  public AnonymousCountResponse comment(AnonymousCountResponsePost comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment
   * @return comment
  */
  @Valid 
  @JsonProperty("comment")
  public AnonymousCountResponsePost getComment() {
    return comment;
  }

  public void setComment(AnonymousCountResponsePost comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnonymousCountResponse anonymousCountResponse = (AnonymousCountResponse) o;
    return Objects.equals(this.post, anonymousCountResponse.post) &&
        Objects.equals(this.comment, anonymousCountResponse.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(post, comment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnonymousCountResponse {\n");
    sb.append("    post: ").append(toIndentedString(post)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
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

