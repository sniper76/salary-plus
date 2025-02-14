package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DashboardItemResponse;
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
 * DashboardResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DashboardResponse {

  private String descriptionLabel;

  @Valid
  private List<@Valid DashboardItemResponse> items;

  public DashboardResponse descriptionLabel(String descriptionLabel) {
    this.descriptionLabel = descriptionLabel;
    return this;
  }

  /**
   * Get descriptionLabel
   * @return descriptionLabel
  */
  
  @JsonProperty("descriptionLabel")
  public String getDescriptionLabel() {
    return descriptionLabel;
  }

  public void setDescriptionLabel(String descriptionLabel) {
    this.descriptionLabel = descriptionLabel;
  }

  public DashboardResponse items(List<@Valid DashboardItemResponse> items) {
    this.items = items;
    return this;
  }

  public DashboardResponse addItemsItem(DashboardItemResponse itemsItem) {
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
  public List<@Valid DashboardItemResponse> getItems() {
    return items;
  }

  public void setItems(List<@Valid DashboardItemResponse> items) {
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
    DashboardResponse dashboardResponse = (DashboardResponse) o;
    return Objects.equals(this.descriptionLabel, dashboardResponse.descriptionLabel) &&
        Objects.equals(this.items, dashboardResponse.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptionLabel, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardResponse {\n");
    sb.append("    descriptionLabel: ").append(toIndentedString(descriptionLabel)).append("\n");
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

