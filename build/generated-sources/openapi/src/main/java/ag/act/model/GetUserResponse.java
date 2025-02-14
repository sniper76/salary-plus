package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.Paging;
import ag.act.model.UserResponse;
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
 * GetUserResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class GetUserResponse {

  private Paging paging;

  @Valid
  private List<@Valid UserResponse> data;

  public GetUserResponse paging(Paging paging) {
    this.paging = paging;
    return this;
  }

  /**
   * Get paging
   * @return paging
  */
  @Valid 
  @JsonProperty("paging")
  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  public GetUserResponse data(List<@Valid UserResponse> data) {
    this.data = data;
    return this;
  }

  public GetUserResponse addDataItem(UserResponse dataItem) {
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
  public List<@Valid UserResponse> getData() {
    return data;
  }

  public void setData(List<@Valid UserResponse> data) {
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
    GetUserResponse getUserResponse = (GetUserResponse) o;
    return Objects.equals(this.paging, getUserResponse.paging) &&
        Objects.equals(this.data, getUserResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paging, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetUserResponse {\n");
    sb.append("    paging: ").append(toIndentedString(paging)).append("\n");
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

