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
 * DashboardStatisticsDataResponseSearch
 */

@JsonTypeName("DashboardStatisticsDataResponse_search")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsDataResponseSearch {

  private Integer period;

  private String from;

  private String to;

  public DashboardStatisticsDataResponseSearch period(Integer period) {
    this.period = period;
    return this;
  }

  /**
   * Get period
   * @return period
  */
  
  @JsonProperty("period")
  public Integer getPeriod() {
    return period;
  }

  public void setPeriod(Integer period) {
    this.period = period;
  }

  public DashboardStatisticsDataResponseSearch from(String from) {
    this.from = from;
    return this;
  }

  /**
   * 월별 2023-10 or 일별 2023-10-01
   * @return from
  */
  
  @JsonProperty("from")
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public DashboardStatisticsDataResponseSearch to(String to) {
    this.to = to;
    return this;
  }

  /**
   * 월별 2023-10 or 일별 2023-10-01
   * @return to
  */
  
  @JsonProperty("to")
  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardStatisticsDataResponseSearch dashboardStatisticsDataResponseSearch = (DashboardStatisticsDataResponseSearch) o;
    return Objects.equals(this.period, dashboardStatisticsDataResponseSearch.period) &&
        Objects.equals(this.from, dashboardStatisticsDataResponseSearch.from) &&
        Objects.equals(this.to, dashboardStatisticsDataResponseSearch.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(period, from, to);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsDataResponseSearch {\n");
    sb.append("    period: ").append(toIndentedString(period)).append("\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

