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
 * StockStatisticsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockStatisticsResponse {

  private String key;

  private java.math.BigDecimal value;

  public StockStatisticsResponse key(String key) {
    this.key = key;
    return this;
  }

  /**
   * 년월, 년월일
   * @return key
  */
  
  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public StockStatisticsResponse value(java.math.BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * 주식수, 주주수, 시가액, 지분율
   * @return value
  */
  @Valid 
  @JsonProperty("value")
  public java.math.BigDecimal getValue() {
    return value;
  }

  public void setValue(java.math.BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockStatisticsResponse stockStatisticsResponse = (StockStatisticsResponse) o;
    return Objects.equals(this.key, stockStatisticsResponse.key) &&
        Objects.equals(this.value, stockStatisticsResponse.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockStatisticsResponse {\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

