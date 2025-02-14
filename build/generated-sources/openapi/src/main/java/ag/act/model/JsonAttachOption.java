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
 * JsonAttachOption
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class JsonAttachOption {

  private String signImage;

  private String idCardImage;

  private String bankAccountImage;

  private String hectoEncryptedBankAccountPdf;

  public JsonAttachOption signImage(String signImage) {
    this.signImage = signImage;
    return this;
  }

  /**
   * REQUIRED or OPTIONAL or NONE
   * @return signImage
  */
  
  @JsonProperty("signImage")
  public String getSignImage() {
    return signImage;
  }

  public void setSignImage(String signImage) {
    this.signImage = signImage;
  }

  public JsonAttachOption idCardImage(String idCardImage) {
    this.idCardImage = idCardImage;
    return this;
  }

  /**
   * REQUIRED or OPTIONAL or NONE
   * @return idCardImage
  */
  
  @JsonProperty("idCardImage")
  public String getIdCardImage() {
    return idCardImage;
  }

  public void setIdCardImage(String idCardImage) {
    this.idCardImage = idCardImage;
  }

  public JsonAttachOption bankAccountImage(String bankAccountImage) {
    this.bankAccountImage = bankAccountImage;
    return this;
  }

  /**
   * REQUIRED or OPTIONAL or NONE
   * @return bankAccountImage
  */
  
  @JsonProperty("bankAccountImage")
  public String getBankAccountImage() {
    return bankAccountImage;
  }

  public void setBankAccountImage(String bankAccountImage) {
    this.bankAccountImage = bankAccountImage;
  }

  public JsonAttachOption hectoEncryptedBankAccountPdf(String hectoEncryptedBankAccountPdf) {
    this.hectoEncryptedBankAccountPdf = hectoEncryptedBankAccountPdf;
    return this;
  }

  /**
   * REQUIRED or OPTIONAL or NONE
   * @return hectoEncryptedBankAccountPdf
  */
  
  @JsonProperty("hectoEncryptedBankAccountPdf")
  public String getHectoEncryptedBankAccountPdf() {
    return hectoEncryptedBankAccountPdf;
  }

  public void setHectoEncryptedBankAccountPdf(String hectoEncryptedBankAccountPdf) {
    this.hectoEncryptedBankAccountPdf = hectoEncryptedBankAccountPdf;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonAttachOption jsonAttachOption = (JsonAttachOption) o;
    return Objects.equals(this.signImage, jsonAttachOption.signImage) &&
        Objects.equals(this.idCardImage, jsonAttachOption.idCardImage) &&
        Objects.equals(this.bankAccountImage, jsonAttachOption.bankAccountImage) &&
        Objects.equals(this.hectoEncryptedBankAccountPdf, jsonAttachOption.hectoEncryptedBankAccountPdf);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signImage, idCardImage, bankAccountImage, hectoEncryptedBankAccountPdf);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonAttachOption {\n");
    sb.append("    signImage: ").append(toIndentedString(signImage)).append("\n");
    sb.append("    idCardImage: ").append(toIndentedString(idCardImage)).append("\n");
    sb.append("    bankAccountImage: ").append(toIndentedString(bankAccountImage)).append("\n");
    sb.append("    hectoEncryptedBankAccountPdf: ").append(toIndentedString(hectoEncryptedBankAccountPdf)).append("\n");
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

