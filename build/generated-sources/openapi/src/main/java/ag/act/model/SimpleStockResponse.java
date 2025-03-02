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
 * SimpleStockResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SimpleStockResponse {

  private String code;

  private String name;

  private String standardCode;

  public SimpleStockResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  */
  
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public SimpleStockResponse name(String name) {
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

  public SimpleStockResponse standardCode(String standardCode) {
    this.standardCode = standardCode;
    return this;
  }

  /**
   * Get standardCode
   * @return standardCode
  */
  
  @JsonProperty("standardCode")
  public String getStandardCode() {
    return standardCode;
  }

  public void setStandardCode(String standardCode) {
    this.standardCode = standardCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleStockResponse simpleStockResponse = (SimpleStockResponse) o;
    return Objects.equals(this.code, simpleStockResponse.code) &&
        Objects.equals(this.name, simpleStockResponse.name) &&
        Objects.equals(this.standardCode, simpleStockResponse.standardCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, standardCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimpleStockResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    standardCode: ").append(toIndentedString(standardCode)).append("\n");
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

