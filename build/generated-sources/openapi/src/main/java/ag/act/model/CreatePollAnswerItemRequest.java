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
 * CreatePollAnswerItemRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreatePollAnswerItemRequest {

  private Long pollItemId;

  public CreatePollAnswerItemRequest pollItemId(Long pollItemId) {
    this.pollItemId = pollItemId;
    return this;
  }

  /**
   * Get pollItemId
   * @return pollItemId
  */
  
  @JsonProperty("pollItemId")
  public Long getPollItemId() {
    return pollItemId;
  }

  public void setPollItemId(Long pollItemId) {
    this.pollItemId = pollItemId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePollAnswerItemRequest createPollAnswerItemRequest = (CreatePollAnswerItemRequest) o;
    return Objects.equals(this.pollItemId, createPollAnswerItemRequest.pollItemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pollItemId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePollAnswerItemRequest {\n");
    sb.append("    pollItemId: ").append(toIndentedString(pollItemId)).append("\n");
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

