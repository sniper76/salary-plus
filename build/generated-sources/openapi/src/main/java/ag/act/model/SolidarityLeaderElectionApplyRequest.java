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
 * SolidarityLeaderElectionApplyRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionApplyRequest {

  @NotBlank(message = "제출 상태를 확인해주세요.")

  private String applyStatus;

  private String reasonsForApply;

  private String knowledgeOfCompanyManagement;

  private String goals;

  private String commentsForStockHolder;

  public SolidarityLeaderElectionApplyRequest applyStatus(String applyStatus) {
    this.applyStatus = applyStatus;
    return this;
  }

  /**
   * SAVE or COMPLETE
   * @return applyStatus
  */
  
  @JsonProperty("applyStatus")
  public String getApplyStatus() {
    return applyStatus;
  }

  public void setApplyStatus(String applyStatus) {
    this.applyStatus = applyStatus;
  }

  public SolidarityLeaderElectionApplyRequest reasonsForApply(String reasonsForApply) {
    this.reasonsForApply = reasonsForApply;
    return this;
  }

  /**
   * 지원 동기
   * @return reasonsForApply
  */
  
  @JsonProperty("reasonsForApply")
  public String getReasonsForApply() {
    return reasonsForApply;
  }

  public void setReasonsForApply(String reasonsForApply) {
    this.reasonsForApply = reasonsForApply;
  }

  public SolidarityLeaderElectionApplyRequest knowledgeOfCompanyManagement(String knowledgeOfCompanyManagement) {
    this.knowledgeOfCompanyManagement = knowledgeOfCompanyManagement;
    return this;
  }

  /**
   * 현기업 경영에 대한 지식
   * @return knowledgeOfCompanyManagement
  */
  
  @JsonProperty("knowledgeOfCompanyManagement")
  public String getKnowledgeOfCompanyManagement() {
    return knowledgeOfCompanyManagement;
  }

  public void setKnowledgeOfCompanyManagement(String knowledgeOfCompanyManagement) {
    this.knowledgeOfCompanyManagement = knowledgeOfCompanyManagement;
  }

  public SolidarityLeaderElectionApplyRequest goals(String goals) {
    this.goals = goals;
    return this;
  }

  /**
   * 주주행동 운영 목표 및 간단한 계획
   * @return goals
  */
  
  @JsonProperty("goals")
  public String getGoals() {
    return goals;
  }

  public void setGoals(String goals) {
    this.goals = goals;
  }

  public SolidarityLeaderElectionApplyRequest commentsForStockHolder(String commentsForStockHolder) {
    this.commentsForStockHolder = commentsForStockHolder;
    return this;
  }

  /**
   * 주주에게 전하고 싶은 말
   * @return commentsForStockHolder
  */
  
  @JsonProperty("commentsForStockHolder")
  public String getCommentsForStockHolder() {
    return commentsForStockHolder;
  }

  public void setCommentsForStockHolder(String commentsForStockHolder) {
    this.commentsForStockHolder = commentsForStockHolder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionApplyRequest solidarityLeaderElectionApplyRequest = (SolidarityLeaderElectionApplyRequest) o;
    return Objects.equals(this.applyStatus, solidarityLeaderElectionApplyRequest.applyStatus) &&
        Objects.equals(this.reasonsForApply, solidarityLeaderElectionApplyRequest.reasonsForApply) &&
        Objects.equals(this.knowledgeOfCompanyManagement, solidarityLeaderElectionApplyRequest.knowledgeOfCompanyManagement) &&
        Objects.equals(this.goals, solidarityLeaderElectionApplyRequest.goals) &&
        Objects.equals(this.commentsForStockHolder, solidarityLeaderElectionApplyRequest.commentsForStockHolder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applyStatus, reasonsForApply, knowledgeOfCompanyManagement, goals, commentsForStockHolder);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionApplyRequest {\n");
    sb.append("    applyStatus: ").append(toIndentedString(applyStatus)).append("\n");
    sb.append("    reasonsForApply: ").append(toIndentedString(reasonsForApply)).append("\n");
    sb.append("    knowledgeOfCompanyManagement: ").append(toIndentedString(knowledgeOfCompanyManagement)).append("\n");
    sb.append("    goals: ").append(toIndentedString(goals)).append("\n");
    sb.append("    commentsForStockHolder: ").append(toIndentedString(commentsForStockHolder)).append("\n");
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

