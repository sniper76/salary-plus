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
 * JsonTestStockUser
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class JsonTestStockUser {

  @Size(min = 1, max = 255, message = "이름을 확인하세요.")

  private String name;

  @Size(min = 8, max = 8, message = "생년월일을 8자리 ''YYYYMMDD'' 형식으로 입력해 주세요.")
@IsBirthDate(message = "생년월일을 8자리 ''YYYYMMDD'' 형식으로 입력해 주세요.")

  private String birthDate;

  public JsonTestStockUser name(String name) {
    this.name = name;
    return this;
  }

  /**
   * User name
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public JsonTestStockUser birthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * Get birthDate
   * @return birthDate
  */
  
  @JsonProperty("birthDate")
  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonTestStockUser jsonTestStockUser = (JsonTestStockUser) o;
    return Objects.equals(this.name, jsonTestStockUser.name) &&
        Objects.equals(this.birthDate, jsonTestStockUser.birthDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, birthDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonTestStockUser {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
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

