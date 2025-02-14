package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardStatisticsResponseItemsInner;
import ag.act.model.DashboardStatisticsResponseSummary;
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
 * DashboardStatisticsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsResponse {

  private String type;

  private String title;

  private java.math.BigDecimal value;

  private String periodType;

  private DashboardStatisticsResponseSummary summary;

  @Valid
  private List<@Valid DashboardStatisticsResponseItemsInner> items;

  public DashboardStatisticsResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * DashboardStatisticsType enum
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public DashboardStatisticsResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * DashboardStatisticsType title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DashboardStatisticsResponse value(java.math.BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @Valid 
  @JsonProperty("value")
  public java.math.BigDecimal getValue() {
    return value;
  }

  public void setValue(java.math.BigDecimal value) {
    this.value = value;
  }

  public DashboardStatisticsResponse periodType(String periodType) {
    this.periodType = periodType;
    return this;
  }

  /**
   * DAILY or MONTHLY
   * @return periodType
  */
  
  @JsonProperty("periodType")
  public String getPeriodType() {
    return periodType;
  }

  public void setPeriodType(String periodType) {
    this.periodType = periodType;
  }

  public DashboardStatisticsResponse summary(DashboardStatisticsResponseSummary summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get summary
   * @return summary
  */
  @Valid 
  @JsonProperty("summary")
  public DashboardStatisticsResponseSummary getSummary() {
    return summary;
  }

  public void setSummary(DashboardStatisticsResponseSummary summary) {
    this.summary = summary;
  }

  public DashboardStatisticsResponse items(List<@Valid DashboardStatisticsResponseItemsInner> items) {
    this.items = items;
    return this;
  }

  public DashboardStatisticsResponse addItemsItem(DashboardStatisticsResponseItemsInner itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid 
  @JsonProperty("items")
  public List<@Valid DashboardStatisticsResponseItemsInner> getItems() {
    return items;
  }

  public void setItems(List<@Valid DashboardStatisticsResponseItemsInner> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardStatisticsResponse dashboardStatisticsResponse = (DashboardStatisticsResponse) o;
    return Objects.equals(this.type, dashboardStatisticsResponse.type) &&
        Objects.equals(this.title, dashboardStatisticsResponse.title) &&
        Objects.equals(this.value, dashboardStatisticsResponse.value) &&
        Objects.equals(this.periodType, dashboardStatisticsResponse.periodType) &&
        Objects.equals(this.summary, dashboardStatisticsResponse.summary) &&
        Objects.equals(this.items, dashboardStatisticsResponse.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, value, periodType, summary, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    periodType: ").append(toIndentedString(periodType)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

