package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SimpleStockResponse;
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
 * SimplePostResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SimplePostResponse {

  private Long postId;

  private SimpleStockResponse stock;

  public SimplePostResponse postId(Long postId) {
    this.postId = postId;
    return this;
  }

  /**
   * Get postId
   * @return postId
  */
  
  @JsonProperty("postId")
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public SimplePostResponse stock(SimpleStockResponse stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * @return stock
  */
  @Valid 
  @JsonProperty("stock")
  public SimpleStockResponse getStock() {
    return stock;
  }

  public void setStock(SimpleStockResponse stock) {
    this.stock = stock;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimplePostResponse simplePostResponse = (SimplePostResponse) o;
    return Objects.equals(this.postId, simplePostResponse.postId) &&
        Objects.equals(this.stock, simplePostResponse.stock);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postId, stock);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimplePostResponse {\n");
    sb.append("    postId: ").append(toIndentedString(postId)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
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

