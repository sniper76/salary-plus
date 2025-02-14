package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UserPushAgreementItem
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserPushAgreementItem {

  private String title;

  @Valid
  private List<String> agreementTypes;

  private Boolean value;

  private String itemType;

  public UserPushAgreementItem title(String title) {
    this.title = title;
    return this;
  }

  /**
   * 화면에 표시될 토글의 이름
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UserPushAgreementItem agreementTypes(List<String> agreementTypes) {
    this.agreementTypes = agreementTypes;
    return this;
  }

  public UserPushAgreementItem addAgreementTypesItem(String agreementTypesItem) {
    if (this.agreementTypes == null) {
      this.agreementTypes = new ArrayList<>();
    }
    this.agreementTypes.add(agreementTypesItem);
    return this;
  }

  /**
   * 푸시 전송 타입. 베스트 진입, 새 댓글 등
   * @return agreementTypes
  */
  
  @JsonProperty("agreementTypes")
  public List<String> getAgreementTypes() {
    return agreementTypes;
  }

  public void setAgreementTypes(List<String> agreementTypes) {
    this.agreementTypes = agreementTypes;
  }

  public UserPushAgreementItem value(Boolean value) {
    this.value = value;
    return this;
  }

  /**
   * 유저 수신 동의 여부
   * @return value
  */
  
  @JsonProperty("value")
  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }

  public UserPushAgreementItem itemType(String itemType) {
    this.itemType = itemType;
    return this;
  }

  /**
   * 전체 ON/OFF 여부인지 아니면 개별 그룹에 대한 ON/OFF 여부인지
   * @return itemType
  */
  
  @JsonProperty("itemType")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPushAgreementItem userPushAgreementItem = (UserPushAgreementItem) o;
    return Objects.equals(this.title, userPushAgreementItem.title) &&
        Objects.equals(this.agreementTypes, userPushAgreementItem.agreementTypes) &&
        Objects.equals(this.value, userPushAgreementItem.value) &&
        Objects.equals(this.itemType, userPushAgreementItem.itemType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, agreementTypes, value, itemType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPushAgreementItem {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    agreementTypes: ").append(toIndentedString(agreementTypes)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    itemType: ").append(toIndentedString(itemType)).append("\n");
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

