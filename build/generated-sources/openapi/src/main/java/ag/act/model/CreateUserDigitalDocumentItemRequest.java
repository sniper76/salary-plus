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
 * CreateUserDigitalDocumentItemRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateUserDigitalDocumentItemRequest {

  private Long itemId;

  private String userSelectValue;

  public CreateUserDigitalDocumentItemRequest itemId(Long itemId) {
    this.itemId = itemId;
    return this;
  }

  /**
   * 전자문서 의안 아이디
   * @return itemId
  */
  
  @JsonProperty("itemId")
  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public CreateUserDigitalDocumentItemRequest userSelectValue(String userSelectValue) {
    this.userSelectValue = userSelectValue;
    return this;
  }

  /**
   * 사용자 선택값
   * @return userSelectValue
  */
  
  @JsonProperty("userSelectValue")
  public String getUserSelectValue() {
    return userSelectValue;
  }

  public void setUserSelectValue(String userSelectValue) {
    this.userSelectValue = userSelectValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserDigitalDocumentItemRequest createUserDigitalDocumentItemRequest = (CreateUserDigitalDocumentItemRequest) o;
    return Objects.equals(this.itemId, createUserDigitalDocumentItemRequest.itemId) &&
        Objects.equals(this.userSelectValue, createUserDigitalDocumentItemRequest.userSelectValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, userSelectValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserDigitalDocumentItemRequest {\n");
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    userSelectValue: ").append(toIndentedString(userSelectValue)).append("\n");
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

