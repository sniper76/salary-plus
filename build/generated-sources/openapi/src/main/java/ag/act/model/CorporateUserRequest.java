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
 * CorporateUserRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CorporateUserRequest {

  @NotBlank(message = "법인사업자번호을 확인해주세요.")

  private String corporateNo;

  @NotBlank(message = "볍인명을 확인해주세요.")

  private String corporateName;

  private String status = "ACTIVE";

  public CorporateUserRequest corporateNo(String corporateNo) {
    this.corporateNo = corporateNo;
    return this;
  }

  /**
   * Get corporateNo
   * @return corporateNo
  */
  
  @JsonProperty("corporateNo")
  public String getCorporateNo() {
    return corporateNo;
  }

  public void setCorporateNo(String corporateNo) {
    this.corporateNo = corporateNo;
  }

  public CorporateUserRequest corporateName(String corporateName) {
    this.corporateName = corporateName;
    return this;
  }

  /**
   * Get corporateName
   * @return corporateName
  */
  
  @JsonProperty("corporateName")
  public String getCorporateName() {
    return corporateName;
  }

  public void setCorporateName(String corporateName) {
    this.corporateName = corporateName;
  }

  public CorporateUserRequest status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CorporateUserRequest corporateUserRequest = (CorporateUserRequest) o;
    return Objects.equals(this.corporateNo, corporateUserRequest.corporateNo) &&
        Objects.equals(this.corporateName, corporateUserRequest.corporateName) &&
        Objects.equals(this.status, corporateUserRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corporateNo, corporateName, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorporateUserRequest {\n");
    sb.append("    corporateNo: ").append(toIndentedString(corporateNo)).append("\n");
    sb.append("    corporateName: ").append(toIndentedString(corporateName)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

