package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * Digital Proxy for the post. It can be null.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateDigitalProxyRequest {

  @NotBlank(message = "Template Id 을 확인해주세요.")

  private String templateId;

  @NotBlank(message = "Template Name 을 확인해주세요.")

  private String templateName;

  @NotBlank(message = "Template Role 을 확인해주세요.")

  private String templateRole;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private String status = "ACTIVE";

  public CreateDigitalProxyRequest templateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

  /**
   * Template id of the Digital Proxy is mandatory
   * @return templateId
  */
  
  @JsonProperty("templateId")
  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public CreateDigitalProxyRequest templateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  /**
   * Template name of the Digital Proxy is mandatory
   * @return templateName
  */
  
  @JsonProperty("templateName")
  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public CreateDigitalProxyRequest templateRole(String templateRole) {
    this.templateRole = templateRole;
    return this;
  }

  /**
   * Template role of the Digital Proxy is mandatory
   * @return templateRole
  */
  
  @JsonProperty("templateRole")
  public String getTemplateRole() {
    return templateRole;
  }

  public void setTemplateRole(String templateRole) {
    this.templateRole = templateRole;
  }

  public CreateDigitalProxyRequest targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * The start date for the Digital Proxy is mandatory
   * @return targetStartDate
  */
  @Valid 
  @JsonProperty("targetStartDate")
  public java.time.Instant getTargetStartDate() {
    return targetStartDate;
  }

  public void setTargetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
  }

  public CreateDigitalProxyRequest targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * The end date for the Digital Proxy is mandatory
   * @return targetEndDate
  */
  @Valid 
  @JsonProperty("targetEndDate")
  public java.time.Instant getTargetEndDate() {
    return targetEndDate;
  }

  public void setTargetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
  }

  public CreateDigitalProxyRequest status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Status for the Digital Proxy ex) ACTIVE is mandatory
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateDigitalProxyRequest createDigitalProxyRequest = (CreateDigitalProxyRequest) o;
    return Objects.equals(this.templateId, createDigitalProxyRequest.templateId) &&
        Objects.equals(this.templateName, createDigitalProxyRequest.templateName) &&
        Objects.equals(this.templateRole, createDigitalProxyRequest.templateRole) &&
        Objects.equals(this.targetStartDate, createDigitalProxyRequest.targetStartDate) &&
        Objects.equals(this.targetEndDate, createDigitalProxyRequest.targetEndDate) &&
        Objects.equals(this.status, createDigitalProxyRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templateId, templateName, templateRole, targetStartDate, targetEndDate, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateDigitalProxyRequest {\n");
    sb.append("    templateId: ").append(toIndentedString(templateId)).append("\n");
    sb.append("    templateName: ").append(toIndentedString(templateName)).append("\n");
    sb.append("    templateRole: ").append(toIndentedString(templateRole)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

