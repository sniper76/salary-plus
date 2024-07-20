package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyFill;
import ag.act.module.digitaldocumentgenerator.model.ShareHolderMeetingFill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DigitalProxyFillConverter extends BaseDigitalDocumentFillConverter {
    private final AcceptorFillConverter acceptorFillConverter;
    private final DigitalProxyItemsFillConverter digitalProxyItemsFillConverter;
    private final ShareHolderMeetingFillConverter shareHolderMeetingFillConverter;

    @Override
    public DigitalDocumentFill convert(IGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        final IDigitalProxy digitalProxy = (IDigitalProxy) generateHtmlDocumentDto.getDigitalDocument();
        final DigitalDocumentUser digitalDocumentUser = generateHtmlDocumentDto.getDigitalDocumentUser();

        final DigitalProxyFill digitalProxyFill = (DigitalProxyFill) getBaseDigitalDocumentFill(generateHtmlDocumentDto);

        digitalProxyFill.setShareHolderMeeting(convertShareHolderMeetingFill(digitalProxy));
        digitalProxyFill.setDesignatedAgentNames(digitalProxy.getDesignatedAgentNames());
        digitalProxyFill.setDigitalDocumentItems(digitalProxyItemsFillConverter.convert(digitalDocumentUser));
        digitalProxyFill.setAcceptor(
            acceptorFillConverter.convert(digitalProxy.getStockCode(), digitalProxy.getAcceptUserId())
        );

        return digitalProxyFill;
    }

    private ShareHolderMeetingFill convertShareHolderMeetingFill(IDigitalProxy digitalProxy) {
        return shareHolderMeetingFillConverter.convert(digitalProxy);
    }
}
