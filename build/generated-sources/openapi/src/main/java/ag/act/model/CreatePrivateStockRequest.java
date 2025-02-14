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
 * CreatePrivateStockRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreatePrivateStockRequest {

  @Size(min = 1, max = 255, message = "종목명을 확인해주세요.")
@NotBlank(message = "종목명을 확인해주세요.")

  private String name;

  @Size(min = 6, max = 6, message = "종목 코드는 6글자로 입력해주세요.")
@NotBlank(message = "종목코드를 확인해주세요.")

  private String code;

  @Size(min = 12, max = 12, message = "표준 종목 코드는 12글자로 입력해주세요.")
@NotBlank(message = "표준종목코드를 확인해주세요.")

  private String standardCode;

  @NotBlank(message = "종목타입을 확인해주세요.")
@Pattern(regexp = "보통주|액트주|신형우선주|종류주권|구형우선주", message = "종목타입을 확인해주세요.")

  private String stockType;

  @PositiveOrZero(message = "종가를 확인해주세요.")

  private Integer closingPrice;

  @PositiveOrZero(message = "총주식 발행수를 확인해주세요.")

  private Long totalIssuedQuantity;

  public CreatePrivateStockRequest name(String name) {
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

  public CreatePrivateStockRequest code(String code) {
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

  public CreatePrivateStockRequest standardCode(String standardCode) {
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

  public CreatePrivateStockRequest stockType(String stockType) {
    this.stockType = stockType;
    return this;
  }

  /**
   * Get stockType
   * @return stockType
  */
  
  @JsonProperty("stockType")
  public String getStockType() {
    return stockType;
  }

  public void setStockType(String stockType) {
    this.stockType = stockType;
  }

  public CreatePrivateStockRequest closingPrice(Integer closingPrice) {
    this.closingPrice = closingPrice;
    return this;
  }

  /**
   * Get closingPrice
   * @return closingPrice
  */
  
  @JsonProperty("closingPrice")
  public Integer getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(Integer closingPrice) {
    this.closingPrice = closingPrice;
  }

  public CreatePrivateStockRequest totalIssuedQuantity(Long totalIssuedQuantity) {
    this.totalIssuedQuantity = totalIssuedQuantity;
    return this;
  }

  /**
   * Get totalIssuedQuantity
   * @return totalIssuedQuantity
  */
  
  @JsonProperty("totalIssuedQuantity")
  public Long getTotalIssuedQuantity() {
    return totalIssuedQuantity;
  }

  public void setTotalIssuedQuantity(Long totalIssuedQuantity) {
    this.totalIssuedQuantity = totalIssuedQuantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePrivateStockRequest createPrivateStockRequest = (CreatePrivateStockRequest) o;
    return Objects.equals(this.name, createPrivateStockRequest.name) &&
        Objects.equals(this.code, createPrivateStockRequest.code) &&
        Objects.equals(this.standardCode, createPrivateStockRequest.standardCode) &&
        Objects.equals(this.stockType, createPrivateStockRequest.stockType) &&
        Objects.equals(this.closingPrice, createPrivateStockRequest.closingPrice) &&
        Objects.equals(this.totalIssuedQuantity, createPrivateStockRequest.totalIssuedQuantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, code, standardCode, stockType, closingPrice, totalIssuedQuantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePrivateStockRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    standardCode: ").append(toIndentedString(standardCode)).append("\n");
    sb.append("    stockType: ").append(toIndentedString(stockType)).append("\n");
    sb.append("    closingPrice: ").append(toIndentedString(closingPrice)).append("\n");
    sb.append("    totalIssuedQuantity: ").append(toIndentedString(totalIssuedQuantity)).append("\n");
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

