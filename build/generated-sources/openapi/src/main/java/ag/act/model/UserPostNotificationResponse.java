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
 * UserPostNotificationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class UserPostNotificationResponse {

  private Long id;

  private String boardCategory;

  private String boardCategoryDisplayName;

  private String title;

  private String stockCode;

  private String stockName;

  public UserPostNotificationResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Post ID
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserPostNotificationResponse boardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
    return this;
  }

  /**
   * Board category
   * @return boardCategory
  */
  
  @JsonProperty("boardCategory")
  public String getBoardCategory() {
    return boardCategory;
  }

  public void setBoardCategory(String boardCategory) {
    this.boardCategory = boardCategory;
  }

  public UserPostNotificationResponse boardCategoryDisplayName(String boardCategoryDisplayName) {
    this.boardCategoryDisplayName = boardCategoryDisplayName;
    return this;
  }

  /**
   * Board category Display Name
   * @return boardCategoryDisplayName
  */
  
  @JsonProperty("boardCategoryDisplayName")
  public String getBoardCategoryDisplayName() {
    return boardCategoryDisplayName;
  }

  public void setBoardCategoryDisplayName(String boardCategoryDisplayName) {
    this.boardCategoryDisplayName = boardCategoryDisplayName;
  }

  public UserPostNotificationResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Post title
   * @return title
  */
  
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public UserPostNotificationResponse stockCode(String stockCode) {
    this.stockCode = stockCode;
    return this;
  }

  /**
   * Stock code
   * @return stockCode
  */
  
  @JsonProperty("stockCode")
  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public UserPostNotificationResponse stockName(String stockName) {
    this.stockName = stockName;
    return this;
  }

  /**
   * Stock name
   * @return stockName
  */
  
  @JsonProperty("stockName")
  public String getStockName() {
    return stockName;
  }

  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPostNotificationResponse userPostNotificationResponse = (UserPostNotificationResponse) o;
    return Objects.equals(this.id, userPostNotificationResponse.id) &&
        Objects.equals(this.boardCategory, userPostNotificationResponse.boardCategory) &&
        Objects.equals(this.boardCategoryDisplayName, userPostNotificationResponse.boardCategoryDisplayName) &&
        Objects.equals(this.title, userPostNotificationResponse.title) &&
        Objects.equals(this.stockCode, userPostNotificationResponse.stockCode) &&
        Objects.equals(this.stockName, userPostNotificationResponse.stockName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, boardCategory, boardCategoryDisplayName, title, stockCode, stockName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPostNotificationResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    boardCategory: ").append(toIndentedString(boardCategory)).append("\n");
    sb.append("    boardCategoryDisplayName: ").append(toIndentedString(boardCategoryDisplayName)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockName: ").append(toIndentedString(stockName)).append("\n");
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

