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
 * DigitalProxySignResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DigitalProxySignResponse {

  private String embeddedUrl;

  public DigitalProxySignResponse embeddedUrl(String embeddedUrl) {
    this.embeddedUrl = embeddedUrl;
    return this;
  }

  /**
   * Get embeddedUrl
   * @return embeddedUrl
  */
  
  @JsonProperty("embeddedUrl")
  public String getEmbeddedUrl() {
    return embeddedUrl;
  }

  public void setEmbeddedUrl(String embeddedUrl) {
    this.embeddedUrl = embeddedUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DigitalProxySignResponse digitalProxySignResponse = (DigitalProxySignResponse) o;
    return Objects.equals(this.embeddedUrl, digitalProxySignResponse.embeddedUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(embeddedUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DigitalProxySignResponse {\n");
    sb.append("    embeddedUrl: ").append(toIndentedString(embeddedUrl)).append("\n");
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

