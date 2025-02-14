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
 * SendAuthResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SendAuthResponse {

  private String resultCode;

  private String resultMessage;

  private String txSeqNo;

  private String telComResCd;

  public SendAuthResponse resultCode(String resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  /**
   * Get resultCode
   * @return resultCode
  */
  
  @JsonProperty("resultCode")
  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public SendAuthResponse resultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
    return this;
  }

  /**
   * Get resultMessage
   * @return resultMessage
  */
  
  @JsonProperty("resultMessage")
  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  public SendAuthResponse txSeqNo(String txSeqNo) {
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

  public SendAuthResponse telComResCd(String telComResCd) {
    this.telComResCd = telComResCd;
    return this;
  }

  /**
   * Get telComResCd
   * @return telComResCd
  */
  
  @JsonProperty("telComResCd")
  public String getTelComResCd() {
    return telComResCd;
  }

  public void setTelComResCd(String telComResCd) {
    this.telComResCd = telComResCd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SendAuthResponse sendAuthResponse = (SendAuthResponse) o;
    return Objects.equals(this.resultCode, sendAuthResponse.resultCode) &&
        Objects.equals(this.resultMessage, sendAuthResponse.resultMessage) &&
        Objects.equals(this.txSeqNo, sendAuthResponse.txSeqNo) &&
        Objects.equals(this.telComResCd, sendAuthResponse.telComResCd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultCode, resultMessage, txSeqNo, telComResCd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SendAuthResponse {\n");
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    resultMessage: ").append(toIndentedString(resultMessage)).append("\n");
    sb.append("    txSeqNo: ").append(toIndentedString(txSeqNo)).append("\n");
    sb.append("    telComResCd: ").append(toIndentedString(telComResCd)).append("\n");
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

