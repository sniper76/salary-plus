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
 * ResendAuthRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ResendAuthRequest {

  @IsPhoneNumber(message = "휴대폰 번호를 확인해주세요.")

  private String phoneNumber;

  @Size(min = 20, max = 20, message = "본인인증 요청 트랜잭션 번호를 확인해주세요.")

  private String txSeqNo;

  public ResendAuthRequest phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
  */
  
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public ResendAuthRequest txSeqNo(String txSeqNo) {
    this.txSeqNo = txSeqNo;
    return this;
  }

  /**
   * Get txSeqNo
   * @return txSeqNo
  */
  
  @JsonProperty("txSeqNo")
  public String getTxSeqNo() {
    return txSeqNo;
  }

  public void setTxSeqNo(String txSeqNo) {
    this.txSeqNo = txSeqNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResendAuthRequest resendAuthRequest = (ResendAuthRequest) o;
    return Objects.equals(this.phoneNumber, resendAuthRequest.phoneNumber) &&
        Objects.equals(this.txSeqNo, resendAuthRequest.txSeqNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phoneNumber, txSeqNo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResendAuthRequest {\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    txSeqNo: ").append(toIndentedString(txSeqNo)).append("\n");
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

