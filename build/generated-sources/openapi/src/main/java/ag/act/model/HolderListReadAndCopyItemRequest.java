package ag.act.model;

import java.net.URI;
import java.util.Objects;
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
 * HolderListReadAndCopyItemRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class HolderListReadAndCopyItemRequest {

  @NotBlank(message = "항목 타입을 확인해주세요.")

  private String itemType;

  @NotBlank(message = "항목 값 확인해주세요.")

  private String itemValue;

  public HolderListReadAndCopyItemRequest itemType(String itemType) {
    this.itemType = itemType;
    return this;
  }

  /**
   * Get itemType
   * @return itemType
  */
  
  @JsonProperty("itemType")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public HolderListReadAndCopyItemRequest itemValue(String itemValue) {
    this.itemValue = itemValue;
    return this;
  }

  /**
   * Get itemValue
   * @return itemValue
  */
  
  @JsonProperty("itemValue")
  public String getItemValue() {
    return itemValue;
  }

  public void setItemValue(String itemValue) {
    this.itemValue = itemValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HolderListReadAndCopyItemRequest holderListReadAndCopyItemRequest = (HolderListReadAndCopyItemRequest) o;
    return Objects.equals(this.itemType, holderListReadAndCopyItemRequest.itemType) &&
        Objects.equals(this.itemValue, holderListReadAndCopyItemRequest.itemValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemType, itemValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HolderListReadAndCopyItemRequest {\n");
    sb.append("    itemType: ").append(toIndentedString(itemType)).append("\n");
    sb.append("    itemValue: ").append(toIndentedString(itemValue)).append("\n");
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

