package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardAgeStatisticsResponse;
import ag.act.model.DashboardStatisticsDataResponseSearch;
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
 * DashboardStatisticsAgeDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsAgeDataResponse {

  private DashboardStatisticsDataResponseSearch search;

  private DashboardAgeStatisticsResponse data;

  public DashboardStatisticsAgeDataResponse search(DashboardStatisticsDataResponseSearch search) {
    this.search = search;
    return this;
  }

  /**
   * Get search
   * @return search
  */
  @Valid 
  @JsonProperty("search")
  public DashboardStatisticsDataResponseSearch getSearch() {
    return search;
  }

  public void setSearch(DashboardStatisticsDataResponseSearch search) {
    this.search = search;
  }

  public DashboardStatisticsAgeDataResponse data(DashboardAgeStatisticsResponse data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @Valid 
  @JsonProperty("data")
  public DashboardAgeStatisticsResponse getData() {
    return data;
  }

  public void setData(DashboardAgeStatisticsResponse data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardStatisticsAgeDataResponse dashboardStatisticsAgeDataResponse = (DashboardStatisticsAgeDataResponse) o;
    return Objects.equals(this.search, dashboardStatisticsAgeDataResponse.search) &&
        Objects.equals(this.data, dashboardStatisticsAgeDataResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(search, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsAgeDataResponse {\n");
    sb.append("    search: ").append(toIndentedString(search)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

