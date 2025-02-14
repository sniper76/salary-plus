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
 * DigitalDocumentDownloadResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentDownloadResponse {

  private Long id;

  private Long requestUserId;

  private String zipFileStatus;

  private String zipFileKey;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public DigitalDocumentDownloadResponse id(Long id) {
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

  public DigitalDocumentDownloadResponse requestUserId(Long requestUserId) {
    this.requestUserId = requestUserId;
    return this;
  }

  /**
   * Get requestUserId
   * @return requestUserId
  */
  
  @JsonProperty("requestUserId")
  public Long getRequestUserId() {
    return requestUserId;
  }

  public void setRequestUserId(Long requestUserId) {
    this.requestUserId = requestUserId;
  }

  public DigitalDocumentDownloadResponse zipFileStatus(String zipFileStatus) {
    this.zipFileStatus = zipFileStatus;
    return this;
  }

  /**
   * Get zipFileStatus
   * @return zipFileStatus
  */
  
  @JsonProperty("zipFileStatus")
  public String getZipFileStatus() {
    return zipFileStatus;
  }

  public void setZipFileStatus(String zipFileStatus) {
    this.zipFileStatus = zipFileStatus;
  }

  public DigitalDocumentDownloadResponse zipFileKey(String zipFileKey) {
    this.zipFileKey = zipFileKey;
    return this;
  }

  /**
   * Get zipFileKey
   * @return zipFileKey
  */
  
  @JsonProperty("zipFileKey")
  public String getZipFileKey() {
    return zipFileKey;
  }

  public void setZipFileKey(String zipFileKey) {
    this.zipFileKey = zipFileKey;
  }

  public DigitalDocumentDownloadResponse updatedAt(java.time.Instant updatedAt) {
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
    DigitalDocumentDownloadResponse digitalDocumentDownloadResponse = (DigitalDocumentDownloadResponse) o;
    return Objects.equals(this.id, digitalDocumentDownloadResponse.id) &&
        Objects.equals(this.requestUserId, digitalDocumentDownloadResponse.requestUserId) &&
        Objects.equals(this.zipFileStatus, digitalDocumentDownloadResponse.zipFileStatus) &&
        Objects.equals(this.zipFileKey, digitalDocumentDownloadResponse.zipFileKey) &&
        Objects.equals(this.updatedAt, digitalDocumentDownloadResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, requestUserId, zipFileStatus, zipFileKey, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentDownloadResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    requestUserId: ").append(toIndentedString(requestUserId)).append("\n");
    sb.append("    zipFileStatus: ").append(toIndentedString(zipFileStatus)).append("\n");
    sb.append("    zipFileKey: ").append(toIndentedString(zipFileKey)).append("\n");
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

