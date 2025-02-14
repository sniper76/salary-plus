package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.LeaderElectionProcessDetailResponse;
import ag.act.model.LeaderElectionProcessLabelResponse;
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
 * LeaderElectionProcessResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class LeaderElectionProcessResponse {

  private String title;

  private String electionStatus;

  private LeaderElectionProcessDetailResponse detail;

  private LeaderElectionProcessLabelResponse label;

  public LeaderElectionProcessResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 후보자 등록 기간 or 주주대표 투표 or 투표마감
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public LeaderElectionProcessResponse electionStatus(String electionStatus) {
    this.electionStatus = electionStatus;
    return this;
  }

  /**
   * FINISHED or VOTE_PERIOD or CANDIDATE_REGISTRATION_PERIOD
   * @return electionStatus
  */
  
  @JsonProperty("electionStatus")
  public String getElectionStatus() {
    return electionStatus;
  }

  public void setElectionStatus(String electionStatus) {
    this.electionStatus = electionStatus;
  }

  public LeaderElectionProcessResponse detail(LeaderElectionProcessDetailResponse detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Get detail
   * @return detail
  */
  @Valid 
  @JsonProperty("detail")
  public LeaderElectionProcessDetailResponse getDetail() {
    return detail;
  }

  public void setDetail(LeaderElectionProcessDetailResponse detail) {
    this.detail = detail;
  }

  public LeaderElectionProcessResponse label(LeaderElectionProcessLabelResponse label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  @Valid 
  @JsonProperty("label")
  public LeaderElectionProcessLabelResponse getLabel() {
    return label;
  }

  public void setLabel(LeaderElectionProcessLabelResponse label) {
    this.label = label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderElectionProcessResponse leaderElectionProcessResponse = (LeaderElectionProcessResponse) o;
    return Objects.equals(this.title, leaderElectionProcessResponse.title) &&
        Objects.equals(this.electionStatus, leaderElectionProcessResponse.electionStatus) &&
        Objects.equals(this.detail, leaderElectionProcessResponse.detail) &&
        Objects.equals(this.label, leaderElectionProcessResponse.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, electionStatus, detail, label);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderElectionProcessResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    electionStatus: ").append(toIndentedString(electionStatus)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
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

