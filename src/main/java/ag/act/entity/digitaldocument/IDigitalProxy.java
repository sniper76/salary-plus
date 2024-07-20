package ag.act.entity.digitaldocument;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IDigitalProxy extends IDigitalDocument {

    LocalDate getStockReferenceDate();

    void setStockReferenceDate(LocalDate stockReferenceDate);

    Long getAcceptUserId();//수임인

    void setAcceptUserId(Long acceptUserId);

    LocalDateTime getShareholderMeetingDate();

    void setShareholderMeetingDate(LocalDateTime shareholderMeetingDate);

    String getShareholderMeetingType();//주총구분

    void setShareholderMeetingType(String shareholderMeetingType);

    String getShareholderMeetingName();//주총명칭

    void setShareholderMeetingName(String shareholderMeetingName);

    String getDesignatedAgentNames();//수임인지정대리인

    void setDesignatedAgentNames(String designatedAgentNames);
}
