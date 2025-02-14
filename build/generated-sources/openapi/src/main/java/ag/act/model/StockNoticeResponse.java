package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.NoticeLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * StockNoticeResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockNoticeResponse {

  private NoticeLevel noticeLevel;

  private String message;

  public StockNoticeResponse noticeLevel(NoticeLevel noticeLevel) {
    this.noticeLevel = noticeLevel;
    return this;
  }

  /**
   * Get noticeLevel
   * @return noticeLevel
  */
  @Valid 
  @JsonProperty("noticeLevel")
  public NoticeLevel getNoticeLevel() {
    return noticeLevel;
  }

  public void setNoticeLevel(NoticeLevel noticeLevel) {
    this.noticeLevel = noticeLevel;
  }

  public StockNoticeResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockNoticeResponse stockNoticeResponse = (StockNoticeResponse) o;
    return Objects.equals(this.noticeLevel, stockNoticeResponse.noticeLevel) &&
        Objects.equals(this.message, stockNoticeResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(noticeLevel, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockNoticeResponse {\n");
    sb.append("    noticeLevel: ").append(toIndentedString(noticeLevel)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

