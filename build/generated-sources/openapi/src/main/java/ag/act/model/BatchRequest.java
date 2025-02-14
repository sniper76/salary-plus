package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * BatchRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class BatchRequest {

  private Integer batchPeriod;

  /**
   * The time unit for the batch period
   */
  public enum PeriodTimeUnitEnum {
    MINUTE("MINUTE"),
    
    HOUR("HOUR");

    private String value;

    PeriodTimeUnitEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PeriodTimeUnitEnum fromValue(String value) {
      for (PeriodTimeUnitEnum b : PeriodTimeUnitEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private PeriodTimeUnitEnum periodTimeUnit = PeriodTimeUnitEnum.HOUR;

  private Boolean isFirstCreateUserHoldingStockHistory = false;

  public BatchRequest batchPeriod(Integer batchPeriod) {
    this.batchPeriod = batchPeriod;
    return this;
  }

  /**
   * The duration of the batch period
   * @return batchPeriod
  */
  
  @JsonProperty("batchPeriod")
  public Integer getBatchPeriod() {
    return batchPeriod;
  }

  public void setBatchPeriod(Integer batchPeriod) {
    this.batchPeriod = batchPeriod;
  }

  public BatchRequest periodTimeUnit(PeriodTimeUnitEnum periodTimeUnit) {
    this.periodTimeUnit = periodTimeUnit;
    return this;
  }

  /**
   * The time unit for the batch period
   * @return periodTimeUnit
  */
  
  @JsonProperty("periodTimeUnit")
  public PeriodTimeUnitEnum getPeriodTimeUnit() {
    return periodTimeUnit;
  }

  public void setPeriodTimeUnit(PeriodTimeUnitEnum periodTimeUnit) {
    this.periodTimeUnit = periodTimeUnit;
  }

  public BatchRequest isFirstCreateUserHoldingStockHistory(Boolean isFirstCreateUserHoldingStockHistory) {
    this.isFirstCreateUserHoldingStockHistory = isFirstCreateUserHoldingStockHistory;
    return this;
  }

  /**
   * Get isFirstCreateUserHoldingStockHistory
   * @return isFirstCreateUserHoldingStockHistory
  */
  
  @JsonProperty("isFirstCreateUserHoldingStockHistory")
  public Boolean getIsFirstCreateUserHoldingStockHistory() {
    return isFirstCreateUserHoldingStockHistory;
  }

  public void setIsFirstCreateUserHoldingStockHistory(Boolean isFirstCreateUserHoldingStockHistory) {
    this.isFirstCreateUserHoldingStockHistory = isFirstCreateUserHoldingStockHistory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BatchRequest batchRequest = (BatchRequest) o;
    return Objects.equals(this.batchPeriod, batchRequest.batchPeriod) &&
        Objects.equals(this.periodTimeUnit, batchRequest.periodTimeUnit) &&
        Objects.equals(this.isFirstCreateUserHoldingStockHistory, batchRequest.isFirstCreateUserHoldingStockHistory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchPeriod, periodTimeUnit, isFirstCreateUserHoldingStockHistory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BatchRequest {\n");
    sb.append("    batchPeriod: ").append(toIndentedString(batchPeriod)).append("\n");
    sb.append("    periodTimeUnit: ").append(toIndentedString(periodTimeUnit)).append("\n");
    sb.append("    isFirstCreateUserHoldingStockHistory: ").append(toIndentedString(isFirstCreateUserHoldingStockHistory)).append("\n");
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

