package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CarouselItemResponse;
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
 * CarouselSectionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CarouselSectionResponse {

  private String type;

  @Valid
  private List<@Valid CarouselItemResponse> carouselItems;

  public CarouselSectionResponse type(String type) {
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

  public CarouselSectionResponse carouselItems(List<@Valid CarouselItemResponse> carouselItems) {
    this.carouselItems = carouselItems;
    return this;
  }

  public CarouselSectionResponse addCarouselItemsItem(CarouselItemResponse carouselItemsItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CarouselSectionResponse carouselSectionResponse = (CarouselSectionResponse) o;
    return Objects.equals(this.type, carouselSectionResponse.type) &&
        Objects.equals(this.carouselItems, carouselSectionResponse.carouselItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, carouselItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CarouselSectionResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    carouselItems: ").append(toIndentedString(carouselItems)).append("\n");
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

