package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * 모두싸인 의결권위임 입력 정보
 */

@JsonTypeName("UpdatePostRequest_digitalProxy")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdatePostRequestDigitalProxy implements ag.act.dto.TargetStartAndEndDatePeriod {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  public UpdatePostRequestDigitalProxy targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * 시작일
   * @return targetStartDate
  */
  @Valid 
  @JsonProperty("targetStartDate")
  public java.time.Instant getTargetStartDate() {
    return targetStartDate;
  }

  public void setTargetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
  }

  public UpdatePostRequestDigitalProxy targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * 종료일
   * @return targetEndDate
  */
  @Valid 
  @JsonProperty("targetEndDate")
  public java.time.Instant getTargetEndDate() {
    return targetEndDate;
  }

  public void setTargetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdatePostRequestDigitalProxy updatePostRequestDigitalProxy = (UpdatePostRequestDigitalProxy) o;
    return Objects.equals(this.targetStartDate, updatePostRequestDigitalProxy.targetStartDate) &&
        Objects.equals(this.targetEndDate, updatePostRequestDigitalProxy.targetEndDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetStartDate, targetEndDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdatePostRequestDigitalProxy {\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
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

