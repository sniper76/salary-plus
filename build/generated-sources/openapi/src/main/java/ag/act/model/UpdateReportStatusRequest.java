package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UpdateReportStatusRequest
 */

@JsonTypeName("updateReportStatus_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateReportStatusRequest {

  @NotBlank(message = "처리 사유를 확인해주세요.")

  private String result;

  private String currentReportStatus;

  private String changeReportStatus;

  public UpdateReportStatusRequest result(String result) {
    this.result = result;
    return this;
  }

  /**
   * 판단이유 mandatory
   * @return result
  */
  
  @JsonProperty("result")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public UpdateReportStatusRequest currentReportStatus(String currentReportStatus) {
    this.currentReportStatus = currentReportStatus;
    return this;
  }

  /**
   * Current Status of the report is mandatory
   * @return currentReportStatus
  */
  
  @JsonProperty("currentReportStatus")
  public String getCurrentReportStatus() {
    return currentReportStatus;
  }

  public void setCurrentReportStatus(String currentReportStatus) {
    this.currentReportStatus = currentReportStatus;
  }

  public UpdateReportStatusRequest changeReportStatus(String changeReportStatus) {
    this.changeReportStatus = changeReportStatus;
    return this;
  }

  /**
   * Changing Status of the report is mandatory
   * @return changeReportStatus
  */
  
  @JsonProperty("changeReportStatus")
  public String getChangeReportStatus() {
    return changeReportStatus;
  }

  public void setChangeReportStatus(String changeReportStatus) {
    this.changeReportStatus = changeReportStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReportStatusRequest updateReportStatusRequest = (UpdateReportStatusRequest) o;
    return Objects.equals(this.result, updateReportStatusRequest.result) &&
        Objects.equals(this.currentReportStatus, updateReportStatusRequest.currentReportStatus) &&
        Objects.equals(this.changeReportStatus, updateReportStatusRequest.changeReportStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, currentReportStatus, changeReportStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReportStatusRequest {\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    currentReportStatus: ").append(toIndentedString(currentReportStatus)).append("\n");
    sb.append("    changeReportStatus: ").append(toIndentedString(changeReportStatus)).append("\n");
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

