package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SolidarityLeaderElectionApplicantDataLabelResponse;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
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
 * SolidarityLeaderElectionApplicantDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionApplicantDataResponse {

  private Long solidarityLeaderApplicantId;

  private String nickname;

  private Long totalVoteStockQuantity;

  private SolidarityLeaderElectionApplicantDataLabelResponse resolutionCondition;

  private SolidarityLeaderElectionApplicantDataLabelResponse finishedEarlyCondition;

  @Valid
  private List<@Valid SolidarityLeaderElectionVoteItemDataResponse> pollItemGroups;

  public SolidarityLeaderElectionApplicantDataResponse solidarityLeaderApplicantId(Long solidarityLeaderApplicantId) {
    this.solidarityLeaderApplicantId = solidarityLeaderApplicantId;
    return this;
  }

  /**
   * Get solidarityLeaderApplicantId
   * @return solidarityLeaderApplicantId
  */
  
  @JsonProperty("solidarityLeaderApplicantId")
  public Long getSolidarityLeaderApplicantId() {
    return solidarityLeaderApplicantId;
  }

  public void setSolidarityLeaderApplicantId(Long solidarityLeaderApplicantId) {
    this.solidarityLeaderApplicantId = solidarityLeaderApplicantId;
  }

  public SolidarityLeaderElectionApplicantDataResponse nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  /**
   * Get nickname
   * @return nickname
  */
  
  @JsonProperty("nickname")
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public SolidarityLeaderElectionApplicantDataResponse totalVoteStockQuantity(Long totalVoteStockQuantity) {
    this.totalVoteStockQuantity = totalVoteStockQuantity;
    return this;
  }

  /**
   * Get totalVoteStockQuantity
   * @return totalVoteStockQuantity
  */
  
  @JsonProperty("totalVoteStockQuantity")
  public Long getTotalVoteStockQuantity() {
    return totalVoteStockQuantity;
  }

  public void setTotalVoteStockQuantity(Long totalVoteStockQuantity) {
    this.totalVoteStockQuantity = totalVoteStockQuantity;
  }

  public SolidarityLeaderElectionApplicantDataResponse resolutionCondition(SolidarityLeaderElectionApplicantDataLabelResponse resolutionCondition) {
    this.resolutionCondition = resolutionCondition;
    return this;
  }

  /**
   * Get resolutionCondition
   * @return resolutionCondition
  */
  @Valid 
  @JsonProperty("resolutionCondition")
  public SolidarityLeaderElectionApplicantDataLabelResponse getResolutionCondition() {
    return resolutionCondition;
  }

  public void setResolutionCondition(SolidarityLeaderElectionApplicantDataLabelResponse resolutionCondition) {
    this.resolutionCondition = resolutionCondition;
  }

  public SolidarityLeaderElectionApplicantDataResponse finishedEarlyCondition(SolidarityLeaderElectionApplicantDataLabelResponse finishedEarlyCondition) {
    this.finishedEarlyCondition = finishedEarlyCondition;
    return this;
  }

  /**
   * Get finishedEarlyCondition
   * @return finishedEarlyCondition
  */
  @Valid 
  @JsonProperty("finishedEarlyCondition")
  public SolidarityLeaderElectionApplicantDataLabelResponse getFinishedEarlyCondition() {
    return finishedEarlyCondition;
  }

  public void setFinishedEarlyCondition(SolidarityLeaderElectionApplicantDataLabelResponse finishedEarlyCondition) {
    this.finishedEarlyCondition = finishedEarlyCondition;
  }

  public SolidarityLeaderElectionApplicantDataResponse pollItemGroups(List<@Valid SolidarityLeaderElectionVoteItemDataResponse> pollItemGroups) {
    this.pollItemGroups = pollItemGroups;
    return this;
  }

  public SolidarityLeaderElectionApplicantDataResponse addPollItemGroupsItem(SolidarityLeaderElectionVoteItemDataResponse pollItemGroupsItem) {
    if (this.pollItemGroups == null) {
      this.pollItemGroups = new ArrayList<>();
    }
    this.pollItemGroups.add(pollItemGroupsItem);
    return this;
  }

  /**
   * Get pollItemGroups
   * @return pollItemGroups
  */
  @Valid 
  @JsonProperty("pollItemGroups")
  public List<@Valid SolidarityLeaderElectionVoteItemDataResponse> getPollItemGroups() {
    return pollItemGroups;
  }

  public void setPollItemGroups(List<@Valid SolidarityLeaderElectionVoteItemDataResponse> pollItemGroups) {
    this.pollItemGroups = pollItemGroups;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionApplicantDataResponse solidarityLeaderElectionApplicantDataResponse = (SolidarityLeaderElectionApplicantDataResponse) o;
    return Objects.equals(this.solidarityLeaderApplicantId, solidarityLeaderElectionApplicantDataResponse.solidarityLeaderApplicantId) &&
        Objects.equals(this.nickname, solidarityLeaderElectionApplicantDataResponse.nickname) &&
        Objects.equals(this.totalVoteStockQuantity, solidarityLeaderElectionApplicantDataResponse.totalVoteStockQuantity) &&
        Objects.equals(this.resolutionCondition, solidarityLeaderElectionApplicantDataResponse.resolutionCondition) &&
        Objects.equals(this.finishedEarlyCondition, solidarityLeaderElectionApplicantDataResponse.finishedEarlyCondition) &&
        Objects.equals(this.pollItemGroups, solidarityLeaderElectionApplicantDataResponse.pollItemGroups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityLeaderApplicantId, nickname, totalVoteStockQuantity, resolutionCondition, finishedEarlyCondition, pollItemGroups);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionApplicantDataResponse {\n");
    sb.append("    solidarityLeaderApplicantId: ").append(toIndentedString(solidarityLeaderApplicantId)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    totalVoteStockQuantity: ").append(toIndentedString(totalVoteStockQuantity)).append("\n");
    sb.append("    resolutionCondition: ").append(toIndentedString(resolutionCondition)).append("\n");
    sb.append("    finishedEarlyCondition: ").append(toIndentedString(finishedEarlyCondition)).append("\n");
    sb.append("    pollItemGroups: ").append(toIndentedString(pollItemGroups)).append("\n");
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

