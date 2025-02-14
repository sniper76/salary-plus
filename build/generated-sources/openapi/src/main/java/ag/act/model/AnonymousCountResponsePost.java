package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * AnonymousCountResponsePost
 */

@JsonTypeName("AnonymousCountResponse_post")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class AnonymousCountResponsePost {

  private Integer current;

  private Integer max;

  public AnonymousCountResponsePost current(Integer current) {
    this.current = current;
    return this;
  }

  /**
   * Get current
   * @return current
  */
  
  @JsonProperty("current")
  public Integer getCurrent() {
    return current;
  }

  public void setCurrent(Integer current) {
    this.current = current;
  }

  public AnonymousCountResponsePost max(Integer max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
  */
  
  @JsonProperty("max")
  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnonymousCountResponsePost anonymousCountResponsePost = (AnonymousCountResponsePost) o;
    return Objects.equals(this.current, anonymousCountResponsePost.current) &&
        Objects.equals(this.max, anonymousCountResponsePost.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(current, max);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnonymousCountResponsePost {\n");
    sb.append("    current: ").append(toIndentedString(current)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
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

