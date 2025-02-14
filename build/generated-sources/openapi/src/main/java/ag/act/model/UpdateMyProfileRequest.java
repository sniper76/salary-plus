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
 * 내 정보 수정 모든 필드는 Optional
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UpdateMyProfileRequest {

  private String jobTitle;

  private String mySpeech;

  private String address;

  private String addressDetail;

  private String zipcode;

  private Boolean isAgreeToReceiveMail = true;

  private Boolean isVisibilityStockQuantity = true;

  private Boolean isVisibilityTotalAsset = true;

  public UpdateMyProfileRequest jobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
    return this;
  }

  /**
   * Get jobTitle
   * @return jobTitle
  */
  
  @JsonProperty("jobTitle")
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public UpdateMyProfileRequest mySpeech(String mySpeech) {
    this.mySpeech = mySpeech;
    return this;
  }

  /**
   * Get mySpeech
   * @return mySpeech
  */
  
  @JsonProperty("mySpeech")
  public String getMySpeech() {
    return mySpeech;
  }

  public void setMySpeech(String mySpeech) {
    this.mySpeech = mySpeech;
  }

  public UpdateMyProfileRequest address(String address) {
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

  public UpdateMyProfileRequest addressDetail(String addressDetail) {
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

  public UpdateMyProfileRequest zipcode(String zipcode) {
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

  public UpdateMyProfileRequest isAgreeToReceiveMail(Boolean isAgreeToReceiveMail) {
    this.isAgreeToReceiveMail = isAgreeToReceiveMail;
    return this;
  }

  /**
   * Get isAgreeToReceiveMail
   * @return isAgreeToReceiveMail
  */
  
  @JsonProperty("isAgreeToReceiveMail")
  public Boolean getIsAgreeToReceiveMail() {
    return isAgreeToReceiveMail;
  }

  public void setIsAgreeToReceiveMail(Boolean isAgreeToReceiveMail) {
    this.isAgreeToReceiveMail = isAgreeToReceiveMail;
  }

  public UpdateMyProfileRequest isVisibilityStockQuantity(Boolean isVisibilityStockQuantity) {
    this.isVisibilityStockQuantity = isVisibilityStockQuantity;
    return this;
  }

  /**
   * 주식수 설정
   * @return isVisibilityStockQuantity
  */
  
  @JsonProperty("isVisibilityStockQuantity")
  public Boolean getIsVisibilityStockQuantity() {
    return isVisibilityStockQuantity;
  }

  public void setIsVisibilityStockQuantity(Boolean isVisibilityStockQuantity) {
    this.isVisibilityStockQuantity = isVisibilityStockQuantity;
  }

  public UpdateMyProfileRequest isVisibilityTotalAsset(Boolean isVisibilityTotalAsset) {
    this.isVisibilityTotalAsset = isVisibilityTotalAsset;
    return this;
  }

  /**
   * 총자산 설정
   * @return isVisibilityTotalAsset
  */
  
  @JsonProperty("isVisibilityTotalAsset")
  public Boolean getIsVisibilityTotalAsset() {
    return isVisibilityTotalAsset;
  }

  public void setIsVisibilityTotalAsset(Boolean isVisibilityTotalAsset) {
    this.isVisibilityTotalAsset = isVisibilityTotalAsset;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateMyProfileRequest updateMyProfileRequest = (UpdateMyProfileRequest) o;
    return Objects.equals(this.jobTitle, updateMyProfileRequest.jobTitle) &&
        Objects.equals(this.mySpeech, updateMyProfileRequest.mySpeech) &&
        Objects.equals(this.address, updateMyProfileRequest.address) &&
        Objects.equals(this.addressDetail, updateMyProfileRequest.addressDetail) &&
        Objects.equals(this.zipcode, updateMyProfileRequest.zipcode) &&
        Objects.equals(this.isAgreeToReceiveMail, updateMyProfileRequest.isAgreeToReceiveMail) &&
        Objects.equals(this.isVisibilityStockQuantity, updateMyProfileRequest.isVisibilityStockQuantity) &&
        Objects.equals(this.isVisibilityTotalAsset, updateMyProfileRequest.isVisibilityTotalAsset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobTitle, mySpeech, address, addressDetail, zipcode, isAgreeToReceiveMail, isVisibilityStockQuantity, isVisibilityTotalAsset);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateMyProfileRequest {\n");
    sb.append("    jobTitle: ").append(toIndentedString(jobTitle)).append("\n");
    sb.append("    mySpeech: ").append(toIndentedString(mySpeech)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressDetail: ").append(toIndentedString(addressDetail)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
    sb.append("    isAgreeToReceiveMail: ").append(toIndentedString(isAgreeToReceiveMail)).append("\n");
    sb.append("    isVisibilityStockQuantity: ").append(toIndentedString(isVisibilityStockQuantity)).append("\n");
    sb.append("    isVisibilityTotalAsset: ").append(toIndentedString(isVisibilityTotalAsset)).append("\n");
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

