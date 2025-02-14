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
 * SendAuthRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SendAuthRequest {

  @IsPhoneNumber(message = "휴대폰 번호를 확인해주세요.")

  private String phoneNumber;

  @Pattern(regexp = "^(01|02|03|04|05|06)$", message = "휴대폰 통신사를 확인해 주세요.")

  private String provider;

  @IsGender(message = "성별을 확인해주세요.")

  private String gender;

  @Size(min = 8, max = 8, message = "생년월일을 8자리 ''YYYYMMDD'' 형식으로 입력해 주세요.")
@IsBirthDate(message = "생년월일을 8자리 ''YYYYMMDD'' 형식으로 입력해 주세요.")

  private String birthDate;

  @NotBlank(message = "이름을 확인해주세요.")

  private String name;

  @Pattern(regexp = "^[1-8]$", message = "주민번호 첫번째 자리를 확인해 주세요.")
@NotBlank(message = "주민번호 첫번째 자리를 확인해 주세요.")

  private String firstNumberOfIdentification;

  /**
   * Default constructor
   * @deprecated Use {@link SendAuthRequest#SendAuthRequest(String, String, String, String, String, String)}
   */
  @Deprecated
  public SendAuthRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SendAuthRequest(String phoneNumber, String provider, String gender, String birthDate, String name, String firstNumberOfIdentification) {
    this.phoneNumber = phoneNumber;
    this.provider = provider;
    this.gender = gender;
    this.birthDate = birthDate;
    this.name = name;
    this.firstNumberOfIdentification = firstNumberOfIdentification;
  }

  public SendAuthRequest phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
  */
  @NotNull 
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public SendAuthRequest provider(String provider) {
    this.provider = provider;
    return this;
  }

  /**
   * 통신사 구분(SKT: 01, KT: 02, LGU+: 03, 알뚤폰 SKT: 04, 알뜰폰 KT: 05, 알뜰폰 LGU+: 06)
   * @return provider
  */
  @NotNull 
  @JsonProperty("provider")
  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public SendAuthRequest gender(String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
  */
  @NotNull 
  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public SendAuthRequest birthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * Get birthDate
   * @return birthDate
  */
  @NotNull 
  @JsonProperty("birthDate")
  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public SendAuthRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull 
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SendAuthRequest firstNumberOfIdentification(String firstNumberOfIdentification) {
    this.firstNumberOfIdentification = firstNumberOfIdentification;
    return this;
  }

  /**
   * 주민번호 첫번째 자리 숫자 (1:1900년대생 남자,2:1900년대생 여자,3:2000년대생 남자,4:2000년대생 여자,5:1900년대생 외국 남자,6:1900년대생 외국 여자,7:2000년대생 외국 남자,8:2000년대생 외국 여자)
   * @return firstNumberOfIdentification
  */
  @NotNull 
  @JsonProperty("firstNumberOfIdentification")
  public String getFirstNumberOfIdentification() {
    return firstNumberOfIdentification;
  }

  public void setFirstNumberOfIdentification(String firstNumberOfIdentification) {
    this.firstNumberOfIdentification = firstNumberOfIdentification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SendAuthRequest sendAuthRequest = (SendAuthRequest) o;
    return Objects.equals(this.phoneNumber, sendAuthRequest.phoneNumber) &&
        Objects.equals(this.provider, sendAuthRequest.provider) &&
        Objects.equals(this.gender, sendAuthRequest.gender) &&
        Objects.equals(this.birthDate, sendAuthRequest.birthDate) &&
        Objects.equals(this.name, sendAuthRequest.name) &&
        Objects.equals(this.firstNumberOfIdentification, sendAuthRequest.firstNumberOfIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phoneNumber, provider, gender, birthDate, name, firstNumberOfIdentification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SendAuthRequest {\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    firstNumberOfIdentification: ").append(toIndentedString(firstNumberOfIdentification)).append("\n");
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

