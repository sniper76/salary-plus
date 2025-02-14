package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.BoardCategoryResponse;
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
 * SimpleBoardGroupResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SimpleBoardGroupResponse {

  private String name;

  private String displayName;

  @Valid
  private List<@Valid BoardCategoryResponse> categories;

  public SimpleBoardGroupResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SimpleBoardGroupResponse displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  */
  
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SimpleBoardGroupResponse categories(List<@Valid BoardCategoryResponse> categories) {
    this.categories = categories;
    return this;
  }

  public SimpleBoardGroupResponse addCategoriesItem(BoardCategoryResponse categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * Get categories
   * @return categories
  */
  @Valid 
  @JsonProperty("categories")
  public List<@Valid BoardCategoryResponse> getCategories() {
    return categories;
  }

  public void setCategories(List<@Valid BoardCategoryResponse> categories) {
    this.categories = categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleBoardGroupResponse simpleBoardGroupResponse = (SimpleBoardGroupResponse) o;
    return Objects.equals(this.name, simpleBoardGroupResponse.name) &&
        Objects.equals(this.displayName, simpleBoardGroupResponse.displayName) &&
        Objects.equals(this.categories, simpleBoardGroupResponse.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, displayName, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimpleBoardGroupResponse {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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

