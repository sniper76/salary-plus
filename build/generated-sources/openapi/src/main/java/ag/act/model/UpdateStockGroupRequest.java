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
 * UpdateStockGroupRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateStockGroupRequest {

  private String name;

  private String description;

  @Valid
  private List<String> stockCodes;

  public UpdateStockGroupRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the stock group is optional
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UpdateStockGroupRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the stock group is optional
   * @return description
  */
  
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public UpdateStockGroupRequest stockCodes(List<String> stockCodes) {
    this.stockCodes = stockCodes;
    return this;
  }

  public UpdateStockGroupRequest addStockCodesItem(String stockCodesItem) {
    if (this.stockCodes == null) {
      this.stockCodes = new ArrayList<>();
    }
    this.stockCodes.add(stockCodesItem);
    return this;
  }

  /**
   * Stock codes of the stock group is optional
   * @return stockCodes
  */
  
  @JsonProperty("stockCodes")
  public List<String> getStockCodes() {
    return stockCodes;
  }

  public void setStockCodes(List<String> stockCodes) {
    this.stockCodes = stockCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateStockGroupRequest updateStockGroupRequest = (UpdateStockGroupRequest) o;
    return Objects.equals(this.name, updateStockGroupRequest.name) &&
        Objects.equals(this.description, updateStockGroupRequest.description) &&
        Objects.equals(this.stockCodes, updateStockGroupRequest.stockCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, stockCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateStockGroupRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    stockCodes: ").append(toIndentedString(stockCodes)).append("\n");
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

