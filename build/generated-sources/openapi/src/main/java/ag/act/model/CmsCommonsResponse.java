package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SimpleBoardGroupResponse;
import ag.act.model.SimpleStockGroupResponse;
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
 * CmsCommonsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class CmsCommonsResponse {

  @Valid
  private List<@Valid SimpleStockResponse> stocks;

  @Valid
  private List<@Valid SimpleStockGroupResponse> stockGroups;

  @Valid
  private List<@Valid SimpleBoardGroupResponse> boardGroups;

  public CmsCommonsResponse stocks(List<@Valid SimpleStockResponse> stocks) {
    this.stocks = stocks;
    return this;
  }

  public CmsCommonsResponse addStocksItem(SimpleStockResponse stocksItem) {
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

  public CmsCommonsResponse stockGroups(List<@Valid SimpleStockGroupResponse> stockGroups) {
    this.stockGroups = stockGroups;
    return this;
  }

  public CmsCommonsResponse addStockGroupsItem(SimpleStockGroupResponse stockGroupsItem) {
    if (this.stockGroups == null) {
      this.stockGroups = new ArrayList<>();
    }
    this.stockGroups.add(stockGroupsItem);
    return this;
  }

  /**
   * Get stockGroups
   * @return stockGroups
  */
  @Valid 
  @JsonProperty("stockGroups")
  public List<@Valid SimpleStockGroupResponse> getStockGroups() {
    return stockGroups;
  }

  public void setStockGroups(List<@Valid SimpleStockGroupResponse> stockGroups) {
    this.stockGroups = stockGroups;
  }

  public CmsCommonsResponse boardGroups(List<@Valid SimpleBoardGroupResponse> boardGroups) {
    this.boardGroups = boardGroups;
    return this;
  }

  public CmsCommonsResponse addBoardGroupsItem(SimpleBoardGroupResponse boardGroupsItem) {
    if (this.boardGroups == null) {
      this.boardGroups = new ArrayList<>();
    }
    this.boardGroups.add(boardGroupsItem);
    return this;
  }

  /**
   * Get boardGroups
   * @return boardGroups
  */
  @Valid 
  @JsonProperty("boardGroups")
  public List<@Valid SimpleBoardGroupResponse> getBoardGroups() {
    return boardGroups;
  }

  public void setBoardGroups(List<@Valid SimpleBoardGroupResponse> boardGroups) {
    this.boardGroups = boardGroups;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CmsCommonsResponse cmsCommonsResponse = (CmsCommonsResponse) o;
    return Objects.equals(this.stocks, cmsCommonsResponse.stocks) &&
        Objects.equals(this.stockGroups, cmsCommonsResponse.stockGroups) &&
        Objects.equals(this.boardGroups, cmsCommonsResponse.boardGroups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stocks, stockGroups, boardGroups);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CmsCommonsResponse {\n");
    sb.append("    stocks: ").append(toIndentedString(stocks)).append("\n");
    sb.append("    stockGroups: ").append(toIndentedString(stockGroups)).append("\n");
    sb.append("    boardGroups: ").append(toIndentedString(boardGroups)).append("\n");
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

