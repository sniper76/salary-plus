package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CarouselItemResponse;
import ag.act.model.ListSectionHeaderResponse;
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
 * StockHomeSectionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockHomeSectionResponse {

  private String type;

  @Valid
  private List<@Valid CarouselItemResponse> carouselItems;

  private ListSectionHeaderResponse header;

  @Valid
  private List<@Valid SectionItemResponse> listItems;

  public StockHomeSectionResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StockHomeSectionResponse carouselItems(List<@Valid CarouselItemResponse> carouselItems) {
    this.carouselItems = carouselItems;
    return this;
  }

  public StockHomeSectionResponse addCarouselItemsItem(CarouselItemResponse carouselItemsItem) {
    if (this.carouselItems == null) {
      this.carouselItems = new ArrayList<>();
    }
    this.carouselItems.add(carouselItemsItem);
    return this;
  }

  /**
   * Get carouselItems
   * @return carouselItems
  */
  @Valid 
  @JsonProperty("carouselItems")
  public List<@Valid CarouselItemResponse> getCarouselItems() {
    return carouselItems;
  }

  public void setCarouselItems(List<@Valid CarouselItemResponse> carouselItems) {
    this.carouselItems = carouselItems;
  }

  public StockHomeSectionResponse header(ListSectionHeaderResponse header) {
    this.header = header;
    return this;
  }

  /**
   * Get header
   * @return header
  */
  @Valid 
  @JsonProperty("header")
  public ListSectionHeaderResponse getHeader() {
    return header;
  }

  public void setHeader(ListSectionHeaderResponse header) {
    this.header = header;
  }

  public StockHomeSectionResponse listItems(List<@Valid SectionItemResponse> listItems) {
    this.listItems = listItems;
    return this;
  }

  public StockHomeSectionResponse addListItemsItem(SectionItemResponse listItemsItem) {
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
    StockHomeSectionResponse stockHomeSectionResponse = (StockHomeSectionResponse) o;
    return Objects.equals(this.type, stockHomeSectionResponse.type) &&
        Objects.equals(this.carouselItems, stockHomeSectionResponse.carouselItems) &&
        Objects.equals(this.header, stockHomeSectionResponse.header) &&
        Objects.equals(this.listItems, stockHomeSectionResponse.listItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, carouselItems, header, listItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockHomeSectionResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    carouselItems: ").append(toIndentedString(carouselItems)).append("\n");
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

