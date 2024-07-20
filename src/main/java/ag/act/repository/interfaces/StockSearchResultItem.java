package ag.act.repository.interfaces;

import ag.act.model.Status;

import java.time.LocalDateTime;

public interface StockSearchResultItem {

    String getCode();

    String getName();

    Long getTotalIssuedQuantity();

    String getRepresentativePhoneNumber();

    Long getSolidarityId();

    Status getStatus();

    String getStandardCode();

    String getFullName();

    String getMarketType();

    String getStockType();

    Integer getClosingPrice();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    LocalDateTime getDeletedAt();

    Double getStake();

    Integer getMemberCount();

    Long getMarketValue();

    Boolean getIsPrivate();
}
