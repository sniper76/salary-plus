package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.AuthType;
import ag.act.model.Gender;
import ag.act.model.SimpleStockResponse;
import ag.act.model.Status;
import ag.act.model.UserBadgeVisibilityResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserResponse {

  private Long id;

  private String email;

  private String name;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant birthDate;

  private String phoneNumber = null;

  private Gender gender;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant lastPinNumberVerifiedAt = null;

  private String nickname = null;

  private String mySpeech = null;

  private String jobTitle = null;

  private String address = null;

  private String addressDetail = null;

  private String zipcode = null;

  private Long totalAssetAmount;

  private String profileImageUrl = null;

  private Status status;

  private AuthType authType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant lastNicknameUpdatedAt = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant editedAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant deletedAt = null;

  private Boolean isPinNumberRegistered;

  private Boolean isAgreeToReceiveMail = null;

  @Valid
  private List<@Valid UserBadgeVisibilityResponse> userBadgeVisibilities;

  private Boolean isChangePasswordRequired = null;

  private Boolean isAdmin = null;

  private Boolean isSolidarityLeaderConfidentialAgreementSigned;

  @Valid
  private List<String> leadingSolidarityStockCodes;

  @Valid
  private List<String> roles;

  @Valid
  private List<@Valid SimpleStockResponse> holdingStocks;

  public UserResponse id(Long id) {
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

  public UserResponse email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserResponse name(String name) {
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

  public UserResponse birthDate(java.time.Instant birthDate) {
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

  public UserResponse phoneNumber(String phoneNumber) {
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

  public UserResponse gender(Gender gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
  */
  @Valid 
  @JsonProperty("gender")
  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public UserResponse lastPinNumberVerifiedAt(java.time.Instant lastPinNumberVerifiedAt) {
    this.lastPinNumberVerifiedAt = lastPinNumberVerifiedAt;
    return this;
  }

  /**
   * Get lastPinNumberVerifiedAt
   * @return lastPinNumberVerifiedAt
  */
  @Valid 
  @JsonProperty("lastPinNumberVerifiedAt")
  public java.time.Instant getLastPinNumberVerifiedAt() {
    return lastPinNumberVerifiedAt;
  }

  public void setLastPinNumberVerifiedAt(java.time.Instant lastPinNumberVerifiedAt) {
    this.lastPinNumberVerifiedAt = lastPinNumberVerifiedAt;
  }

  public UserResponse nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  /**
   * Get nickname
   * @return nickname
  */
  
  @JsonProperty("nickname")
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public UserResponse mySpeech(String mySpeech) {
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

  public UserResponse jobTitle(String jobTitle) {
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

  public UserResponse address(String address) {
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

  public UserResponse addressDetail(String addressDetail) {
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

  public UserResponse zipcode(String zipcode) {
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

  public UserResponse totalAssetAmount(Long totalAssetAmount) {
    this.totalAssetAmount = totalAssetAmount;
    return this;
  }

  /**
   * Get totalAssetAmount
   * @return totalAssetAmount
  */
  
  @JsonProperty("totalAssetAmount")
  public Long getTotalAssetAmount() {
    return totalAssetAmount;
  }

  public void setTotalAssetAmount(Long totalAssetAmount) {
    this.totalAssetAmount = totalAssetAmount;
  }

  public UserResponse profileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  /**
   * Get profileImageUrl
   * @return profileImageUrl
  */
  
  @JsonProperty("profileImageUrl")
  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public UserResponse status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @JsonProperty("status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public UserResponse authType(AuthType authType) {
    this.authType = authType;
    return this;
  }

  /**
   * Get authType
   * @return authType
  */
  @Valid 
  @JsonProperty("authType")
  public AuthType getAuthType() {
    return authType;
  }

  public void setAuthType(AuthType authType) {
    this.authType = authType;
  }

  public UserResponse lastNicknameUpdatedAt(java.time.Instant lastNicknameUpdatedAt) {
    this.lastNicknameUpdatedAt = lastNicknameUpdatedAt;
    return this;
  }

  /**
   * Get lastNicknameUpdatedAt
   * @return lastNicknameUpdatedAt
  */
  @Valid 
  @JsonProperty("lastNicknameUpdatedAt")
  public java.time.Instant getLastNicknameUpdatedAt() {
    return lastNicknameUpdatedAt;
  }

  public void setLastNicknameUpdatedAt(java.time.Instant lastNicknameUpdatedAt) {
    this.lastNicknameUpdatedAt = lastNicknameUpdatedAt;
  }

  public UserResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  */
  @Valid 
  @JsonProperty("createdAt")
  public java.time.Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
  }

  public UserResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  */
  @Valid 
  @JsonProperty("updatedAt")
  public java.time.Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UserResponse editedAt(java.time.Instant editedAt) {
    this.editedAt = editedAt;
    return this;
  }

  /**
   * Get editedAt
   * @return editedAt
  */
  @Valid 
  @JsonProperty("editedAt")
  public java.time.Instant getEditedAt() {
    return editedAt;
  }

  public void setEditedAt(java.time.Instant editedAt) {
    this.editedAt = editedAt;
  }

  public UserResponse deletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
    return this;
  }

  /**
   * Get deletedAt
   * @return deletedAt
  */
  @Valid 
  @JsonProperty("deletedAt")
  public java.time.Instant getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(java.time.Instant deletedAt) {
    this.deletedAt = deletedAt;
  }

  public UserResponse isPinNumberRegistered(Boolean isPinNumberRegistered) {
    this.isPinNumberRegistered = isPinNumberRegistered;
    return this;
  }

  /**
   * Get isPinNumberRegistered
   * @return isPinNumberRegistered
  */
  
  @JsonProperty("isPinNumberRegistered")
  public Boolean getIsPinNumberRegistered() {
    return isPinNumberRegistered;
  }

  public void setIsPinNumberRegistered(Boolean isPinNumberRegistered) {
    this.isPinNumberRegistered = isPinNumberRegistered;
  }

  public UserResponse isAgreeToReceiveMail(Boolean isAgreeToReceiveMail) {
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

  public UserResponse userBadgeVisibilities(List<@Valid UserBadgeVisibilityResponse> userBadgeVisibilities) {
    this.userBadgeVisibilities = userBadgeVisibilities;
    return this;
  }

  public UserResponse addUserBadgeVisibilitiesItem(UserBadgeVisibilityResponse userBadgeVisibilitiesItem) {
    if (this.userBadgeVisibilities == null) {
      this.userBadgeVisibilities = new ArrayList<>();
    }
    this.userBadgeVisibilities.add(userBadgeVisibilitiesItem);
    return this;
  }

  /**
   * Get userBadgeVisibilities
   * @return userBadgeVisibilities
  */
  @Valid 
  @JsonProperty("userBadgeVisibilities")
  public List<@Valid UserBadgeVisibilityResponse> getUserBadgeVisibilities() {
    return userBadgeVisibilities;
  }

  public void setUserBadgeVisibilities(List<@Valid UserBadgeVisibilityResponse> userBadgeVisibilities) {
    this.userBadgeVisibilities = userBadgeVisibilities;
  }

  public UserResponse isChangePasswordRequired(Boolean isChangePasswordRequired) {
    this.isChangePasswordRequired = isChangePasswordRequired;
    return this;
  }

  /**
   * Get isChangePasswordRequired
   * @return isChangePasswordRequired
  */
  
  @JsonProperty("isChangePasswordRequired")
  public Boolean getIsChangePasswordRequired() {
    return isChangePasswordRequired;
  }

  public void setIsChangePasswordRequired(Boolean isChangePasswordRequired) {
    this.isChangePasswordRequired = isChangePasswordRequired;
  }

  public UserResponse isAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }

  /**
   * Get isAdmin
   * @return isAdmin
  */
  
  @JsonProperty("isAdmin")
  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public UserResponse isSolidarityLeaderConfidentialAgreementSigned(Boolean isSolidarityLeaderConfidentialAgreementSigned) {
    this.isSolidarityLeaderConfidentialAgreementSigned = isSolidarityLeaderConfidentialAgreementSigned;
    return this;
  }

  /**
   * 주주연대 대표 비밀유지계약서 서명 여부
   * @return isSolidarityLeaderConfidentialAgreementSigned
  */
  
  @JsonProperty("isSolidarityLeaderConfidentialAgreementSigned")
  public Boolean getIsSolidarityLeaderConfidentialAgreementSigned() {
    return isSolidarityLeaderConfidentialAgreementSigned;
  }

  public void setIsSolidarityLeaderConfidentialAgreementSigned(Boolean isSolidarityLeaderConfidentialAgreementSigned) {
    this.isSolidarityLeaderConfidentialAgreementSigned = isSolidarityLeaderConfidentialAgreementSigned;
  }

  public UserResponse leadingSolidarityStockCodes(List<String> leadingSolidarityStockCodes) {
    this.leadingSolidarityStockCodes = leadingSolidarityStockCodes;
    return this;
  }

  public UserResponse addLeadingSolidarityStockCodesItem(String leadingSolidarityStockCodesItem) {
    if (this.leadingSolidarityStockCodes == null) {
      this.leadingSolidarityStockCodes = new ArrayList<>();
    }
    this.leadingSolidarityStockCodes.add(leadingSolidarityStockCodesItem);
    return this;
  }

  /**
   * Get leadingSolidarityStockCodes
   * @return leadingSolidarityStockCodes
  */
  
  @JsonProperty("leadingSolidarityStockCodes")
  public List<String> getLeadingSolidarityStockCodes() {
    return leadingSolidarityStockCodes;
  }

  public void setLeadingSolidarityStockCodes(List<String> leadingSolidarityStockCodes) {
    this.leadingSolidarityStockCodes = leadingSolidarityStockCodes;
  }

  public UserResponse roles(List<String> roles) {
    this.roles = roles;
    return this;
  }

  public UserResponse addRolesItem(String rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
  */
  
  @JsonProperty("roles")
  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public UserResponse holdingStocks(List<@Valid SimpleStockResponse> holdingStocks) {
    this.holdingStocks = holdingStocks;
    return this;
  }

  public UserResponse addHoldingStocksItem(SimpleStockResponse holdingStocksItem) {
    if (this.holdingStocks == null) {
      this.holdingStocks = new ArrayList<>();
    }
    this.holdingStocks.add(holdingStocksItem);
    return this;
  }

  /**
   * Get holdingStocks
   * @return holdingStocks
  */
  @Valid 
  @JsonProperty("holdingStocks")
  public List<@Valid SimpleStockResponse> getHoldingStocks() {
    return holdingStocks;
  }

  public void setHoldingStocks(List<@Valid SimpleStockResponse> holdingStocks) {
    this.holdingStocks = holdingStocks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserResponse userResponse = (UserResponse) o;
    return Objects.equals(this.id, userResponse.id) &&
        Objects.equals(this.email, userResponse.email) &&
        Objects.equals(this.name, userResponse.name) &&
        Objects.equals(this.birthDate, userResponse.birthDate) &&
        Objects.equals(this.phoneNumber, userResponse.phoneNumber) &&
        Objects.equals(this.gender, userResponse.gender) &&
        Objects.equals(this.lastPinNumberVerifiedAt, userResponse.lastPinNumberVerifiedAt) &&
        Objects.equals(this.nickname, userResponse.nickname) &&
        Objects.equals(this.mySpeech, userResponse.mySpeech) &&
        Objects.equals(this.jobTitle, userResponse.jobTitle) &&
        Objects.equals(this.address, userResponse.address) &&
        Objects.equals(this.addressDetail, userResponse.addressDetail) &&
        Objects.equals(this.zipcode, userResponse.zipcode) &&
        Objects.equals(this.totalAssetAmount, userResponse.totalAssetAmount) &&
        Objects.equals(this.profileImageUrl, userResponse.profileImageUrl) &&
        Objects.equals(this.status, userResponse.status) &&
        Objects.equals(this.authType, userResponse.authType) &&
        Objects.equals(this.lastNicknameUpdatedAt, userResponse.lastNicknameUpdatedAt) &&
        Objects.equals(this.createdAt, userResponse.createdAt) &&
        Objects.equals(this.updatedAt, userResponse.updatedAt) &&
        Objects.equals(this.editedAt, userResponse.editedAt) &&
        Objects.equals(this.deletedAt, userResponse.deletedAt) &&
        Objects.equals(this.isPinNumberRegistered, userResponse.isPinNumberRegistered) &&
        Objects.equals(this.isAgreeToReceiveMail, userResponse.isAgreeToReceiveMail) &&
        Objects.equals(this.userBadgeVisibilities, userResponse.userBadgeVisibilities) &&
        Objects.equals(this.isChangePasswordRequired, userResponse.isChangePasswordRequired) &&
        Objects.equals(this.isAdmin, userResponse.isAdmin) &&
        Objects.equals(this.isSolidarityLeaderConfidentialAgreementSigned, userResponse.isSolidarityLeaderConfidentialAgreementSigned) &&
        Objects.equals(this.leadingSolidarityStockCodes, userResponse.leadingSolidarityStockCodes) &&
        Objects.equals(this.roles, userResponse.roles) &&
        Objects.equals(this.holdingStocks, userResponse.holdingStocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, birthDate, phoneNumber, gender, lastPinNumberVerifiedAt, nickname, mySpeech, jobTitle, address, addressDetail, zipcode, totalAssetAmount, profileImageUrl, status, authType, lastNicknameUpdatedAt, createdAt, updatedAt, editedAt, deletedAt, isPinNumberRegistered, isAgreeToReceiveMail, userBadgeVisibilities, isChangePasswordRequired, isAdmin, isSolidarityLeaderConfidentialAgreementSigned, leadingSolidarityStockCodes, roles, holdingStocks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    lastPinNumberVerifiedAt: ").append(toIndentedString(lastPinNumberVerifiedAt)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    mySpeech: ").append(toIndentedString(mySpeech)).append("\n");
    sb.append("    jobTitle: ").append(toIndentedString(jobTitle)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressDetail: ").append(toIndentedString(addressDetail)).append("\n");
    sb.append("    zipcode: ").append(toIndentedString(zipcode)).append("\n");
    sb.append("    totalAssetAmount: ").append(toIndentedString(totalAssetAmount)).append("\n");
    sb.append("    profileImageUrl: ").append(toIndentedString(profileImageUrl)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    authType: ").append(toIndentedString(authType)).append("\n");
    sb.append("    lastNicknameUpdatedAt: ").append(toIndentedString(lastNicknameUpdatedAt)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    editedAt: ").append(toIndentedString(editedAt)).append("\n");
    sb.append("    deletedAt: ").append(toIndentedString(deletedAt)).append("\n");
    sb.append("    isPinNumberRegistered: ").append(toIndentedString(isPinNumberRegistered)).append("\n");
    sb.append("    isAgreeToReceiveMail: ").append(toIndentedString(isAgreeToReceiveMail)).append("\n");
    sb.append("    userBadgeVisibilities: ").append(toIndentedString(userBadgeVisibilities)).append("\n");
    sb.append("    isChangePasswordRequired: ").append(toIndentedString(isChangePasswordRequired)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
    sb.append("    isSolidarityLeaderConfidentialAgreementSigned: ").append(toIndentedString(isSolidarityLeaderConfidentialAgreementSigned)).append("\n");
    sb.append("    leadingSolidarityStockCodes: ").append(toIndentedString(leadingSolidarityStockCodes)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("    holdingStocks: ").append(toIndentedString(holdingStocks)).append("\n");
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

