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
 * 내 주소 변경 리퀘스트
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateMyAddressRequest {

  private String address;

  private String addressDetail;

  private String zipcode;

  public UpdateMyAddressRequest address(String address) {
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

  public UpdateMyAddressRequest addressDetail(String addressDetail) {
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

  public UpdateMyAddressRequest zipcode(String zipcode) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateMyAddressRequest updateMyAddressRequest = (UpdateMyAddressRequest) o;
    return Objects.equals(this.address, updateMyAddressRequest.address) &&
        Objects.equals(this.addressDetail, updateMyAddressRequest.addressDetail) &&
        Objects.equals(this.zipcode, updateMyAddressRequest.zipcode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, addressDetail, zipcode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateMyAddressRequest {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressDetail: ").append(toIndentedString(addressDetail)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
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

