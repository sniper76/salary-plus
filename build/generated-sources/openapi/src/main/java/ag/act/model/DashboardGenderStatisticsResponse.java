package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardStatisticsItemResponse;
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
 * DashboardGenderStatisticsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardGenderStatisticsResponse {

  private String type;

  private String title;

  private String periodType;

  private Long total;

  private DashboardStatisticsItemResponse male;

  private DashboardStatisticsItemResponse female;

  public DashboardGenderStatisticsResponse type(String type) {
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

  public DashboardGenderStatisticsResponse title(String title) {
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

  public DashboardGenderStatisticsResponse periodType(String periodType) {
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

  public DashboardGenderStatisticsResponse total(Long total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
  */
  
  @JsonProperty("total")
  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public DashboardGenderStatisticsResponse male(DashboardStatisticsItemResponse male) {
    this.male = male;
    return this;
  }

  /**
   * Get male
   * @return male
  */
  @Valid 
  @JsonProperty("male")
  public DashboardStatisticsItemResponse getMale() {
    return male;
  }

  public void setMale(DashboardStatisticsItemResponse male) {
    this.male = male;
  }

  public DashboardGenderStatisticsResponse female(DashboardStatisticsItemResponse female) {
    this.female = female;
    return this;
  }

  /**
   * Get female
   * @return female
  */
  @Valid 
  @JsonProperty("female")
  public DashboardStatisticsItemResponse getFemale() {
    return female;
  }

  public void setFemale(DashboardStatisticsItemResponse female) {
    this.female = female;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardGenderStatisticsResponse dashboardGenderStatisticsResponse = (DashboardGenderStatisticsResponse) o;
    return Objects.equals(this.type, dashboardGenderStatisticsResponse.type) &&
        Objects.equals(this.title, dashboardGenderStatisticsResponse.title) &&
        Objects.equals(this.periodType, dashboardGenderStatisticsResponse.periodType) &&
        Objects.equals(this.total, dashboardGenderStatisticsResponse.total) &&
        Objects.equals(this.male, dashboardGenderStatisticsResponse.male) &&
        Objects.equals(this.female, dashboardGenderStatisticsResponse.female);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, periodType, total, male, female);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardGenderStatisticsResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    periodType: ").append(toIndentedString(periodType)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    male: ").append(toIndentedString(male)).append("\n");
    sb.append("    female: ").append(toIndentedString(female)).append("\n");
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

