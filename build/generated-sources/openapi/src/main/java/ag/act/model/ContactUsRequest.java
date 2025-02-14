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
 * ContactUsRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class ContactUsRequest {

  @NotBlank(message = "reCAPTCHA 요청값를 확인해주세요.")

  private String recaptchaResponse;

  @NotBlank(message = "이름을 확인해주세요.")

  private String senderName;

  @Email(message = "형식에 맞지 않는 이메일입니다. ex) act123@naver.com", regexp="^[A-Za-z0-9][A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")

  private String senderEmail;

  @IsPhoneNumber(message = "휴대폰번호를 확인해주세요.")

  private String phoneNumber;

  @NotBlank(message = "문의내용을 확인해주세요.")

  private String content;

  public ContactUsRequest recaptchaResponse(String recaptchaResponse) {
    this.recaptchaResponse = recaptchaResponse;
    return this;
  }

  /**
   * reCAPTCHA 요청값
   * @return recaptchaResponse
  */
  
  @JsonProperty("recaptchaResponse")
  public String getRecaptchaResponse() {
    return recaptchaResponse;
  }

  public void setRecaptchaResponse(String recaptchaResponse) {
    this.recaptchaResponse = recaptchaResponse;
  }

  public ContactUsRequest senderName(String senderName) {
    this.senderName = senderName;
    return this;
  }

  /**
   * 이름
   * @return senderName
  */
  
  @JsonProperty("senderName")
  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public ContactUsRequest senderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
    return this;
  }

  /**
   * 이메일 주소
   * @return senderEmail
  */
  
  @JsonProperty("senderEmail")
  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }

  public ContactUsRequest phoneNumber(String phoneNumber) {
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

  public ContactUsRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * 문의내용
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactUsRequest contactUsRequest = (ContactUsRequest) o;
    return Objects.equals(this.recaptchaResponse, contactUsRequest.recaptchaResponse) &&
        Objects.equals(this.senderName, contactUsRequest.senderName) &&
        Objects.equals(this.senderEmail, contactUsRequest.senderEmail) &&
        Objects.equals(this.phoneNumber, contactUsRequest.phoneNumber) &&
        Objects.equals(this.content, contactUsRequest.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recaptchaResponse, senderName, senderEmail, phoneNumber, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactUsRequest {\n");
    sb.append("    recaptchaResponse: ").append(toIndentedString(recaptchaResponse)).append("\n");
    sb.append("    senderName: ").append(toIndentedString(senderName)).append("\n");
    sb.append("    senderEmail: ").append(toIndentedString(senderEmail)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

