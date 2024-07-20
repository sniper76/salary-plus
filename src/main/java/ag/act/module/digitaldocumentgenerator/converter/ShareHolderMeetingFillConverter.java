package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.enums.digitaldocument.ShareholderMeetingType;
import ag.act.module.digitaldocumentgenerator.model.DateFill;
import ag.act.module.digitaldocumentgenerator.model.ShareHolderMeetingFill;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

@Component
public class ShareHolderMeetingFillConverter {
    public ShareHolderMeetingFill convert(IDigitalProxy digitalProxy) {
        return new ShareHolderMeetingFill(
            digitalProxy.getShareholderMeetingName(),
            ShareholderMeetingType.REGULAR_GENERAL_MEETING_STOCKHOLDERS.name()
                .equals(digitalProxy.getShareholderMeetingType()),
            new DateFill(
                KoreanDateTimeUtil.toKoreanTime(digitalProxy.getShareholderMeetingDate())
            )
        );
    }
}
