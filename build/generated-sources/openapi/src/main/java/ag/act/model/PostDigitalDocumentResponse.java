package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.Status;
import ag.act.model.StockResponse;
import ag.act.model.UserDigitalDocumentResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * PostDigitalDocumentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class PostDigitalDocumentResponse {

  private Long id;

  private String title;

  private String content = null;

  private Status status;

  private StockResponse stock;

  private UserDigitalDocumentResponse digitalDocument;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant updatedAt;

  public PostDigitalDocumentResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PostDigitalDocumentResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PostDigitalDocumentResponse content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
  */
  
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public PostDigitalDocumentResponse status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @JsonProperty("status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public PostDigitalDocumentResponse stock(StockResponse stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * @return stock
  */
  @Valid 
  @JsonProperty("stock")
  public StockResponse getStock() {
    return stock;
  }

  public void setStock(StockResponse stock) {
    this.stock = stock;
  }

  public PostDigitalDocumentResponse digitalDocument(UserDigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
    return this;
  }

  /**
   * Get digitalDocument
   * @return digitalDocument
  */
  @Valid 
  @JsonProperty("digitalDocument")
  public UserDigitalDocumentResponse getDigitalDocument() {
    return digitalDocument;
  }

  public void setDigitalDocument(UserDigitalDocumentResponse digitalDocument) {
    this.digitalDocument = digitalDocument;
  }

  public PostDigitalDocumentResponse createdAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  */
  @Valid 
  @JsonProperty("createdAt")
  public java.time.Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.Instant createdAt) {
    this.createdAt = createdAt;
  }

  public PostDigitalDocumentResponse updatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  */
  @Valid 
  @JsonProperty("updatedAt")
  public java.time.Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.time.Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostDigitalDocumentResponse postDigitalDocumentResponse = (PostDigitalDocumentResponse) o;
    return Objects.equals(this.id, postDigitalDocumentResponse.id) &&
        Objects.equals(this.title, postDigitalDocumentResponse.title) &&
        Objects.equals(this.content, postDigitalDocumentResponse.content) &&
        Objects.equals(this.status, postDigitalDocumentResponse.status) &&
        Objects.equals(this.stock, postDigitalDocumentResponse.stock) &&
        Objects.equals(this.digitalDocument, postDigitalDocumentResponse.digitalDocument) &&
        Objects.equals(this.createdAt, postDigitalDocumentResponse.createdAt) &&
        Objects.equals(this.updatedAt, postDigitalDocumentResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, status, stock, digitalDocument, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostDigitalDocumentResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    digitalDocument: ").append(toIndentedString(digitalDocument)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

