package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardResponse;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderResponse;
import ag.act.model.SolidarityResponse;
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
 * StockDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockDetailsResponse {

  private SolidarityResponse solidarity;

  private DashboardResponse todayDelta;

  private SolidarityLeaderResponse solidarityLeader;

  private DigitalDocumentAcceptUserResponse acceptUser;

  @Valid
  private List<@Valid SolidarityLeaderApplicantResponse> solidarityLeaderApplicants;

  public StockDetailsResponse solidarity(SolidarityResponse solidarity) {
    this.solidarity = solidarity;
    return this;
  }

  /**
   * Get solidarity
   * @return solidarity
  */
  @Valid 
  @JsonProperty("solidarity")
  public SolidarityResponse getSolidarity() {
    return solidarity;
  }

  public void setSolidarity(SolidarityResponse solidarity) {
    this.solidarity = solidarity;
  }

  public StockDetailsResponse todayDelta(DashboardResponse todayDelta) {
    this.todayDelta = todayDelta;
    return this;
  }

  /**
   * Get todayDelta
   * @return todayDelta
  */
  @Valid 
  @JsonProperty("todayDelta")
  public DashboardResponse getTodayDelta() {
    return todayDelta;
  }

  public void setTodayDelta(DashboardResponse todayDelta) {
    this.todayDelta = todayDelta;
  }

  public StockDetailsResponse solidarityLeader(SolidarityLeaderResponse solidarityLeader) {
    this.solidarityLeader = solidarityLeader;
    return this;
  }

  /**
   * Get solidarityLeader
   * @return solidarityLeader
  */
  @Valid 
  @JsonProperty("solidarityLeader")
  public SolidarityLeaderResponse getSolidarityLeader() {
    return solidarityLeader;
  }

  public void setSolidarityLeader(SolidarityLeaderResponse solidarityLeader) {
    this.solidarityLeader = solidarityLeader;
  }

  public StockDetailsResponse acceptUser(DigitalDocumentAcceptUserResponse acceptUser) {
    this.acceptUser = acceptUser;
    return this;
  }

  /**
   * Get acceptUser
   * @return acceptUser
  */
  @Valid 
  @JsonProperty("acceptUser")
  public DigitalDocumentAcceptUserResponse getAcceptUser() {
    return acceptUser;
  }

  public void setAcceptUser(DigitalDocumentAcceptUserResponse acceptUser) {
    this.acceptUser = acceptUser;
  }

  public StockDetailsResponse solidarityLeaderApplicants(List<@Valid SolidarityLeaderApplicantResponse> solidarityLeaderApplicants) {
    this.solidarityLeaderApplicants = solidarityLeaderApplicants;
    return this;
  }

  public StockDetailsResponse addSolidarityLeaderApplicantsItem(SolidarityLeaderApplicantResponse solidarityLeaderApplicantsItem) {
    if (this.solidarityLeaderApplicants == null) {
      this.solidarityLeaderApplicants = new ArrayList<>();
    }
    this.solidarityLeaderApplicants.add(solidarityLeaderApplicantsItem);
    return this;
  }

  /**
   * Get solidarityLeaderApplicants
   * @return solidarityLeaderApplicants
  */
  @Valid 
  @JsonProperty("solidarityLeaderApplicants")
  public List<@Valid SolidarityLeaderApplicantResponse> getSolidarityLeaderApplicants() {
    return solidarityLeaderApplicants;
  }

  public void setSolidarityLeaderApplicants(List<@Valid SolidarityLeaderApplicantResponse> solidarityLeaderApplicants) {
    this.solidarityLeaderApplicants = solidarityLeaderApplicants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockDetailsResponse stockDetailsResponse = (StockDetailsResponse) o;
    return Objects.equals(this.solidarity, stockDetailsResponse.solidarity) &&
        Objects.equals(this.todayDelta, stockDetailsResponse.todayDelta) &&
        Objects.equals(this.solidarityLeader, stockDetailsResponse.solidarityLeader) &&
        Objects.equals(this.acceptUser, stockDetailsResponse.acceptUser) &&
        Objects.equals(this.solidarityLeaderApplicants, stockDetailsResponse.solidarityLeaderApplicants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarity, todayDelta, solidarityLeader, acceptUser, solidarityLeaderApplicants);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockDetailsResponse {\n");
    sb.append("    solidarity: ").append(toIndentedString(solidarity)).append("\n");
    sb.append("    todayDelta: ").append(toIndentedString(todayDelta)).append("\n");
    sb.append("    solidarityLeader: ").append(toIndentedString(solidarityLeader)).append("\n");
    sb.append("    acceptUser: ").append(toIndentedString(acceptUser)).append("\n");
    sb.append("    solidarityLeaderApplicants: ").append(toIndentedString(solidarityLeaderApplicants)).append("\n");
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

