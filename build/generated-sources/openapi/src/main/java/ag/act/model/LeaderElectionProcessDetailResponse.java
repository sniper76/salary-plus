package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * LeaderElectionProcessDetailResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class LeaderElectionProcessDetailResponse {

  private String title;

  private Long value;

  private String unit;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant endDate;

  public LeaderElectionProcessDetailResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 지원자 현황 or 투표율
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public LeaderElectionProcessDetailResponse value(Long value) {
    this.value = value;
    return this;
  }

  /**
   * 지원자 현황인원수 or 투표율
   * @return value
  */
  
  @JsonProperty("value")
  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  public LeaderElectionProcessDetailResponse unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * 명 or %
   * @return unit
  */
  
  @JsonProperty("unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public LeaderElectionProcessDetailResponse startDate(java.time.Instant startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * 시작일
   * @return startDate
  */
  @Valid 
  @JsonProperty("startDate")
  public java.time.Instant getStartDate() {
    return startDate;
  }

  public void setStartDate(java.time.Instant startDate) {
    this.startDate = startDate;
  }

  public LeaderElectionProcessDetailResponse endDate(java.time.Instant endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * 시작일
   * @return endDate
  */
  @Valid 
  @JsonProperty("endDate")
  public java.time.Instant getEndDate() {
    return endDate;
  }

  public void setEndDate(java.time.Instant endDate) {
    this.endDate = endDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderElectionProcessDetailResponse leaderElectionProcessDetailResponse = (LeaderElectionProcessDetailResponse) o;
    return Objects.equals(this.title, leaderElectionProcessDetailResponse.title) &&
        Objects.equals(this.value, leaderElectionProcessDetailResponse.value) &&
        Objects.equals(this.unit, leaderElectionProcessDetailResponse.unit) &&
        Objects.equals(this.startDate, leaderElectionProcessDetailResponse.startDate) &&
        Objects.equals(this.endDate, leaderElectionProcessDetailResponse.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, value, unit, startDate, endDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderElectionProcessDetailResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
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

