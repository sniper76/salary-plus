package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * DashboardStatisticsResponseSummary
 */

@JsonTypeName("DashboardStatisticsResponse_summary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsResponseSummary {

  private String upDown;

  private String upDownText;

  private String upDownPercent;

  public DashboardStatisticsResponseSummary upDown(String upDown) {
    this.upDown = upDown;
    return this;
  }

  /**
   * Up, Down, - 구분
   * @return upDown
  */
  
  @JsonProperty("upDown")
  public String getUpDown() {
    return upDown;
  }

  public void setUpDown(String upDown) {
    this.upDown = upDown;
  }

  public DashboardStatisticsResponseSummary upDownText(String upDownText) {
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

  public DashboardStatisticsResponseSummary upDownPercent(String upDownPercent) {
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
    DashboardStatisticsResponseSummary dashboardStatisticsResponseSummary = (DashboardStatisticsResponseSummary) o;
    return Objects.equals(this.upDown, dashboardStatisticsResponseSummary.upDown) &&
        Objects.equals(this.upDownText, dashboardStatisticsResponseSummary.upDownText) &&
        Objects.equals(this.upDownPercent, dashboardStatisticsResponseSummary.upDownPercent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(upDown, upDownText, upDownPercent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsResponseSummary {\n");
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

