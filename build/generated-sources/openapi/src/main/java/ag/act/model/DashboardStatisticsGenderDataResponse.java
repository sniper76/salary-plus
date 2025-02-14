package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardGenderStatisticsResponse;
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
 * DashboardStatisticsGenderDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardStatisticsGenderDataResponse {

  private DashboardStatisticsDataResponseSearch search;

  private DashboardGenderStatisticsResponse data;

  public DashboardStatisticsGenderDataResponse search(DashboardStatisticsDataResponseSearch search) {
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

  public DashboardStatisticsGenderDataResponse data(DashboardGenderStatisticsResponse data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @Valid 
  @JsonProperty("data")
  public DashboardGenderStatisticsResponse getData() {
    return data;
  }

  public void setData(DashboardGenderStatisticsResponse data) {
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
    DashboardStatisticsGenderDataResponse dashboardStatisticsGenderDataResponse = (DashboardStatisticsGenderDataResponse) o;
    return Objects.equals(this.search, dashboardStatisticsGenderDataResponse.search) &&
        Objects.equals(this.data, dashboardStatisticsGenderDataResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(search, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardStatisticsGenderDataResponse {\n");
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

