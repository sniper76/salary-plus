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
 * DigitalProxyResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalProxyResponse {

  private Long id;

  private String templateId;

  private String templateName;

  private String templateRole;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public DigitalProxyResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DigitalProxyResponse templateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

  /**
   * Get templateId
   * @return templateId
  */
  
  @JsonProperty("templateId")
  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public DigitalProxyResponse templateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  /**
   * Get templateName
   * @return templateName
  */
  
  @JsonProperty("templateName")
  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public DigitalProxyResponse templateRole(String templateRole) {
    this.templateRole = templateRole;
    return this;
  }

  /**
   * Get templateRole
   * @return templateRole
  */
  
  @JsonProperty("templateRole")
  public String getTemplateRole() {
    return templateRole;
  }

  public void setTemplateRole(String templateRole) {
    this.templateRole = templateRole;
  }

  public DigitalProxyResponse targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * Get targetStartDate
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

  public DigitalProxyResponse targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * Get targetEndDate
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

  public DigitalProxyResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public DigitalProxyResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  */
  @Valid 
  @JsonProperty("createdAt")
  public java.time.Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
  }

  public DigitalProxyResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  */
  @Valid 
  @JsonProperty("updatedAt")
  public java.time.Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalProxyResponse digitalProxyResponse = (DigitalProxyResponse) o;
    return Objects.equals(this.id, digitalProxyResponse.id) &&
        Objects.equals(this.templateId, digitalProxyResponse.templateId) &&
        Objects.equals(this.templateName, digitalProxyResponse.templateName) &&
        Objects.equals(this.templateRole, digitalProxyResponse.templateRole) &&
        Objects.equals(this.targetStartDate, digitalProxyResponse.targetStartDate) &&
        Objects.equals(this.targetEndDate, digitalProxyResponse.targetEndDate) &&
        Objects.equals(this.status, digitalProxyResponse.status) &&
        Objects.equals(this.createdAt, digitalProxyResponse.createdAt) &&
        Objects.equals(this.updatedAt, digitalProxyResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, templateId, templateName, templateRole, targetStartDate, targetEndDate, status, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalProxyResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    templateId: ").append(toIndentedString(templateId)).append("\n");
    sb.append("    templateName: ").append(toIndentedString(templateName)).append("\n");
    sb.append("    templateRole: ").append(toIndentedString(templateRole)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

