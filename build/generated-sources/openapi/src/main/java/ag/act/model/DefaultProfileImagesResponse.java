package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DefaultProfileImageResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * DefaultProfileImagesResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class DefaultProfileImagesResponse {

  @Valid
  private List<@Valid DefaultProfileImageResponse> data;

  public DefaultProfileImagesResponse data(List<@Valid DefaultProfileImageResponse> data) {
    this.data = data;
    return this;
  }

  public DefaultProfileImagesResponse addDataItem(DefaultProfileImageResponse dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @Valid 
  @JsonProperty("data")
  public List<@Valid DefaultProfileImageResponse> getData() {
    return data;
  }

  public void setData(List<@Valid DefaultProfileImageResponse> data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultProfileImagesResponse defaultProfileImagesResponse = (DefaultProfileImagesResponse) o;
    return Objects.equals(this.data, defaultProfileImagesResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DefaultProfileImagesResponse {\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

