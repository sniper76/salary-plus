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
 * DigitalDocumentUserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentUserResponse {

  private Long id;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant birthDate;

  private String gender;

  private String phoneNumber;

  private String zipcode;

  private String address;

  private String addressDetail;

  public DigitalDocumentUserResponse id(Long id) {
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

  public DigitalDocumentUserResponse name(String name) {
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

  public DigitalDocumentUserResponse birthDate(java.time.Instant birthDate) {
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

  public DigitalDocumentUserResponse gender(String gender) {
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

  public DigitalDocumentUserResponse phoneNumber(String phoneNumber) {
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

  public DigitalDocumentUserResponse zipcode(String zipcode) {
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

  public DigitalDocumentUserResponse address(String address) {
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

  public DigitalDocumentUserResponse addressDetail(String addressDetail) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentUserResponse digitalDocumentUserResponse = (DigitalDocumentUserResponse) o;
    return Objects.equals(this.id, digitalDocumentUserResponse.id) &&
        Objects.equals(this.name, digitalDocumentUserResponse.name) &&
        Objects.equals(this.birthDate, digitalDocumentUserResponse.birthDate) &&
        Objects.equals(this.gender, digitalDocumentUserResponse.gender) &&
        Objects.equals(this.phoneNumber, digitalDocumentUserResponse.phoneNumber) &&
        Objects.equals(this.zipcode, digitalDocumentUserResponse.zipcode) &&
        Objects.equals(this.address, digitalDocumentUserResponse.address) &&
        Objects.equals(this.addressDetail, digitalDocumentUserResponse.addressDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, birthDate, gender, phoneNumber, zipcode, address, addressDetail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentUserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressDetail: ").append(toIndentedString(addressDetail)).append("\n");
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

