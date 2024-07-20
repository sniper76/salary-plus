package ag.act.dto;

import java.time.Instant;

public interface IDigitalDocumentFillRequest {
    String getContent();

    String getTitle();

    String getCompanyRegistrationNumber();

    String getShareholderMeetingType();

    String getShareholderMeetingName();

    Instant getShareholderMeetingDate();

    String getDesignatedAgentNames();
}
