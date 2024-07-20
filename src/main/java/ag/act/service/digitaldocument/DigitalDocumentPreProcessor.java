package ag.act.service.digitaldocument;

import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.service.user.UserService;
import ag.act.validator.document.DigitalDocumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalDocumentPreProcessor {
    private final UserService userService;
    private final DigitalDocumentValidator digitalDocumentValidator;

    public void validateJointOwnershipDocumentForCreate(CreateDigitalDocumentRequest createRequest, String stockCode) {
        digitalDocumentValidator.validateJointOwnershipDocument(createRequest);

        validateAcceptor(createRequest, stockCode);
    }

    public void validateDigitalDocumentForCreate(CreateDigitalDocumentRequest createRequest, String stockCode) {
        digitalDocumentValidator.validateDigitalProxy(createRequest);

        validateAcceptor(createRequest, stockCode);
    }

    private void validateAcceptor(CreateDigitalDocumentRequest createRequest, String stockCode) {
        final DigitalDocumentType digitalDocumentType = DigitalDocumentType.fromValue(createRequest.getType());

        if (digitalDocumentType == DigitalDocumentType.ETC_DOCUMENT) {
            return;
        }

        if (createRequest.getAcceptUserId() == null) {
            throw new BadRequestException("전자문서 %s 정보가 없습니다.".formatted(digitalDocumentType.getAcceptUserDisplayName()));
        }

        userService.findUser(createRequest.getAcceptUserId())
            .orElseThrow(() -> new BadRequestException("전자문서 %s 정보가 없습니다.".formatted(digitalDocumentType.getAcceptUserDisplayName())));

        digitalDocumentValidator.validateAcceptor(
            digitalDocumentType,
            getAcceptor(stockCode),
            createRequest.getAcceptUserId()
        );
    }

    private User getAcceptor(String stockCode) {
        return userService.getAcceptorByStockCode(stockCode)
            .orElseThrow(() -> new BadRequestException("전자문서 수임인 정보가 없습니다."));
    }
}
