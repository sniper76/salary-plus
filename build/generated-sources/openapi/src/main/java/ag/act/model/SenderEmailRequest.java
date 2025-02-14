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
 * SenderEmailRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SenderEmailRequest {

  @Email(message = "형식에 맞지 않는 이메일입니다. ex) act123@naver.com", regexp="^[A-Za-z0-9][A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")

  private String senderEmail;

  @Email(message = "형식에 맞지 않는 이메일입니다. ex) act123@naver.com", regexp="^[A-Za-z0-9][A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")

  private String recipientEmail;

  @NotBlank(message = "이메일 제목을 확인해주세요.")

  private String subject;

  @NotBlank(message = "이메일 내용을 확인해주세요.")

  private String content;

  public SenderEmailRequest senderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
    return this;
  }

  /**
   * 발신자 이메일 주소
   * @return senderEmail
  */
  
  @JsonProperty("senderEmail")
  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }

  public SenderEmailRequest recipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
    return this;
  }

  /**
   * 수신자 이메일 주소
   * @return recipientEmail
  */
  
  @JsonProperty("recipientEmail")
  public String getRecipientEmail() {
    return recipientEmail;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  public SenderEmailRequest subject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * 이메일 제목
   * @return subject
  */
  
  @JsonProperty("subject")
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public SenderEmailRequest content(String content) {
    this.content = content;
    return this;
  }

  /**
   * 이메일 내용
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
    SenderEmailRequest senderEmailRequest = (SenderEmailRequest) o;
    return Objects.equals(this.senderEmail, senderEmailRequest.senderEmail) &&
        Objects.equals(this.recipientEmail, senderEmailRequest.recipientEmail) &&
        Objects.equals(this.subject, senderEmailRequest.subject) &&
        Objects.equals(this.content, senderEmailRequest.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(senderEmail, recipientEmail, subject, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SenderEmailRequest {\n");
    sb.append("    senderEmail: ").append(toIndentedString(senderEmail)).append("\n");
    sb.append("    recipientEmail: ").append(toIndentedString(recipientEmail)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
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

