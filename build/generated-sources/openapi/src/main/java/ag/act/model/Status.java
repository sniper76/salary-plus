package ag.act.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets Status
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public enum Status {
  
  ACTIVE("ACTIVE"),
  
  PROCESSING("PROCESSING"),
  
  INACTIVE_BY_USER("INACTIVE_BY_USER"),
  
  INACTIVE_BY_ADMIN("INACTIVE_BY_ADMIN"),
  
  WITHDRAWAL_REQUESTED("WITHDRAWAL_REQUESTED"),
  
  DELETED_BY_USER("DELETED_BY_USER"),
  
  DELETED_BY_ADMIN("DELETED_BY_ADMIN"),
  
  DELETED("DELETED"),
  
  SCHEDULE("SCHEDULE");

  private String value;

  Status(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static Status fromValue(String value) {
    for (Status b : Status.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

