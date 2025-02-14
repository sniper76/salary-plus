package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardResponse;
import ag.act.model.LeaderElectionDetailResponse;
import ag.act.model.LeaderElectionFeatureActiveConditionResponse;
import ag.act.model.SimpleStockResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.StockHomeLeaderResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.model.StockNoticeResponse;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
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
 * StockHomeResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockHomeResponse {

  private SimpleStockResponse stock;

  private DashboardResponse dashboard;

  private StockHomeLeaderResponse leader;

  private SolidarityLeaderApplicationResponse leaderApplication;

  @Valid
  private List<@Valid StockHomeSectionResponse> sections;

  @Valid
  private List<@Valid StockNoticeResponse> notices;

  private LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveCondition;

  private LeaderElectionDetailResponse leaderElectionDetail;

  private UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus;

  public StockHomeResponse stock(SimpleStockResponse stock) {
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

  public StockHomeResponse dashboard(DashboardResponse dashboard) {
    this.dashboard = dashboard;
    return this;
  }

  /**
   * Get dashboard
   * @return dashboard
  */
  @Valid 
  @JsonProperty("dashboard")
  public DashboardResponse getDashboard() {
    return dashboard;
  }

  public void setDashboard(DashboardResponse dashboard) {
    this.dashboard = dashboard;
  }

  public StockHomeResponse leader(StockHomeLeaderResponse leader) {
    this.leader = leader;
    return this;
  }

  /**
   * Get leader
   * @return leader
  */
  @Valid 
  @JsonProperty("leader")
  public StockHomeLeaderResponse getLeader() {
    return leader;
  }

  public void setLeader(StockHomeLeaderResponse leader) {
    this.leader = leader;
  }

  public StockHomeResponse leaderApplication(SolidarityLeaderApplicationResponse leaderApplication) {
    this.leaderApplication = leaderApplication;
    return this;
  }

  /**
   * Get leaderApplication
   * @return leaderApplication
  */
  @Valid 
  @JsonProperty("leaderApplication")
  public SolidarityLeaderApplicationResponse getLeaderApplication() {
    return leaderApplication;
  }

  public void setLeaderApplication(SolidarityLeaderApplicationResponse leaderApplication) {
    this.leaderApplication = leaderApplication;
  }

  public StockHomeResponse sections(List<@Valid StockHomeSectionResponse> sections) {
    this.sections = sections;
    return this;
  }

  public StockHomeResponse addSectionsItem(StockHomeSectionResponse sectionsItem) {
    if (this.sections == null) {
      this.sections = new ArrayList<>();
    }
    this.sections.add(sectionsItem);
    return this;
  }

  /**
   * Sections for preview posts
   * @return sections
  */
  @Valid 
  @JsonProperty("sections")
  public List<@Valid StockHomeSectionResponse> getSections() {
    return sections;
  }

  public void setSections(List<@Valid StockHomeSectionResponse> sections) {
    this.sections = sections;
  }

  public StockHomeResponse notices(List<@Valid StockNoticeResponse> notices) {
    this.notices = notices;
    return this;
  }

  public StockHomeResponse addNoticesItem(StockNoticeResponse noticesItem) {
    if (this.notices == null) {
      this.notices = new ArrayList<>();
    }
    this.notices.add(noticesItem);
    return this;
  }

  /**
   * Notices for the stock
   * @return notices
  */
  @Valid 
  @JsonProperty("notices")
  public List<@Valid StockNoticeResponse> getNotices() {
    return notices;
  }

  public void setNotices(List<@Valid StockNoticeResponse> notices) {
    this.notices = notices;
  }

  public StockHomeResponse leaderElectionFeatureActiveCondition(LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveCondition) {
    this.leaderElectionFeatureActiveCondition = leaderElectionFeatureActiveCondition;
    return this;
  }

  /**
   * Get leaderElectionFeatureActiveCondition
   * @return leaderElectionFeatureActiveCondition
  */
  @Valid 
  @JsonProperty("leaderElectionFeatureActiveCondition")
  public LeaderElectionFeatureActiveConditionResponse getLeaderElectionFeatureActiveCondition() {
    return leaderElectionFeatureActiveCondition;
  }

  public void setLeaderElectionFeatureActiveCondition(LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveCondition) {
    this.leaderElectionFeatureActiveCondition = leaderElectionFeatureActiveCondition;
  }

  public StockHomeResponse leaderElectionDetail(LeaderElectionDetailResponse leaderElectionDetail) {
    this.leaderElectionDetail = leaderElectionDetail;
    return this;
  }

  /**
   * Get leaderElectionDetail
   * @return leaderElectionDetail
  */
  @Valid 
  @JsonProperty("leaderElectionDetail")
  public LeaderElectionDetailResponse getLeaderElectionDetail() {
    return leaderElectionDetail;
  }

  public void setLeaderElectionDetail(LeaderElectionDetailResponse leaderElectionDetail) {
    this.leaderElectionDetail = leaderElectionDetail;
  }

  public StockHomeResponse unreadStockBoardGroupPostStatus(UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus) {
    this.unreadStockBoardGroupPostStatus = unreadStockBoardGroupPostStatus;
    return this;
  }

  /**
   * Get unreadStockBoardGroupPostStatus
   * @return unreadStockBoardGroupPostStatus
  */
  @Valid 
  @JsonProperty("unreadStockBoardGroupPostStatus")
  public UnreadStockBoardGroupPostStatusResponse getUnreadStockBoardGroupPostStatus() {
    return unreadStockBoardGroupPostStatus;
  }

  public void setUnreadStockBoardGroupPostStatus(UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus) {
    this.unreadStockBoardGroupPostStatus = unreadStockBoardGroupPostStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockHomeResponse stockHomeResponse = (StockHomeResponse) o;
    return Objects.equals(this.stock, stockHomeResponse.stock) &&
        Objects.equals(this.dashboard, stockHomeResponse.dashboard) &&
        Objects.equals(this.leader, stockHomeResponse.leader) &&
        Objects.equals(this.leaderApplication, stockHomeResponse.leaderApplication) &&
        Objects.equals(this.sections, stockHomeResponse.sections) &&
        Objects.equals(this.notices, stockHomeResponse.notices) &&
        Objects.equals(this.leaderElectionFeatureActiveCondition, stockHomeResponse.leaderElectionFeatureActiveCondition) &&
        Objects.equals(this.leaderElectionDetail, stockHomeResponse.leaderElectionDetail) &&
        Objects.equals(this.unreadStockBoardGroupPostStatus, stockHomeResponse.unreadStockBoardGroupPostStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stock, dashboard, leader, leaderApplication, sections, notices, leaderElectionFeatureActiveCondition, leaderElectionDetail, unreadStockBoardGroupPostStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockHomeResponse {\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    dashboard: ").append(toIndentedString(dashboard)).append("\n");
    sb.append("    leader: ").append(toIndentedString(leader)).append("\n");
    sb.append("    leaderApplication: ").append(toIndentedString(leaderApplication)).append("\n");
    sb.append("    sections: ").append(toIndentedString(sections)).append("\n");
    sb.append("    notices: ").append(toIndentedString(notices)).append("\n");
    sb.append("    leaderElectionFeatureActiveCondition: ").append(toIndentedString(leaderElectionFeatureActiveCondition)).append("\n");
    sb.append("    leaderElectionDetail: ").append(toIndentedString(leaderElectionDetail)).append("\n");
    sb.append("    unreadStockBoardGroupPostStatus: ").append(toIndentedString(unreadStockBoardGroupPostStatus)).append("\n");
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

