package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SimpleStockResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
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
 * SolidarityLeaderApplicationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderApplicationResponse {

  private SolidarityLeaderApplicantResponse user;

  private SimpleStockResponse stock;

  private Long solidarityLeaderElectionId;

  private Long solidarityLeaderApplicantId;

  private String applyStatus;

  private String reasonsForApply;

  private String knowledgeOfCompanyManagement;

  private String goals;

  private String commentsForStockHolder;

  public SolidarityLeaderApplicationResponse user(SolidarityLeaderApplicantResponse user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
  */
  @Valid 
  @JsonProperty("user")
  public SolidarityLeaderApplicantResponse getUser() {
    return user;
  }

  public void setUser(SolidarityLeaderApplicantResponse user) {
    this.user = user;
  }

  public SolidarityLeaderApplicationResponse stock(SimpleStockResponse stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * @return stock
  */
  @Valid 
  @JsonProperty("stock")
  public SimpleStockResponse getStock() {
    return stock;
  }

  public void setStock(SimpleStockResponse stock) {
    this.stock = stock;
  }

  public SolidarityLeaderApplicationResponse solidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
    return this;
  }

  /**
   * Get solidarityLeaderElectionId
   * @return solidarityLeaderElectionId
  */
  
  @JsonProperty("solidarityLeaderElectionId")
  public Long getSolidarityLeaderElectionId() {
    return solidarityLeaderElectionId;
  }

  public void setSolidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
  }

  public SolidarityLeaderApplicationResponse solidarityLeaderApplicantId(Long solidarityLeaderApplicantId) {
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

  public SolidarityLeaderApplicationResponse applyStatus(String applyStatus) {
    this.applyStatus = applyStatus;
    return this;
  }

  /**
   * Get applyStatus
   * @return applyStatus
  */
  
  @JsonProperty("applyStatus")
  public String getApplyStatus() {
    return applyStatus;
  }

  public void setApplyStatus(String applyStatus) {
    this.applyStatus = applyStatus;
  }

  public SolidarityLeaderApplicationResponse reasonsForApply(String reasonsForApply) {
    this.reasonsForApply = reasonsForApply;
    return this;
  }

  /**
   * Get reasonsForApply
   * @return reasonsForApply
  */
  
  @JsonProperty("reasonsForApply")
  public String getReasonsForApply() {
    return reasonsForApply;
  }

  public void setReasonsForApply(String reasonsForApply) {
    this.reasonsForApply = reasonsForApply;
  }

  public SolidarityLeaderApplicationResponse knowledgeOfCompanyManagement(String knowledgeOfCompanyManagement) {
    this.knowledgeOfCompanyManagement = knowledgeOfCompanyManagement;
    return this;
  }

  /**
   * Get knowledgeOfCompanyManagement
   * @return knowledgeOfCompanyManagement
  */
  
  @JsonProperty("knowledgeOfCompanyManagement")
  public String getKnowledgeOfCompanyManagement() {
    return knowledgeOfCompanyManagement;
  }

  public void setKnowledgeOfCompanyManagement(String knowledgeOfCompanyManagement) {
    this.knowledgeOfCompanyManagement = knowledgeOfCompanyManagement;
  }

  public SolidarityLeaderApplicationResponse goals(String goals) {
    this.goals = goals;
    return this;
  }

  /**
   * Get goals
   * @return goals
  */
  
  @JsonProperty("goals")
  public String getGoals() {
    return goals;
  }

  public void setGoals(String goals) {
    this.goals = goals;
  }

  public SolidarityLeaderApplicationResponse commentsForStockHolder(String commentsForStockHolder) {
    this.commentsForStockHolder = commentsForStockHolder;
    return this;
  }

  /**
   * Get commentsForStockHolder
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
    SolidarityLeaderApplicationResponse solidarityLeaderApplicationResponse = (SolidarityLeaderApplicationResponse) o;
    return Objects.equals(this.user, solidarityLeaderApplicationResponse.user) &&
        Objects.equals(this.stock, solidarityLeaderApplicationResponse.stock) &&
        Objects.equals(this.solidarityLeaderElectionId, solidarityLeaderApplicationResponse.solidarityLeaderElectionId) &&
        Objects.equals(this.solidarityLeaderApplicantId, solidarityLeaderApplicationResponse.solidarityLeaderApplicantId) &&
        Objects.equals(this.applyStatus, solidarityLeaderApplicationResponse.applyStatus) &&
        Objects.equals(this.reasonsForApply, solidarityLeaderApplicationResponse.reasonsForApply) &&
        Objects.equals(this.knowledgeOfCompanyManagement, solidarityLeaderApplicationResponse.knowledgeOfCompanyManagement) &&
        Objects.equals(this.goals, solidarityLeaderApplicationResponse.goals) &&
        Objects.equals(this.commentsForStockHolder, solidarityLeaderApplicationResponse.commentsForStockHolder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, stock, solidarityLeaderElectionId, solidarityLeaderApplicantId, applyStatus, reasonsForApply, knowledgeOfCompanyManagement, goals, commentsForStockHolder);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderApplicationResponse {\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    solidarityLeaderElectionId: ").append(toIndentedString(solidarityLeaderElectionId)).append("\n");
    sb.append("    solidarityLeaderApplicantId: ").append(toIndentedString(solidarityLeaderApplicantId)).append("\n");
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

