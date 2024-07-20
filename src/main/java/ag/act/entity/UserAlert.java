package ag.act.entity;

import ag.act.enums.AppLinkType;

public interface UserAlert extends ActEntity {
    AppLinkType getLinkType();

    String getStockCode();

    Long getStockGroupId();

    String getLinkUrl();

}
