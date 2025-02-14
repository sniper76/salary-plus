package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.LinkResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * MySolidarityResponseAllOf
 */

@JsonTypeName("MySolidarityResponse_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class MySolidarityResponseAllOf {

  private Integer displayOrder;

  @Valid
  private List<@Valid LinkResponse> links;

  private String stakeRank;

  private Integer stakeRankDelta;

  private String marketValueRank;

  private Integer marketValueRankDelta;

  public MySolidarityResponseAllOf displayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
    return this;
  }

  /**
   * Get displayOrder
   * @return displayOrder
  */
  
  @JsonProperty("displayOrder")
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public MySolidarityResponseAllOf links(List<@Valid LinkResponse> links) {
    this.links = links;
    return this;
  }

  public MySolidarityResponseAllOf addLinksItem(LinkResponse linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Get links
   * @return links
  */
  @Valid 
  @JsonProperty("links")
  public List<@Valid LinkResponse> getLinks() {
    return links;
  }

  public void setLinks(List<@Valid LinkResponse> links) {
    this.links = links;
  }

  public MySolidarityResponseAllOf stakeRank(String stakeRank) {
    this.stakeRank = stakeRank;
    return this;
  }

  /**
   * Get stakeRank
   * @return stakeRank
  */
  
  @JsonProperty("stakeRank")
  public String getStakeRank() {
    return stakeRank;
  }

  public void setStakeRank(String stakeRank) {
    this.stakeRank = stakeRank;
  }

  public MySolidarityResponseAllOf stakeRankDelta(Integer stakeRankDelta) {
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

  public MySolidarityResponseAllOf marketValueRank(String marketValueRank) {
    this.marketValueRank = marketValueRank;
    return this;
  }

  /**
   * Get marketValueRank
   * @return marketValueRank
  */
  
  @JsonProperty("marketValueRank")
  public String getMarketValueRank() {
    return marketValueRank;
  }

  public void setMarketValueRank(String marketValueRank) {
    this.marketValueRank = marketValueRank;
  }

  public MySolidarityResponseAllOf marketValueRankDelta(Integer marketValueRankDelta) {
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
    MySolidarityResponseAllOf mySolidarityResponseAllOf = (MySolidarityResponseAllOf) o;
    return Objects.equals(this.displayOrder, mySolidarityResponseAllOf.displayOrder) &&
        Objects.equals(this.links, mySolidarityResponseAllOf.links) &&
        Objects.equals(this.stakeRank, mySolidarityResponseAllOf.stakeRank) &&
        Objects.equals(this.stakeRankDelta, mySolidarityResponseAllOf.stakeRankDelta) &&
        Objects.equals(this.marketValueRank, mySolidarityResponseAllOf.marketValueRank) &&
        Objects.equals(this.marketValueRankDelta, mySolidarityResponseAllOf.marketValueRankDelta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayOrder, links, stakeRank, stakeRankDelta, marketValueRank, marketValueRankDelta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MySolidarityResponseAllOf {\n");
    sb.append("    displayOrder: ").append(toIndentedString(displayOrder)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

