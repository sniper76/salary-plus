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
 * StockRankingResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class StockRankingResponse {

  private String stockCode;

  private String stockName;

  private String stake;

  private String marketValue;

  private Integer stakeRank;

  private Integer stakeRankDelta;

  private Integer marketValueRank;

  private Integer marketValueRankDelta;

  public StockRankingResponse stockCode(String stockCode) {
    this.stockCode = stockCode;
    return this;
  }

  /**
   * Get stockCode
   * @return stockCode
  */
  
  @JsonProperty("stockCode")
  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public StockRankingResponse stockName(String stockName) {
    this.stockName = stockName;
    return this;
  }

  /**
   * Get stockName
   * @return stockName
  */
  
  @JsonProperty("stockName")
  public String getStockName() {
    return stockName;
  }

  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  public StockRankingResponse stake(String stake) {
    this.stake = stake;
    return this;
  }

  /**
   * Get stake
   * @return stake
  */
  
  @JsonProperty("stake")
  public String getStake() {
    return stake;
  }

  public void setStake(String stake) {
    this.stake = stake;
  }

  public StockRankingResponse marketValue(String marketValue) {
    this.marketValue = marketValue;
    return this;
  }

  /**
   * Get marketValue
   * @return marketValue
  */
  
  @JsonProperty("marketValue")
  public String getMarketValue() {
    return marketValue;
  }

  public void setMarketValue(String marketValue) {
    this.marketValue = marketValue;
  }

  public StockRankingResponse stakeRank(Integer stakeRank) {
    this.stakeRank = stakeRank;
    return this;
  }

  /**
   * Get stakeRank
   * @return stakeRank
  */
  
  @JsonProperty("stakeRank")
  public Integer getStakeRank() {
    return stakeRank;
  }

  public void setStakeRank(Integer stakeRank) {
    this.stakeRank = stakeRank;
  }

  public StockRankingResponse stakeRankDelta(Integer stakeRankDelta) {
    this.stakeRankDelta = stakeRankDelta;
    return this;
  }

  /**
   * Get stakeRankDelta
   * @return stakeRankDelta
  */
  
  @JsonProperty("stakeRankDelta")
  public Integer getStakeRankDelta() {
    return stakeRankDelta;
  }

  public void setStakeRankDelta(Integer stakeRankDelta) {
    this.stakeRankDelta = stakeRankDelta;
  }

  public StockRankingResponse marketValueRank(Integer marketValueRank) {
    this.marketValueRank = marketValueRank;
    return this;
  }

  /**
   * Get marketValueRank
   * @return marketValueRank
  */
  
  @JsonProperty("marketValueRank")
  public Integer getMarketValueRank() {
    return marketValueRank;
  }

  public void setMarketValueRank(Integer marketValueRank) {
    this.marketValueRank = marketValueRank;
  }

  public StockRankingResponse marketValueRankDelta(Integer marketValueRankDelta) {
    this.marketValueRankDelta = marketValueRankDelta;
    return this;
  }

  /**
   * Get marketValueRankDelta
   * @return marketValueRankDelta
  */
  
  @JsonProperty("marketValueRankDelta")
  public Integer getMarketValueRankDelta() {
    return marketValueRankDelta;
  }

  public void setMarketValueRankDelta(Integer marketValueRankDelta) {
    this.marketValueRankDelta = marketValueRankDelta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockRankingResponse stockRankingResponse = (StockRankingResponse) o;
    return Objects.equals(this.stockCode, stockRankingResponse.stockCode) &&
        Objects.equals(this.stockName, stockRankingResponse.stockName) &&
        Objects.equals(this.stake, stockRankingResponse.stake) &&
        Objects.equals(this.marketValue, stockRankingResponse.marketValue) &&
        Objects.equals(this.stakeRank, stockRankingResponse.stakeRank) &&
        Objects.equals(this.stakeRankDelta, stockRankingResponse.stakeRankDelta) &&
        Objects.equals(this.marketValueRank, stockRankingResponse.marketValueRank) &&
        Objects.equals(this.marketValueRankDelta, stockRankingResponse.marketValueRankDelta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stockCode, stockName, stake, marketValue, stakeRank, stakeRankDelta, marketValueRank, marketValueRankDelta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockRankingResponse {\n");
    sb.append("    stockCode: ").append(toIndentedString(stockCode)).append("\n");
    sb.append("    stockName: ").append(toIndentedString(stockName)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    marketValue: ").append(toIndentedString(marketValue)).append("\n");
    sb.append("    stakeRank: ").append(toIndentedString(stakeRank)).append("\n");
    sb.append("    stakeRankDelta: ").append(toIndentedString(stakeRankDelta)).append("\n");
    sb.append("    marketValueRank: ").append(toIndentedString(marketValueRank)).append("\n");
    sb.append("    marketValueRankDelta: ").append(toIndentedString(marketValueRankDelta)).append("\n");
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

