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
 * DigitalDocumentAcceptUserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentAcceptUserResponse {

  private Long id;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant birthDate;

  private String phoneNumber;

  private String corporateNo;

  public DigitalDocumentAcceptUserResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DigitalDocumentAcceptUserResponse name(String name) {
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

  public DigitalDocumentAcceptUserResponse birthDate(java.time.Instant birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * Get birthDate
   * @return birthDate
  */
  @Valid 
  @JsonProperty("birthDate")
  public java.time.Instant getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(java.time.Instant birthDate) {
    this.birthDate = birthDate;
  }

  public DigitalDocumentAcceptUserResponse phoneNumber(String phoneNumber) {
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

  public DigitalDocumentAcceptUserResponse corporateNo(String corporateNo) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentAcceptUserResponse digitalDocumentAcceptUserResponse = (DigitalDocumentAcceptUserResponse) o;
    return Objects.equals(this.id, digitalDocumentAcceptUserResponse.id) &&
        Objects.equals(this.name, digitalDocumentAcceptUserResponse.name) &&
        Objects.equals(this.birthDate, digitalDocumentAcceptUserResponse.birthDate) &&
        Objects.equals(this.phoneNumber, digitalDocumentAcceptUserResponse.phoneNumber) &&
        Objects.equals(this.corporateNo, digitalDocumentAcceptUserResponse.corporateNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, birthDate, phoneNumber, corporateNo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentAcceptUserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    corporateNo: ").append(toIndentedString(corporateNo)).append("\n");
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

