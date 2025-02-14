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
 * DigitalDocumentItemResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentItemResponse {

  private Long id;

  private String title;

  private String content;

  private String defaultSelectValue;

  private String leaderDescription;

  @Valid
  private List<@Valid DigitalDocumentItemResponse> childItems;

  public DigitalDocumentItemResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DigitalDocumentItemResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 의안 제목
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DigitalDocumentItemResponse content(String content) {
    this.content = content;
    return this;
  }

  /**
   * 의안 상세
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public DigitalDocumentItemResponse defaultSelectValue(String defaultSelectValue) {
    this.defaultSelectValue = defaultSelectValue;
    return this;
  }

  /**
   * 기본 선택값
   * @return defaultSelectValue
  */
  
  @JsonProperty("defaultSelectValue")
  public String getDefaultSelectValue() {
    return defaultSelectValue;
  }

  public void setDefaultSelectValue(String defaultSelectValue) {
    this.defaultSelectValue = defaultSelectValue;
  }

  public DigitalDocumentItemResponse leaderDescription(String leaderDescription) {
    this.leaderDescription = leaderDescription;
    return this;
  }

  /**
   * 주주대표 의안 상세 설명
   * @return leaderDescription
  */
  
  @JsonProperty("leaderDescription")
  public String getLeaderDescription() {
    return leaderDescription;
  }

  public void setLeaderDescription(String leaderDescription) {
    this.leaderDescription = leaderDescription;
  }

  public DigitalDocumentItemResponse childItems(List<@Valid DigitalDocumentItemResponse> childItems) {
    this.childItems = childItems;
    return this;
  }

  public DigitalDocumentItemResponse addChildItemsItem(DigitalDocumentItemResponse childItemsItem) {
    if (this.childItems == null) {
      this.childItems = new ArrayList<>();
    }
    this.childItems.add(childItemsItem);
    return this;
  }

  /**
   * Get childItems
   * @return childItems
  */
  @Valid 
  @JsonProperty("childItems")
  public List<@Valid DigitalDocumentItemResponse> getChildItems() {
    return childItems;
  }

  public void setChildItems(List<@Valid DigitalDocumentItemResponse> childItems) {
    this.childItems = childItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentItemResponse digitalDocumentItemResponse = (DigitalDocumentItemResponse) o;
    return Objects.equals(this.id, digitalDocumentItemResponse.id) &&
        Objects.equals(this.title, digitalDocumentItemResponse.title) &&
        Objects.equals(this.content, digitalDocumentItemResponse.content) &&
        Objects.equals(this.defaultSelectValue, digitalDocumentItemResponse.defaultSelectValue) &&
        Objects.equals(this.leaderDescription, digitalDocumentItemResponse.leaderDescription) &&
        Objects.equals(this.childItems, digitalDocumentItemResponse.childItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, defaultSelectValue, leaderDescription, childItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentItemResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    defaultSelectValue: ").append(toIndentedString(defaultSelectValue)).append("\n");
    sb.append("    leaderDescription: ").append(toIndentedString(leaderDescription)).append("\n");
    sb.append("    childItems: ").append(toIndentedString(childItems)).append("\n");
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

