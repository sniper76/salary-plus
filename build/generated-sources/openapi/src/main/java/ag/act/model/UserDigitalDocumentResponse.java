package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.DigitalDocumentStockResponse;
import ag.act.model.DigitalDocumentUserResponse;
import ag.act.model.JsonAttachOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * UserDigitalDocumentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserDigitalDocumentResponse {

  private Long id;

  private String answerStatus;

  private String digitalDocumentType;

  private Integer joinUserCount;

  private Long joinStockSum;

  private Float shareholdingRatio;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetStartDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant targetEndDate;

  private DigitalDocumentUserResponse user;

  private DigitalDocumentStockResponse stock;

  private DigitalDocumentAcceptUserResponse acceptUser;

  @Valid
  private List<@Valid DigitalDocumentItemResponse> items;

  private JsonAttachOption attachOptions;

  private DigitalDocumentDownloadResponse digitalDocumentDownload;

  public UserDigitalDocumentResponse id(Long id) {
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

  public UserDigitalDocumentResponse answerStatus(String answerStatus) {
    this.answerStatus = answerStatus;
    return this;
  }

  /**
   * Get answerStatus
   * @return answerStatus
  */
  
  @JsonProperty("answerStatus")
  public String getAnswerStatus() {
    return answerStatus;
  }

  public void setAnswerStatus(String answerStatus) {
    this.answerStatus = answerStatus;
  }

  public UserDigitalDocumentResponse digitalDocumentType(String digitalDocumentType) {
    this.digitalDocumentType = digitalDocumentType;
    return this;
  }

  /**
   * Get digitalDocumentType
   * @return digitalDocumentType
  */
  
  @JsonProperty("digitalDocumentType")
  public String getDigitalDocumentType() {
    return digitalDocumentType;
  }

  public void setDigitalDocumentType(String digitalDocumentType) {
    this.digitalDocumentType = digitalDocumentType;
  }

  public UserDigitalDocumentResponse joinUserCount(Integer joinUserCount) {
    this.joinUserCount = joinUserCount;
    return this;
  }

  /**
   * Get joinUserCount
   * @return joinUserCount
  */
  
  @JsonProperty("joinUserCount")
  public Integer getJoinUserCount() {
    return joinUserCount;
  }

  public void setJoinUserCount(Integer joinUserCount) {
    this.joinUserCount = joinUserCount;
  }

  public UserDigitalDocumentResponse joinStockSum(Long joinStockSum) {
    this.joinStockSum = joinStockSum;
    return this;
  }

  /**
   * Get joinStockSum
   * @return joinStockSum
  */
  
  @JsonProperty("joinStockSum")
  public Long getJoinStockSum() {
    return joinStockSum;
  }

  public void setJoinStockSum(Long joinStockSum) {
    this.joinStockSum = joinStockSum;
  }

  public UserDigitalDocumentResponse shareholdingRatio(Float shareholdingRatio) {
    this.shareholdingRatio = shareholdingRatio;
    return this;
  }

  /**
   * Get shareholdingRatio
   * @return shareholdingRatio
  */
  
  @JsonProperty("shareholdingRatio")
  public Float getShareholdingRatio() {
    return shareholdingRatio;
  }

  public void setShareholdingRatio(Float shareholdingRatio) {
    this.shareholdingRatio = shareholdingRatio;
  }

  public UserDigitalDocumentResponse targetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
    return this;
  }

  /**
   * Get targetStartDate
   * @return targetStartDate
  */
  @Valid 
  @JsonProperty("targetStartDate")
  public java.time.Instant getTargetStartDate() {
    return targetStartDate;
  }

  public void setTargetStartDate(java.time.Instant targetStartDate) {
    this.targetStartDate = targetStartDate;
  }

  public UserDigitalDocumentResponse targetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
    return this;
  }

  /**
   * Get targetEndDate
   * @return targetEndDate
  */
  @Valid 
  @JsonProperty("targetEndDate")
  public java.time.Instant getTargetEndDate() {
    return targetEndDate;
  }

  public void setTargetEndDate(java.time.Instant targetEndDate) {
    this.targetEndDate = targetEndDate;
  }

  public UserDigitalDocumentResponse user(DigitalDocumentUserResponse user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
  */
  @Valid 
  @JsonProperty("user")
  public DigitalDocumentUserResponse getUser() {
    return user;
  }

  public void setUser(DigitalDocumentUserResponse user) {
    this.user = user;
  }

  public UserDigitalDocumentResponse stock(DigitalDocumentStockResponse stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Get stock
   * @return stock
  */
  @Valid 
  @JsonProperty("stock")
  public DigitalDocumentStockResponse getStock() {
    return stock;
  }

  public void setStock(DigitalDocumentStockResponse stock) {
    this.stock = stock;
  }

  public UserDigitalDocumentResponse acceptUser(DigitalDocumentAcceptUserResponse acceptUser) {
    this.acceptUser = acceptUser;
    return this;
  }

  /**
   * Get acceptUser
   * @return acceptUser
  */
  @Valid 
  @JsonProperty("acceptUser")
  public DigitalDocumentAcceptUserResponse getAcceptUser() {
    return acceptUser;
  }

  public void setAcceptUser(DigitalDocumentAcceptUserResponse acceptUser) {
    this.acceptUser = acceptUser;
  }

  public UserDigitalDocumentResponse items(List<@Valid DigitalDocumentItemResponse> items) {
    this.items = items;
    return this;
  }

  public UserDigitalDocumentResponse addItemsItem(DigitalDocumentItemResponse itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid 
  @JsonProperty("items")
  public List<@Valid DigitalDocumentItemResponse> getItems() {
    return items;
  }

  public void setItems(List<@Valid DigitalDocumentItemResponse> items) {
    this.items = items;
  }

  public UserDigitalDocumentResponse attachOptions(JsonAttachOption attachOptions) {
    this.attachOptions = attachOptions;
    return this;
  }

  /**
   * Get attachOptions
   * @return attachOptions
  */
  @Valid 
  @JsonProperty("attachOptions")
  public JsonAttachOption getAttachOptions() {
    return attachOptions;
  }

  public void setAttachOptions(JsonAttachOption attachOptions) {
    this.attachOptions = attachOptions;
  }

  public UserDigitalDocumentResponse digitalDocumentDownload(DigitalDocumentDownloadResponse digitalDocumentDownload) {
    this.digitalDocumentDownload = digitalDocumentDownload;
    return this;
  }

  /**
   * Get digitalDocumentDownload
   * @return digitalDocumentDownload
  */
  @Valid 
  @JsonProperty("digitalDocumentDownload")
  public DigitalDocumentDownloadResponse getDigitalDocumentDownload() {
    return digitalDocumentDownload;
  }

  public void setDigitalDocumentDownload(DigitalDocumentDownloadResponse digitalDocumentDownload) {
    this.digitalDocumentDownload = digitalDocumentDownload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDigitalDocumentResponse userDigitalDocumentResponse = (UserDigitalDocumentResponse) o;
    return Objects.equals(this.id, userDigitalDocumentResponse.id) &&
        Objects.equals(this.answerStatus, userDigitalDocumentResponse.answerStatus) &&
        Objects.equals(this.digitalDocumentType, userDigitalDocumentResponse.digitalDocumentType) &&
        Objects.equals(this.joinUserCount, userDigitalDocumentResponse.joinUserCount) &&
        Objects.equals(this.joinStockSum, userDigitalDocumentResponse.joinStockSum) &&
        Objects.equals(this.shareholdingRatio, userDigitalDocumentResponse.shareholdingRatio) &&
        Objects.equals(this.targetStartDate, userDigitalDocumentResponse.targetStartDate) &&
        Objects.equals(this.targetEndDate, userDigitalDocumentResponse.targetEndDate) &&
        Objects.equals(this.user, userDigitalDocumentResponse.user) &&
        Objects.equals(this.stock, userDigitalDocumentResponse.stock) &&
        Objects.equals(this.acceptUser, userDigitalDocumentResponse.acceptUser) &&
        Objects.equals(this.items, userDigitalDocumentResponse.items) &&
        Objects.equals(this.attachOptions, userDigitalDocumentResponse.attachOptions) &&
        Objects.equals(this.digitalDocumentDownload, userDigitalDocumentResponse.digitalDocumentDownload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, answerStatus, digitalDocumentType, joinUserCount, joinStockSum, shareholdingRatio, targetStartDate, targetEndDate, user, stock, acceptUser, items, attachOptions, digitalDocumentDownload);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDigitalDocumentResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    answerStatus: ").append(toIndentedString(answerStatus)).append("\n");
    sb.append("    digitalDocumentType: ").append(toIndentedString(digitalDocumentType)).append("\n");
    sb.append("    joinUserCount: ").append(toIndentedString(joinUserCount)).append("\n");
    sb.append("    joinStockSum: ").append(toIndentedString(joinStockSum)).append("\n");
    sb.append("    shareholdingRatio: ").append(toIndentedString(shareholdingRatio)).append("\n");
    sb.append("    targetStartDate: ").append(toIndentedString(targetStartDate)).append("\n");
    sb.append("    targetEndDate: ").append(toIndentedString(targetEndDate)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    acceptUser: ").append(toIndentedString(acceptUser)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    attachOptions: ").append(toIndentedString(attachOptions)).append("\n");
    sb.append("    digitalDocumentDownload: ").append(toIndentedString(digitalDocumentDownload)).append("\n");
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

