package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CarouselSectionHeaderResponse;
import ag.act.model.SectionItemResponse;
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
 * CarouselItemResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CarouselItemResponse {

  private CarouselSectionHeaderResponse header;

  @Valid
  private List<@Valid SectionItemResponse> listItems;

  public CarouselItemResponse header(CarouselSectionHeaderResponse header) {
    this.header = header;
    return this;
  }

  /**
   * Get header
   * @return header
  */
  @Valid 
  @JsonProperty("header")
  public CarouselSectionHeaderResponse getHeader() {
    return header;
  }

  public void setHeader(CarouselSectionHeaderResponse header) {
    this.header = header;
  }

  public CarouselItemResponse listItems(List<@Valid SectionItemResponse> listItems) {
    this.listItems = listItems;
    return this;
  }

  public CarouselItemResponse addListItemsItem(SectionItemResponse listItemsItem) {
    if (this.listItems == null) {
      this.listItems = new ArrayList<>();
    }
    this.listItems.add(listItemsItem);
    return this;
  }

  /**
   * Get listItems
   * @return listItems
  */
  @Valid 
  @JsonProperty("listItems")
  public List<@Valid SectionItemResponse> getListItems() {
    return listItems;
  }

  public void setListItems(List<@Valid SectionItemResponse> listItems) {
    this.listItems = listItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CarouselItemResponse carouselItemResponse = (CarouselItemResponse) o;
    return Objects.equals(this.header, carouselItemResponse.header) &&
        Objects.equals(this.listItems, carouselItemResponse.listItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(header, listItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CarouselItemResponse {\n");
    sb.append("    header: ").append(toIndentedString(header)).append("\n");
    sb.append("    listItems: ").append(toIndentedString(listItems)).append("\n");
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

