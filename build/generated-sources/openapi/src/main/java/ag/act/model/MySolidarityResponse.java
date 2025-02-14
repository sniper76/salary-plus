package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.LinkResponse;
import ag.act.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * MySolidarityResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class MySolidarityResponse {

  private Status status;

  private Long id;

  private String name;

  private String code;

  private Integer memberCount = null;

  private Float stake = null;

  private Integer requiredMemberCount = null;

  private Integer minThresholdMemberCount = null;

  private String representativePhoneNumber;

  private Integer displayOrder;

  @Valid
  private List<@Valid LinkResponse> links;

  private String stakeRank;

  private Integer stakeRankDelta;

  private String marketValueRank;

  private Integer marketValueRankDelta;

  public MySolidarityResponse status(Status status) {
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

  public MySolidarityResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Id of Solidarity
   * @return id
  */
  
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MySolidarityResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the Stock
   * @return name
  */
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MySolidarityResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Code of the solidarity
   * @return code
  */
  
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public MySolidarityResponse memberCount(Integer memberCount) {
    this.memberCount = memberCount;
    return this;
  }

  /**
   * Member count of the solidarity
   * @return memberCount
  */
  
  @JsonProperty("memberCount")
  public Integer getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Integer memberCount) {
    this.memberCount = memberCount;
  }

  public MySolidarityResponse stake(Float stake) {
    this.stake = stake;
    return this;
  }

  /**
   * Stake of the solidarity
   * @return stake
  */
  
  @JsonProperty("stake")
  public Float getStake() {
    return stake;
  }

  public void setStake(Float stake) {
    this.stake = stake;
  }

  public MySolidarityResponse requiredMemberCount(Integer requiredMemberCount) {
    this.requiredMemberCount = requiredMemberCount;
    return this;
  }

  /**
   * Required member count for the solidarity
   * @return requiredMemberCount
  */
  
  @JsonProperty("requiredMemberCount")
  public Integer getRequiredMemberCount() {
    return requiredMemberCount;
  }

  public void setRequiredMemberCount(Integer requiredMemberCount) {
    this.requiredMemberCount = requiredMemberCount;
  }

  public MySolidarityResponse minThresholdMemberCount(Integer minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
    return this;
  }

  /**
   * Get minThresholdMemberCount
   * @return minThresholdMemberCount
  */
  
  @JsonProperty("minThresholdMemberCount")
  public Integer getMinThresholdMemberCount() {
    return minThresholdMemberCount;
  }

  public void setMinThresholdMemberCount(Integer minThresholdMemberCount) {
    this.minThresholdMemberCount = minThresholdMemberCount;
  }

  public MySolidarityResponse representativePhoneNumber(String representativePhoneNumber) {
    this.representativePhoneNumber = representativePhoneNumber;
    return this;
  }

  /**
   * 종목 담당자 전화번호
   * @return representativePhoneNumber
  */
  
  @JsonProperty("representativePhoneNumber")
  public String getRepresentativePhoneNumber() {
    return representativePhoneNumber;
  }

  public void setRepresentativePhoneNumber(String representativePhoneNumber) {
    this.representativePhoneNumber = representativePhoneNumber;
  }

  public MySolidarityResponse displayOrder(Integer displayOrder) {
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

  public MySolidarityResponse links(List<@Valid LinkResponse> links) {
    this.links = links;
    return this;
  }

  public MySolidarityResponse addLinksItem(LinkResponse linksItem) {
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

  public MySolidarityResponse stakeRank(String stakeRank) {
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

  public MySolidarityResponse stakeRankDelta(Integer stakeRankDelta) {
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

  public MySolidarityResponse marketValueRank(String marketValueRank) {
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

  public MySolidarityResponse marketValueRankDelta(Integer marketValueRankDelta) {
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
    MySolidarityResponse mySolidarityResponse = (MySolidarityResponse) o;
    return Objects.equals(this.status, mySolidarityResponse.status) &&
        Objects.equals(this.id, mySolidarityResponse.id) &&
        Objects.equals(this.name, mySolidarityResponse.name) &&
        Objects.equals(this.code, mySolidarityResponse.code) &&
        Objects.equals(this.memberCount, mySolidarityResponse.memberCount) &&
        Objects.equals(this.stake, mySolidarityResponse.stake) &&
        Objects.equals(this.requiredMemberCount, mySolidarityResponse.requiredMemberCount) &&
        Objects.equals(this.minThresholdMemberCount, mySolidarityResponse.minThresholdMemberCount) &&
        Objects.equals(this.representativePhoneNumber, mySolidarityResponse.representativePhoneNumber) &&
        Objects.equals(this.displayOrder, mySolidarityResponse.displayOrder) &&
        Objects.equals(this.links, mySolidarityResponse.links) &&
        Objects.equals(this.stakeRank, mySolidarityResponse.stakeRank) &&
        Objects.equals(this.stakeRankDelta, mySolidarityResponse.stakeRankDelta) &&
        Objects.equals(this.marketValueRank, mySolidarityResponse.marketValueRank) &&
        Objects.equals(this.marketValueRankDelta, mySolidarityResponse.marketValueRankDelta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, id, name, code, memberCount, stake, requiredMemberCount, minThresholdMemberCount, representativePhoneNumber, displayOrder, links, stakeRank, stakeRankDelta, marketValueRank, marketValueRankDelta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MySolidarityResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    memberCount: ").append(toIndentedString(memberCount)).append("\n");
    sb.append("    stake: ").append(toIndentedString(stake)).append("\n");
    sb.append("    requiredMemberCount: ").append(toIndentedString(requiredMemberCount)).append("\n");
    sb.append("    minThresholdMemberCount: ").append(toIndentedString(minThresholdMemberCount)).append("\n");
    sb.append("    representativePhoneNumber: ").append(toIndentedString(representativePhoneNumber)).append("\n");
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

