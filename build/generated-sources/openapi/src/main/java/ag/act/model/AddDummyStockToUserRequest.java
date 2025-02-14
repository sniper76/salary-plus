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
 * AddDummyStockToUserRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class AddDummyStockToUserRequest {

  private String stockCode;

  private Long stockReferenceDateId;

  public AddDummyStockToUserRequest stockCode(String stockCode) {
    this.stockCode = stockCode;
    return this;
  }

  /**
   * 종목 코드
   * @return stockCode
  */
  
  @JsonProperty("stockCode")
  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public AddDummyStockToUserRequest stockReferenceDateId(Long stockReferenceDateId) {
    this.stockReferenceDateId = stockReferenceDateId;
    return this;
  }

  /**
   * 기준일 정보 아이디
   * @return stockReferenceDateId
  */
  
  @JsonProperty("stockReferenceDateId")
  public Long getStockReferenceDateId() {
    return stockReferenceDateId;
  }

  public void setStockReferenceDateId(Long stockReferenceDateId) {
    this.stockReferenceDateId = stockReferenceDateId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddDummyStockToUserRequest addDummyStockToUserRequest = (AddDummyStockToUserRequest) o;
    return Objects.equals(this.stockCode, addDummyStockToUserRequest.stockCode) &&
        Objects.equals(this.stockReferenceDateId, addDummyStockToUserRequest.stockReferenceDateId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stockCode, stockReferenceDateId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddDummyStockToUserRequest {\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockReferenceDateId: ").append(toIndentedString(stockReferenceDateId)).append("\n");
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

