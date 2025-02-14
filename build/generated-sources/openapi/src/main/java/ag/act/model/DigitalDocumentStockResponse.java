package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * DigitalDocumentStockResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentStockResponse {

  private String code;

  private String standardCode;

  private String name;

  private Long referenceDateId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate referenceDate;

  private Long referenceDateStockCount;

  public DigitalDocumentStockResponse code(String code) {
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

  public DigitalDocumentStockResponse standardCode(String standardCode) {
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

  public DigitalDocumentStockResponse name(String name) {
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

  public DigitalDocumentStockResponse referenceDateId(Long referenceDateId) {
    this.referenceDateId = referenceDateId;
    return this;
  }

  /**
   * Get referenceDateId
   * @return referenceDateId
  */
  
  @JsonProperty("referenceDateId")
  public Long getReferenceDateId() {
    return referenceDateId;
  }

  public void setReferenceDateId(Long referenceDateId) {
    this.referenceDateId = referenceDateId;
  }

  public DigitalDocumentStockResponse referenceDate(LocalDate referenceDate) {
    this.referenceDate = referenceDate;
    return this;
  }

  /**
   * Get referenceDate
   * @return referenceDate
  */
  @Valid 
  @JsonProperty("referenceDate")
  public LocalDate getReferenceDate() {
    return referenceDate;
  }

  public void setReferenceDate(LocalDate referenceDate) {
    this.referenceDate = referenceDate;
  }

  public DigitalDocumentStockResponse referenceDateStockCount(Long referenceDateStockCount) {
    this.referenceDateStockCount = referenceDateStockCount;
    return this;
  }

  /**
   * Get referenceDateStockCount
   * @return referenceDateStockCount
  */
  
  @JsonProperty("referenceDateStockCount")
  public Long getReferenceDateStockCount() {
    return referenceDateStockCount;
  }

  public void setReferenceDateStockCount(Long referenceDateStockCount) {
    this.referenceDateStockCount = referenceDateStockCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentStockResponse digitalDocumentStockResponse = (DigitalDocumentStockResponse) o;
    return Objects.equals(this.code, digitalDocumentStockResponse.code) &&
        Objects.equals(this.standardCode, digitalDocumentStockResponse.standardCode) &&
        Objects.equals(this.name, digitalDocumentStockResponse.name) &&
        Objects.equals(this.referenceDateId, digitalDocumentStockResponse.referenceDateId) &&
        Objects.equals(this.referenceDate, digitalDocumentStockResponse.referenceDate) &&
        Objects.equals(this.referenceDateStockCount, digitalDocumentStockResponse.referenceDateStockCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, standardCode, name, referenceDateId, referenceDate, referenceDateStockCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentStockResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    standardCode: ").append(toIndentedString(standardCode)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    referenceDateId: ").append(toIndentedString(referenceDateId)).append("\n");
    sb.append("    referenceDate: ").append(toIndentedString(referenceDate)).append("\n");
    sb.append("    referenceDateStockCount: ").append(toIndentedString(referenceDateStockCount)).append("\n");
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

