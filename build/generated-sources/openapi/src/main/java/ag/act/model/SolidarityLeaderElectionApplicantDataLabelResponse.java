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
 * SolidarityLeaderElectionApplicantDataLabelResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionApplicantDataLabelResponse {

  private String requiredStockQuantityRatio;

  private Long stockQuantity;

  private String label;

  private String unit;

  private String valueText;

  private String color;

  public SolidarityLeaderElectionApplicantDataLabelResponse requiredStockQuantityRatio(String requiredStockQuantityRatio) {
    this.requiredStockQuantityRatio = requiredStockQuantityRatio;
    return this;
  }

  /**
   * Get requiredStockQuantityRatio
   * @return requiredStockQuantityRatio
  */
  
  @JsonProperty("requiredStockQuantityRatio")
  public String getRequiredStockQuantityRatio() {
    return requiredStockQuantityRatio;
  }

  public void setRequiredStockQuantityRatio(String requiredStockQuantityRatio) {
    this.requiredStockQuantityRatio = requiredStockQuantityRatio;
  }

  public SolidarityLeaderElectionApplicantDataLabelResponse stockQuantity(Long stockQuantity) {
    this.stockQuantity = stockQuantity;
    return this;
  }

  /**
   * Get stockQuantity
   * @return stockQuantity
  */
  
  @JsonProperty("stockQuantity")
  public Long getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(Long stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public SolidarityLeaderElectionApplicantDataLabelResponse label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public SolidarityLeaderElectionApplicantDataLabelResponse unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * Get unit
   * @return unit
  */
  
  @JsonProperty("unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public SolidarityLeaderElectionApplicantDataLabelResponse valueText(String valueText) {
    this.valueText = valueText;
    return this;
  }

  /**
   * Get valueText
   * @return valueText
  */
  
  @JsonProperty("valueText")
  public String getValueText() {
    return valueText;
  }

  public void setValueText(String valueText) {
    this.valueText = valueText;
  }

  public SolidarityLeaderElectionApplicantDataLabelResponse color(String color) {
    this.color = color;
    return this;
  }

  /**
   * Get color
   * @return color
  */
  
  @JsonProperty("color")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionApplicantDataLabelResponse solidarityLeaderElectionApplicantDataLabelResponse = (SolidarityLeaderElectionApplicantDataLabelResponse) o;
    return Objects.equals(this.requiredStockQuantityRatio, solidarityLeaderElectionApplicantDataLabelResponse.requiredStockQuantityRatio) &&
        Objects.equals(this.stockQuantity, solidarityLeaderElectionApplicantDataLabelResponse.stockQuantity) &&
        Objects.equals(this.label, solidarityLeaderElectionApplicantDataLabelResponse.label) &&
        Objects.equals(this.unit, solidarityLeaderElectionApplicantDataLabelResponse.unit) &&
        Objects.equals(this.valueText, solidarityLeaderElectionApplicantDataLabelResponse.valueText) &&
        Objects.equals(this.color, solidarityLeaderElectionApplicantDataLabelResponse.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requiredStockQuantityRatio, stockQuantity, label, unit, valueText, color);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionApplicantDataLabelResponse {\n");
    sb.append("    requiredStockQuantityRatio: ").append(toIndentedString(requiredStockQuantityRatio)).append("\n");
    sb.append("    stockQuantity: ").append(toIndentedString(stockQuantity)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    valueText: ").append(toIndentedString(valueText)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
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

