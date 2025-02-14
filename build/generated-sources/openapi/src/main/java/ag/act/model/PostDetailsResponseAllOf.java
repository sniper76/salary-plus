package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentResponse;
import ag.act.model.DigitalProxyResponse;
import ag.act.model.PollResponse;
import ag.act.model.PushDetailsResponse;
import ag.act.model.SolidarityLeaderElectionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * PostDetailsResponseAllOf
 */

@JsonTypeName("PostDetailsResponse_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PostDetailsResponseAllOf {

  private PushDetailsResponse push;

  private PollResponse poll;

  @Valid
  private List<@Valid PollResponse> polls;

  private DigitalProxyResponse digitalProxy;

  private DigitalDocumentResponse digitalDocument;

  private SolidarityLeaderElectionResponse election;

  public PostDetailsResponseAllOf push(PushDetailsResponse push) {
    this.push = push;
    return this;
  }

  /**
   * Get push
   * @return push
  */
  @Valid 
  @JsonProperty("push")
  public PushDetailsResponse getPush() {
    return push;
  }

  public void setPush(PushDetailsResponse push) {
    this.push = push;
  }

  public PostDetailsResponseAllOf poll(PollResponse poll) {
    this.poll = poll;
    return this;
  }

  /**
   * Get poll
   * @return poll
  */
  @Valid 
  @JsonProperty("poll")
  public PollResponse getPoll() {
    return poll;
  }

  public void setPoll(PollResponse poll) {
    this.poll = poll;
  }

  public PostDetailsResponseAllOf polls(List<@Valid PollResponse> polls) {
    this.polls = polls;
    return this;
  }

  public PostDetailsResponseAllOf addPollsItem(PollResponse pollsItem) {
    if (this.polls == null) {
      this.polls = new ArrayList<>();
    }
    this.polls.add(pollsItem);
    return this;
  }

  /**
   * Get polls
   * @return polls
  */
  @Valid 
  @JsonProperty("polls")
  public List<@Valid PollResponse> getPolls() {
    return polls;
  }

  public void setPolls(List<@Valid PollResponse> polls) {
    this.polls = polls;
  }

  public PostDetailsResponseAllOf digitalProxy(DigitalProxyResponse digitalProxy) {
    this.digitalProxy = digitalProxy;
    return this;
  }

  /**
   * Get digitalProxy
   * @return digitalProxy
  */
  @Valid 
  @JsonProperty("digitalProxy")
  public DigitalProxyResponse getDigitalProxy() {
    return digitalProxy;
  }

  public void setDigitalProxy(DigitalProxyResponse digitalProxy) {
    this.digitalProxy = digitalProxy;
  }

  public PostDetailsResponseAllOf digitalDocument(DigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
    return this;
  }

  /**
   * Get digitalDocument
   * @return digitalDocument
  */
  @Valid 
  @JsonProperty("digitalDocument")
  public DigitalDocumentResponse getDigitalDocument() {
    return digitalDocument;
  }

  public void setDigitalDocument(DigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
  }

  public PostDetailsResponseAllOf election(SolidarityLeaderElectionResponse election) {
    this.election = election;
    return this;
  }

  /**
   * Get election
   * @return election
  */
  @Valid 
  @JsonProperty("election")
  public SolidarityLeaderElectionResponse getElection() {
    return election;
  }

  public void setElection(SolidarityLeaderElectionResponse election) {
    this.election = election;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostDetailsResponseAllOf postDetailsResponseAllOf = (PostDetailsResponseAllOf) o;
    return Objects.equals(this.push, postDetailsResponseAllOf.push) &&
        Objects.equals(this.poll, postDetailsResponseAllOf.poll) &&
        Objects.equals(this.polls, postDetailsResponseAllOf.polls) &&
        Objects.equals(this.digitalProxy, postDetailsResponseAllOf.digitalProxy) &&
        Objects.equals(this.digitalDocument, postDetailsResponseAllOf.digitalDocument) &&
        Objects.equals(this.election, postDetailsResponseAllOf.election);
  }

  @Override
  public int hashCode() {
    return Objects.hash(push, poll, polls, digitalProxy, digitalDocument, election);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostDetailsResponseAllOf {\n");
    sb.append("    push: ").append(toIndentedString(push)).append("\n");
    sb.append("    poll: ").append(toIndentedString(poll)).append("\n");
    sb.append("    polls: ").append(toIndentedString(polls)).append("\n");
    sb.append("    digitalProxy: ").append(toIndentedString(digitalProxy)).append("\n");
    sb.append("    digitalDocument: ").append(toIndentedString(digitalDocument)).append("\n");
    sb.append("    election: ").append(toIndentedString(election)).append("\n");
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

