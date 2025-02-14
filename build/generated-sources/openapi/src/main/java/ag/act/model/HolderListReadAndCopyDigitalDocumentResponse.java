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
 * HolderListReadAndCopyDigitalDocumentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class HolderListReadAndCopyDigitalDocumentResponse {

  private Long digitalDocumentId;

  private String fileName;

  private Long userId;

  public HolderListReadAndCopyDigitalDocumentResponse digitalDocumentId(Long digitalDocumentId) {
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

  public HolderListReadAndCopyDigitalDocumentResponse fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
  */
  
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public HolderListReadAndCopyDigitalDocumentResponse userId(Long userId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocumentResponse = (HolderListReadAndCopyDigitalDocumentResponse) o;
    return Objects.equals(this.digitalDocumentId, holderListReadAndCopyDigitalDocumentResponse.digitalDocumentId) &&
        Objects.equals(this.fileName, holderListReadAndCopyDigitalDocumentResponse.fileName) &&
        Objects.equals(this.userId, holderListReadAndCopyDigitalDocumentResponse.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(digitalDocumentId, fileName, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HolderListReadAndCopyDigitalDocumentResponse {\n");
    sb.append("    digitalDocumentId: ").append(toIndentedString(digitalDocumentId)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

