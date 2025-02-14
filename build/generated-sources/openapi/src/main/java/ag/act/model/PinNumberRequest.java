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
 * PinNumberRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PinNumberRequest {

  @Pattern(regexp = "^[0-9]{6}$", message = "핀번호를 확인해주세요.")
@Size(min = 6, max = 6, message = "핀번호를 확인해주세요.")

  private String pinNumber;

  public PinNumberRequest pinNumber(String pinNumber) {
    this.pinNumber = pinNumber;
    return this;
  }

  /**
   * Get pinNumber
   * @return pinNumber
  */
  
  @JsonProperty("pinNumber")
  public String getPinNumber() {
    return pinNumber;
  }

  public void setPinNumber(String pinNumber) {
    this.pinNumber = pinNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PinNumberRequest pinNumberRequest = (PinNumberRequest) o;
    return Objects.equals(this.pinNumber, pinNumberRequest.pinNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pinNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PinNumberRequest {\n");
    sb.append("    pinNumber: ").append(toIndentedString(pinNumber)).append("\n");
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

