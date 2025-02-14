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
 * DashboardStatisticsItemResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsItemResponse {

  private String title;

  private Long value;

  private String percent;

  private String upDown;

  private String upDownText;

  private String upDownPercent;

  public DashboardStatisticsItemResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DashboardStatisticsItemResponse value(Long value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  
  @JsonProperty("value")
  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  public DashboardStatisticsItemResponse percent(String percent) {
    this.percent = percent;
    return this;
  }

  /**
   * Get percent
   * @return percent
  */
  
  @JsonProperty("percent")
  public String getPercent() {
    return percent;
  }

  public void setPercent(String percent) {
    this.percent = percent;
  }

  public DashboardStatisticsItemResponse upDown(String upDown) {
    this.upDown = upDown;
    return this;
  }

  /**
   * Up, Down, - 로 구분
   * @return upDown
  */
  
  @JsonProperty("upDown")
  public String getUpDown() {
    return upDown;
  }

  public void setUpDown(String upDown) {
    this.upDown = upDown;
  }

  public DashboardStatisticsItemResponse upDownText(String upDownText) {
    this.upDownText = upDownText;
    return this;
  }

  /**
   * 전일대비 전월대비
   * @return upDownText
  */
  
  @JsonProperty("upDownText")
  public String getUpDownText() {
    return upDownText;
  }

  public void setUpDownText(String upDownText) {
    this.upDownText = upDownText;
  }

  public DashboardStatisticsItemResponse upDownPercent(String upDownPercent) {
    this.upDownPercent = upDownPercent;
    return this;
  }

  /**
   * 12.5%
   * @return upDownPercent
  */
  
  @JsonProperty("upDownPercent")
  public String getUpDownPercent() {
    return upDownPercent;
  }

  public void setUpDownPercent(String upDownPercent) {
    this.upDownPercent = upDownPercent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardStatisticsItemResponse dashboardStatisticsItemResponse = (DashboardStatisticsItemResponse) o;
    return Objects.equals(this.title, dashboardStatisticsItemResponse.title) &&
        Objects.equals(this.value, dashboardStatisticsItemResponse.value) &&
        Objects.equals(this.percent, dashboardStatisticsItemResponse.percent) &&
        Objects.equals(this.upDown, dashboardStatisticsItemResponse.upDown) &&
        Objects.equals(this.upDownText, dashboardStatisticsItemResponse.upDownText) &&
        Objects.equals(this.upDownPercent, dashboardStatisticsItemResponse.upDownPercent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, value, percent, upDown, upDownText, upDownPercent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsItemResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    percent: ").append(toIndentedString(percent)).append("\n");
    sb.append("    upDown: ").append(toIndentedString(upDown)).append("\n");
    sb.append("    upDownText: ").append(toIndentedString(upDownText)).append("\n");
    sb.append("    upDownPercent: ").append(toIndentedString(upDownPercent)).append("\n");
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

