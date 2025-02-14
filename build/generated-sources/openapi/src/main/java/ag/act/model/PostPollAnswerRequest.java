package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.CreatePollAnswerItemRequest;
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
 * PostPollAnswerRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PostPollAnswerRequest {

  @Valid
  private List<@Valid CreatePollAnswerItemRequest> pollAnswer;

  public PostPollAnswerRequest pollAnswer(List<@Valid CreatePollAnswerItemRequest> pollAnswer) {
    this.pollAnswer = pollAnswer;
    return this;
  }

  public PostPollAnswerRequest addPollAnswerItem(CreatePollAnswerItemRequest pollAnswerItem) {
    if (this.pollAnswer == null) {
      this.pollAnswer = new ArrayList<>();
    }
    this.pollAnswer.add(pollAnswerItem);
    return this;
  }

  /**
   * Get pollAnswer
   * @return pollAnswer
  */
  @Valid 
  @JsonProperty("pollAnswer")
  public List<@Valid CreatePollAnswerItemRequest> getPollAnswer() {
    return pollAnswer;
  }

  public void setPollAnswer(List<@Valid CreatePollAnswerItemRequest> pollAnswer) {
    this.pollAnswer = pollAnswer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostPollAnswerRequest postPollAnswerRequest = (PostPollAnswerRequest) o;
    return Objects.equals(this.pollAnswer, postPollAnswerRequest.pollAnswer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pollAnswer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostPollAnswerRequest {\n");
    sb.append("    pollAnswer: ").append(toIndentedString(pollAnswer)).append("\n");
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

