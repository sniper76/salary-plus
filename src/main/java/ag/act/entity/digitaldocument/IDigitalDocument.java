package ag.act.entity.digitaldocument;

import ag.act.entity.ActEntity;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;

import java.time.LocalDateTime;


public interface IDigitalDocument extends ActEntity {

    Long getPostId();

    void setPostId(Long postId);

    String getStockCode();

    void setStockCode(String stockCode);

    String getCompanyName();

    void setCompanyName(String companyName);

    String getTitle();

    void setTitle(String title);

    DigitalDocumentType getType();

    void setType(DigitalDocumentType type);

    int getJoinUserCount();

    void setJoinUserCount(int joinUserCount);

    long getJoinStockSum();

    void setJoinStockSum(long joinStockSum);

    LocalDateTime getTargetStartDate();

    void setTargetStartDate(LocalDateTime targetStartDate);

    LocalDateTime getTargetEndDate();

    void setTargetEndDate(LocalDateTime targetEndDate);

    DigitalDocumentVersion getVersion();

    void setVersion(DigitalDocumentVersion version);

    Boolean getIsDisplayStockQuantity();

    void setIsDisplayStockQuantity(Boolean isDisplayStockQuantity);
}
