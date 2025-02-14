package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.JsonTestStockUser;
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
 * CreateOrUpdateTestStockRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CreateOrUpdateTestStockRequest {

  private JsonTestStockUser solidarityLeader;

  @Valid
  private List<@Valid JsonTestStockUser> users;

  public CreateOrUpdateTestStockRequest solidarityLeader(JsonTestStockUser solidarityLeader) {
    this.solidarityLeader = solidarityLeader;
    return this;
  }

  /**
   * Get solidarityLeader
   * @return solidarityLeader
  */
  @Valid 
  @JsonProperty("solidarityLeader")
  public JsonTestStockUser getSolidarityLeader() {
    return solidarityLeader;
  }

  public void setSolidarityLeader(JsonTestStockUser solidarityLeader) {
    this.solidarityLeader = solidarityLeader;
  }

  public CreateOrUpdateTestStockRequest users(List<@Valid JsonTestStockUser> users) {
    this.users = users;
    return this;
  }

  public CreateOrUpdateTestStockRequest addUsersItem(JsonTestStockUser usersItem) {
    if (this.users == null) {
      this.users = new ArrayList<>();
    }
    this.users.add(usersItem);
    return this;
  }

  /**
   * Get users
   * @return users
  */
  @Valid 
  @JsonProperty("users")
  public List<@Valid JsonTestStockUser> getUsers() {
    return users;
  }

  public void setUsers(List<@Valid JsonTestStockUser> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateOrUpdateTestStockRequest createOrUpdateTestStockRequest = (CreateOrUpdateTestStockRequest) o;
    return Objects.equals(this.solidarityLeader, createOrUpdateTestStockRequest.solidarityLeader) &&
        Objects.equals(this.users, createOrUpdateTestStockRequest.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityLeader, users);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateOrUpdateTestStockRequest {\n");
    sb.append("    solidarityLeader: ").append(toIndentedString(solidarityLeader)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

