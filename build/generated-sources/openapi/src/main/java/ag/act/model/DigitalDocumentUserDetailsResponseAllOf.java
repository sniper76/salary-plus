package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * DigitalDocumentUserDetailsResponseAllOf
 */

@JsonTypeName("DigitalDocumentUserDetailsResponse_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentUserDetailsResponseAllOf {

  private Long userId;

  private Long digitalDocumentId;

  private Long issuedNumber;

  public DigitalDocumentUserDetailsResponseAllOf userId(Long userId) {
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

  public DigitalDocumentUserDetailsResponseAllOf digitalDocumentId(Long digitalDocumentId) {
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

  public DigitalDocumentUserDetailsResponseAllOf issuedNumber(Long issuedNumber) {
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
    DigitalDocumentUserDetailsResponseAllOf digitalDocumentUserDetailsResponseAllOf = (DigitalDocumentUserDetailsResponseAllOf) o;
    return Objects.equals(this.userId, digitalDocumentUserDetailsResponseAllOf.userId) &&
        Objects.equals(this.digitalDocumentId, digitalDocumentUserDetailsResponseAllOf.digitalDocumentId) &&
        Objects.equals(this.issuedNumber, digitalDocumentUserDetailsResponseAllOf.issuedNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, digitalDocumentId, issuedNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentUserDetailsResponseAllOf {\n");
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

