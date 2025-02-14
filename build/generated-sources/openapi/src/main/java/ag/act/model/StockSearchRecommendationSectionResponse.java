package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SimpleStockResponse;
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
 * StockSearchRecommendationSectionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockSearchRecommendationSectionResponse {

  private String title;

  private String baseDateTime;

  private String type;

  @Valid
  private List<@Valid SimpleStockResponse> stocks;

  public StockSearchRecommendationSectionResponse title(String title) {
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

  public StockSearchRecommendationSectionResponse baseDateTime(String baseDateTime) {
    this.baseDateTime = baseDateTime;
    return this;
  }

  /**
   * Get baseDateTime
   * @return baseDateTime
  */
  
  @JsonProperty("baseDateTime")
  public String getBaseDateTime() {
    return baseDateTime;
  }

  public void setBaseDateTime(String baseDateTime) {
    this.baseDateTime = baseDateTime;
  }

  public StockSearchRecommendationSectionResponse type(String type) {
    this.type = type;
    return this;
  }

  /**
   * SEARCH_TREND_STOCK or STAKE_RANKING_STOCK
   * @return type
  */
  
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StockSearchRecommendationSectionResponse stocks(List<@Valid SimpleStockResponse> stocks) {
    this.stocks = stocks;
    return this;
  }

  public StockSearchRecommendationSectionResponse addStocksItem(SimpleStockResponse stocksItem) {
    if (this.stocks == null) {
      this.stocks = new ArrayList<>();
    }
    this.stocks.add(stocksItem);
    return this;
  }

  /**
   * Get stocks
   * @return stocks
  */
  @Valid 
  @JsonProperty("stocks")
  public List<@Valid SimpleStockResponse> getStocks() {
    return stocks;
  }

  public void setStocks(List<@Valid SimpleStockResponse> stocks) {
    this.stocks = stocks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockSearchRecommendationSectionResponse stockSearchRecommendationSectionResponse = (StockSearchRecommendationSectionResponse) o;
    return Objects.equals(this.title, stockSearchRecommendationSectionResponse.title) &&
        Objects.equals(this.baseDateTime, stockSearchRecommendationSectionResponse.baseDateTime) &&
        Objects.equals(this.type, stockSearchRecommendationSectionResponse.type) &&
        Objects.equals(this.stocks, stockSearchRecommendationSectionResponse.stocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, baseDateTime, type, stocks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockSearchRecommendationSectionResponse {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    baseDateTime: ").append(toIndentedString(baseDateTime)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    stocks: ").append(toIndentedString(stocks)).append("\n");
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

