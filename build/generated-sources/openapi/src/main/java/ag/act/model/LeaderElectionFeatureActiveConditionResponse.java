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
 * LeaderElectionFeatureActiveConditionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class LeaderElectionFeatureActiveConditionResponse {

  private Float minThresholdStake;

  private Float stake = 0f;

  private Long minThresholdMemberCount;

  private Long memberCount = 0l;

  private Boolean isVisible = false;

  public LeaderElectionFeatureActiveConditionResponse minThresholdStake(Float minThresholdStake) {
    this.minThresholdStake = minThresholdStake;
    return this;
  }

  /**
   * 주주연대 지분율 임계값
   * @return minThresholdStake
  */
  
  @JsonProperty("minThresholdStake")
  public Float getMinThresholdStake() {
    return minThresholdStake;
  }

  public void setMinThresholdStake(Float minThresholdStake) {
    this.minThresholdStake = minThresholdStake;
  }

  public LeaderElectionFeatureActiveConditionResponse stake(Float stake) {
    this.stake = stake;
    return this;
  }

  /**
   * 실제 주주연대 지분율
   * @return stake
  */
  
  @JsonProperty("stake")
  public Float getStake() {
    return stake;
  }

  public void setStake(Float stake) {
    this.stake = stake;
  }

  public LeaderElectionFeatureActiveConditionResponse minThresholdMemberCount(Long minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
    return this;
  }

  /**
   * 주주연대 인원수 임계값
   * @return minThresholdMemberCount
  */
  
  @JsonProperty("minThresholdMemberCount")
  public Long getMinThresholdMemberCount() {
    return minThresholdMemberCount;
  }

  public void setMinThresholdMemberCount(Long minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
  }

  public LeaderElectionFeatureActiveConditionResponse memberCount(Long memberCount) {
    this.memberCount = memberCount;
    return this;
  }

  /**
   * 실제 주주연대 인원수
   * @return memberCount
  */
  
  @JsonProperty("memberCount")
  public Long getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Long memberCount) {
    this.memberCount = memberCount;
  }

  public LeaderElectionFeatureActiveConditionResponse isVisible(Boolean isVisible) {
    this.isVisible = isVisible;
    return this;
  }

  /**
   * 주주대표 선출기능 활성화 조건 노출여부
   * @return isVisible
  */
  
  @JsonProperty("isVisible")
  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean isVisible) {
    this.isVisible = isVisible;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveConditionResponse = (LeaderElectionFeatureActiveConditionResponse) o;
    return Objects.equals(this.minThresholdStake, leaderElectionFeatureActiveConditionResponse.minThresholdStake) &&
        Objects.equals(this.stake, leaderElectionFeatureActiveConditionResponse.stake) &&
        Objects.equals(this.minThresholdMemberCount, leaderElectionFeatureActiveConditionResponse.minThresholdMemberCount) &&
        Objects.equals(this.memberCount, leaderElectionFeatureActiveConditionResponse.memberCount) &&
        Objects.equals(this.isVisible, leaderElectionFeatureActiveConditionResponse.isVisible);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minThresholdStake, stake, minThresholdMemberCount, memberCount, isVisible);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderElectionFeatureActiveConditionResponse {\n");
    sb.append("    minThresholdStake: ").append(toIndentedString(minThresholdStake)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    minThresholdMemberCount: ").append(toIndentedString(minThresholdMemberCount)).append("\n");
    sb.append("    memberCount: ").append(toIndentedString(memberCount)).append("\n");
    sb.append("    isVisible: ").append(toIndentedString(isVisible)).append("\n");
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

