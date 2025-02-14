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
 * DigitalDocumentUserDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentUserDetailsResponse {

  private Long id;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant birthDate;

  private String gender;

  private String phoneNumber;

  private String zipcode;

  private String address;

  private String addressDetail;

  private Long userId;

  private Long digitalDocumentId;

  private Long issuedNumber;

  public DigitalDocumentUserDetailsResponse id(Long id) {
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

  public DigitalDocumentUserDetailsResponse name(String name) {
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

  public DigitalDocumentUserDetailsResponse birthDate(java.time.Instant birthDate) {
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

  public DigitalDocumentUserDetailsResponse gender(String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
  */
  
  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public DigitalDocumentUserDetailsResponse phoneNumber(String phoneNumber) {
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

  public DigitalDocumentUserDetailsResponse zipcode(String zipcode) {
    this.zipcode = zipcode;
    return this;
  }

  /**
   * Get zipcode
   * @return zipcode
  */
  
  @JsonProperty("zipcode")
  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public DigitalDocumentUserDetailsResponse address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
  */
  
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public DigitalDocumentUserDetailsResponse addressDetail(String addressDetail) {
    this.addressDetail = addressDetail;
    return this;
  }

  /**
   * Get addressDetail
   * @return addressDetail
  */
  
  @JsonProperty("addressDetail")
  public String getAddressDetail() {
    return addressDetail;
  }

  public void setAddressDetail(String addressDetail) {
    this.addressDetail = addressDetail;
  }

  public DigitalDocumentUserDetailsResponse userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public DigitalDocumentUserDetailsResponse digitalDocumentId(Long digitalDocumentId) {
    this.digitalDocumentId = digitalDocumentId;
    return this;
  }

  /**
   * Get digitalDocumentId
   * @return digitalDocumentId
  */
  
  @JsonProperty("digitalDocumentId")
  public Long getDigitalDocumentId() {
    return digitalDocumentId;
  }

  public void setDigitalDocumentId(Long digitalDocumentId) {
    this.digitalDocumentId = digitalDocumentId;
  }

  public DigitalDocumentUserDetailsResponse issuedNumber(Long issuedNumber) {
    this.issuedNumber = issuedNumber;
    return this;
  }

  /**
   * Get issuedNumber
   * @return issuedNumber
  */
  
  @JsonProperty("issuedNumber")
  public Long getIssuedNumber() {
    return issuedNumber;
  }

  public void setIssuedNumber(Long issuedNumber) {
    this.issuedNumber = issuedNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentUserDetailsResponse digitalDocumentUserDetailsResponse = (DigitalDocumentUserDetailsResponse) o;
    return Objects.equals(this.id, digitalDocumentUserDetailsResponse.id) &&
        Objects.equals(this.name, digitalDocumentUserDetailsResponse.name) &&
        Objects.equals(this.birthDate, digitalDocumentUserDetailsResponse.birthDate) &&
        Objects.equals(this.gender, digitalDocumentUserDetailsResponse.gender) &&
        Objects.equals(this.phoneNumber, digitalDocumentUserDetailsResponse.phoneNumber) &&
        Objects.equals(this.zipcode, digitalDocumentUserDetailsResponse.zipcode) &&
        Objects.equals(this.address, digitalDocumentUserDetailsResponse.address) &&
        Objects.equals(this.addressDetail, digitalDocumentUserDetailsResponse.addressDetail) &&
        Objects.equals(this.userId, digitalDocumentUserDetailsResponse.userId) &&
        Objects.equals(this.digitalDocumentId, digitalDocumentUserDetailsResponse.digitalDocumentId) &&
        Objects.equals(this.issuedNumber, digitalDocumentUserDetailsResponse.issuedNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, birthDate, gender, phoneNumber, zipcode, address, addressDetail, userId, digitalDocumentId, issuedNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentUserDetailsResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressDetail: ").append(toIndentedString(addressDetail)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    digitalDocumentId: ").append(toIndentedString(digitalDocumentId)).append("\n");
    sb.append("    issuedNumber: ").append(toIndentedString(issuedNumber)).append("\n");
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

