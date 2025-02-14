package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CommentResponse;
import ag.act.model.PostResponse;
import ag.act.model.ReportResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * ReportDetailResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ReportDetailResponse {

  private PostResponse post;

  private CommentResponse comment;

  @Valid
  private List<@Valid CommentResponse> reply;

  private ReportResponse report;

  public ReportDetailResponse post(PostResponse post) {
    this.post = post;
    return this;
  }

  /**
   * Get post
   * @return post
  */
  @Valid 
  @JsonProperty("post")
  public PostResponse getPost() {
    return post;
  }

  public void setPost(PostResponse post) {
    this.post = post;
  }

  public ReportDetailResponse comment(CommentResponse comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment
   * @return comment
  */
  @Valid 
  @JsonProperty("comment")
  public CommentResponse getComment() {
    return comment;
  }

  public void setComment(CommentResponse comment) {
    this.comment = comment;
  }

  public ReportDetailResponse reply(List<@Valid CommentResponse> reply) {
    this.reply = reply;
    return this;
  }

  public ReportDetailResponse addReplyItem(CommentResponse replyItem) {
    if (this.reply == null) {
      this.reply = new ArrayList<>();
    }
    this.reply.add(replyItem);
    return this;
  }

  /**
   * Get reply
   * @return reply
  */
  @Valid 
  @JsonProperty("reply")
  public List<@Valid CommentResponse> getReply() {
    return reply;
  }

  public void setReply(List<@Valid CommentResponse> reply) {
    this.reply = reply;
  }

  public ReportDetailResponse report(ReportResponse report) {
    this.report = report;
    return this;
  }

  /**
   * Get report
   * @return report
  */
  @Valid 
  @JsonProperty("report")
  public ReportResponse getReport() {
    return report;
  }

  public void setReport(ReportResponse report) {
    this.report = report;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportDetailResponse reportDetailResponse = (ReportDetailResponse) o;
    return Objects.equals(this.post, reportDetailResponse.post) &&
        Objects.equals(this.comment, reportDetailResponse.comment) &&
        Objects.equals(this.reply, reportDetailResponse.reply) &&
        Objects.equals(this.report, reportDetailResponse.report);
  }

  @Override
  public int hashCode() {
    return Objects.hash(post, comment, reply, report);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportDetailResponse {\n");
    sb.append("    post: ").append(toIndentedString(post)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("    reply: ").append(toIndentedString(reply)).append("\n");
    sb.append("    report: ").append(toIndentedString(report)).append("\n");
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

