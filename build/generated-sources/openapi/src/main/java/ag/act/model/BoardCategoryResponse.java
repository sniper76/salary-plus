package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * BoardCategoryResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class BoardCategoryResponse {

  private String name;

  private String displayName;

  private Boolean isExclusiveToHolders = false;

  private String badgeColor;

  private Boolean isGroup = false;

  @Valid
  private List<@Valid BoardCategoryResponse> subCategories;

  public BoardCategoryResponse name(String name) {
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

  public BoardCategoryResponse displayName(String displayName) {
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

  public BoardCategoryResponse isExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
    return this;
  }

  /**
   * Get isExclusiveToHolders
   * @return isExclusiveToHolders
  */
  
  @JsonProperty("isExclusiveToHolders")
  public Boolean getIsExclusiveToHolders() {
    return isExclusiveToHolders;
  }

  public void setIsExclusiveToHolders(Boolean isExclusiveToHolders) {
    this.isExclusiveToHolders = isExclusiveToHolders;
  }

  public BoardCategoryResponse badgeColor(String badgeColor) {
    this.badgeColor = badgeColor;
    return this;
  }

  /**
   * Get badgeColor
   * @return badgeColor
  */
  
  @JsonProperty("badgeColor")
  public String getBadgeColor() {
    return badgeColor;
  }

  public void setBadgeColor(String badgeColor) {
    this.badgeColor = badgeColor;
  }

  public BoardCategoryResponse isGroup(Boolean isGroup) {
    this.isGroup = isGroup;
    return this;
  }

  /**
   * Get isGroup
   * @return isGroup
  */
  
  @JsonProperty("isGroup")
  public Boolean getIsGroup() {
    return isGroup;
  }

  public void setIsGroup(Boolean isGroup) {
    this.isGroup = isGroup;
  }

  public BoardCategoryResponse subCategories(List<@Valid BoardCategoryResponse> subCategories) {
    this.subCategories = subCategories;
    return this;
  }

  public BoardCategoryResponse addSubCategoriesItem(BoardCategoryResponse subCategoriesItem) {
    if (this.subCategories == null) {
      this.subCategories = new ArrayList<>();
    }
    this.subCategories.add(subCategoriesItem);
    return this;
  }

  /**
   * Get subCategories
   * @return subCategories
  */
  @Valid 
  @JsonProperty("subCategories")
  public List<@Valid BoardCategoryResponse> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<@Valid BoardCategoryResponse> subCategories) {
    this.subCategories = subCategories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BoardCategoryResponse boardCategoryResponse = (BoardCategoryResponse) o;
    return Objects.equals(this.name, boardCategoryResponse.name) &&
        Objects.equals(this.displayName, boardCategoryResponse.displayName) &&
        Objects.equals(this.isExclusiveToHolders, boardCategoryResponse.isExclusiveToHolders) &&
        Objects.equals(this.badgeColor, boardCategoryResponse.badgeColor) &&
        Objects.equals(this.isGroup, boardCategoryResponse.isGroup) &&
        Objects.equals(this.subCategories, boardCategoryResponse.subCategories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, displayName, isExclusiveToHolders, badgeColor, isGroup, subCategories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BoardCategoryResponse {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    isExclusiveToHolders: ").append(toIndentedString(isExclusiveToHolders)).append("\n");
    sb.append("    badgeColor: ").append(toIndentedString(badgeColor)).append("\n");
    sb.append("    isGroup: ").append(toIndentedString(isGroup)).append("\n");
    sb.append("    subCategories: ").append(toIndentedString(subCategories)).append("\n");
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

