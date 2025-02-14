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
 * DashboardAgeStatisticsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardAgeStatisticsResponse {

  private String type;

  private String title;

  private String periodType;

  private Long total;

  private DashboardStatisticsItemResponse age10;

  private DashboardStatisticsItemResponse age20;

  private DashboardStatisticsItemResponse age30;

  private DashboardStatisticsItemResponse age40;

  private DashboardStatisticsItemResponse age50;

  private DashboardStatisticsItemResponse age60;

  private DashboardStatisticsItemResponse age70;

  public DashboardAgeStatisticsResponse type(String type) {
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

  public DashboardAgeStatisticsResponse title(String title) {
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

  public DashboardAgeStatisticsResponse periodType(String periodType) {
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

  public DashboardAgeStatisticsResponse total(Long total) {
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

  public DashboardAgeStatisticsResponse age10(DashboardStatisticsItemResponse age10) {
    this.age10 = age10;
    return this;
  }

  /**
   * Get age10
   * @return age10
  */
  @Valid 
  @JsonProperty("age10")
  public DashboardStatisticsItemResponse getAge10() {
    return age10;
  }

  public void setAge10(DashboardStatisticsItemResponse age10) {
    this.age10 = age10;
  }

  public DashboardAgeStatisticsResponse age20(DashboardStatisticsItemResponse age20) {
    this.age20 = age20;
    return this;
  }

  /**
   * Get age20
   * @return age20
  */
  @Valid 
  @JsonProperty("age20")
  public DashboardStatisticsItemResponse getAge20() {
    return age20;
  }

  public void setAge20(DashboardStatisticsItemResponse age20) {
    this.age20 = age20;
  }

  public DashboardAgeStatisticsResponse age30(DashboardStatisticsItemResponse age30) {
    this.age30 = age30;
    return this;
  }

  /**
   * Get age30
   * @return age30
  */
  @Valid 
  @JsonProperty("age30")
  public DashboardStatisticsItemResponse getAge30() {
    return age30;
  }

  public void setAge30(DashboardStatisticsItemResponse age30) {
    this.age30 = age30;
  }

  public DashboardAgeStatisticsResponse age40(DashboardStatisticsItemResponse age40) {
    this.age40 = age40;
    return this;
  }

  /**
   * Get age40
   * @return age40
  */
  @Valid 
  @JsonProperty("age40")
  public DashboardStatisticsItemResponse getAge40() {
    return age40;
  }

  public void setAge40(DashboardStatisticsItemResponse age40) {
    this.age40 = age40;
  }

  public DashboardAgeStatisticsResponse age50(DashboardStatisticsItemResponse age50) {
    this.age50 = age50;
    return this;
  }

  /**
   * Get age50
   * @return age50
  */
  @Valid 
  @JsonProperty("age50")
  public DashboardStatisticsItemResponse getAge50() {
    return age50;
  }

  public void setAge50(DashboardStatisticsItemResponse age50) {
    this.age50 = age50;
  }

  public DashboardAgeStatisticsResponse age60(DashboardStatisticsItemResponse age60) {
    this.age60 = age60;
    return this;
  }

  /**
   * Get age60
   * @return age60
  */
  @Valid 
  @JsonProperty("age60")
  public DashboardStatisticsItemResponse getAge60() {
    return age60;
  }

  public void setAge60(DashboardStatisticsItemResponse age60) {
    this.age60 = age60;
  }

  public DashboardAgeStatisticsResponse age70(DashboardStatisticsItemResponse age70) {
    this.age70 = age70;
    return this;
  }

  /**
   * Get age70
   * @return age70
  */
  @Valid 
  @JsonProperty("age70")
  public DashboardStatisticsItemResponse getAge70() {
    return age70;
  }

  public void setAge70(DashboardStatisticsItemResponse age70) {
    this.age70 = age70;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardAgeStatisticsResponse dashboardAgeStatisticsResponse = (DashboardAgeStatisticsResponse) o;
    return Objects.equals(this.type, dashboardAgeStatisticsResponse.type) &&
        Objects.equals(this.title, dashboardAgeStatisticsResponse.title) &&
        Objects.equals(this.periodType, dashboardAgeStatisticsResponse.periodType) &&
        Objects.equals(this.total, dashboardAgeStatisticsResponse.total) &&
        Objects.equals(this.age10, dashboardAgeStatisticsResponse.age10) &&
        Objects.equals(this.age20, dashboardAgeStatisticsResponse.age20) &&
        Objects.equals(this.age30, dashboardAgeStatisticsResponse.age30) &&
        Objects.equals(this.age40, dashboardAgeStatisticsResponse.age40) &&
        Objects.equals(this.age50, dashboardAgeStatisticsResponse.age50) &&
        Objects.equals(this.age60, dashboardAgeStatisticsResponse.age60) &&
        Objects.equals(this.age70, dashboardAgeStatisticsResponse.age70);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, periodType, total, age10, age20, age30, age40, age50, age60, age70);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardAgeStatisticsResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    periodType: ").append(toIndentedString(periodType)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    age10: ").append(toIndentedString(age10)).append("\n");
    sb.append("    age20: ").append(toIndentedString(age20)).append("\n");
    sb.append("    age30: ").append(toIndentedString(age30)).append("\n");
    sb.append("    age40: ").append(toIndentedString(age40)).append("\n");
    sb.append("    age50: ").append(toIndentedString(age50)).append("\n");
    sb.append("    age60: ").append(toIndentedString(age60)).append("\n");
    sb.append("    age70: ").append(toIndentedString(age70)).append("\n");
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

