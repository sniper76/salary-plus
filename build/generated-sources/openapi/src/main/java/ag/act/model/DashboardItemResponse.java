package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardItemResponseVariation;
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
 * DashboardItemResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardItemResponse {

  private String title;

  private String value;

  private DashboardItemResponseVariation variation;

  public DashboardItemResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the item
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DashboardItemResponse value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Value of the item
   * @return value
  */
  
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DashboardItemResponse variation(DashboardItemResponseVariation variation) {
    this.variation = variation;
    return this;
  }

  /**
   * Get variation
   * @return variation
  */
  @Valid 
  @JsonProperty("variation")
  public DashboardItemResponseVariation getVariation() {
    return variation;
  }

  public void setVariation(DashboardItemResponseVariation variation) {
    this.variation = variation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardItemResponse dashboardItemResponse = (DashboardItemResponse) o;
    return Objects.equals(this.title, dashboardItemResponse.title) &&
        Objects.equals(this.value, dashboardItemResponse.value) &&
        Objects.equals(this.variation, dashboardItemResponse.variation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, value, variation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardItemResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    variation: ").append(toIndentedString(variation)).append("\n");
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

