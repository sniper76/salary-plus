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
 * RegisterUserInfoRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class RegisterUserInfoRequest {

  @Email(message = "형식에 맞지 않는 이메일입니다. ex) act123@naver.com", regexp="^[A-Za-z0-9][A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")

  private String email;

  @NotBlank(message = "닉네임을 확인해주세요.")

  private String nickname;

  @IsBoolean(message = "주주연대 공지 우편물 수신동의를 확인해주세요.")

  private Boolean isAgreeToReceiveMail;

  public RegisterUserInfoRequest email(String email) {
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

  public RegisterUserInfoRequest nickname(String nickname) {
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

  public RegisterUserInfoRequest isAgreeToReceiveMail(Boolean isAgreeToReceiveMail) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterUserInfoRequest registerUserInfoRequest = (RegisterUserInfoRequest) o;
    return Objects.equals(this.email, registerUserInfoRequest.email) &&
        Objects.equals(this.nickname, registerUserInfoRequest.nickname) &&
        Objects.equals(this.isAgreeToReceiveMail, registerUserInfoRequest.isAgreeToReceiveMail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, nickname, isAgreeToReceiveMail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterUserInfoRequest {\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    isAgreeToReceiveMail: ").append(toIndentedString(isAgreeToReceiveMail)).append("\n");
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

