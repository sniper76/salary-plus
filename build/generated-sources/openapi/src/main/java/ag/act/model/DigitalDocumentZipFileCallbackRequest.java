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
 * DigitalDocumentZipFileCallbackRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalDocumentZipFileCallbackRequest {

  private Long digitalDocumentDownloadId;

  private String zipFilePath;

  public DigitalDocumentZipFileCallbackRequest digitalDocumentDownloadId(Long digitalDocumentDownloadId) {
    this.digitalDocumentDownloadId = digitalDocumentDownloadId;
    return this;
  }

  /**
   * Digital document id parameter
   * @return digitalDocumentDownloadId
  */
  
  @JsonProperty("digitalDocumentDownloadId")
  public Long getDigitalDocumentDownloadId() {
    return digitalDocumentDownloadId;
  }

  public void setDigitalDocumentDownloadId(Long digitalDocumentDownloadId) {
    this.digitalDocumentDownloadId = digitalDocumentDownloadId;
  }

  public DigitalDocumentZipFileCallbackRequest zipFilePath(String zipFilePath) {
    this.zipFilePath = zipFilePath;
    return this;
  }

  /**
   * ZIP file URL
   * @return zipFilePath
  */
  
  @JsonProperty("zipFilePath")
  public String getZipFilePath() {
    return zipFilePath;
  }

  public void setZipFilePath(String zipFilePath) {
    this.zipFilePath = zipFilePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest = (DigitalDocumentZipFileCallbackRequest) o;
    return Objects.equals(this.digitalDocumentDownloadId, digitalDocumentZipFileCallbackRequest.digitalDocumentDownloadId) &&
        Objects.equals(this.zipFilePath, digitalDocumentZipFileCallbackRequest.zipFilePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(digitalDocumentDownloadId, zipFilePath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalDocumentZipFileCallbackRequest {\n");
    sb.append("    digitalDocumentDownloadId: ").append(toIndentedString(digitalDocumentDownloadId)).append("\n");
    sb.append("    zipFilePath: ").append(toIndentedString(zipFilePath)).append("\n");
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

