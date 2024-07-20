package ag.act.service.digitaldocument;

import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.InternalServerException;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserHistoryService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalDocumentAcceptUserService {
    private final UserService userService;
    private final StockAcceptorUserHistoryService stockAcceptorUserHistoryService;

    public User getAcceptUser(DigitalDocument digitalDocument) {
        if (isEtcDocumentOrCertificationOfContents(digitalDocument)) {
            return null;
        }
        return userService.getAcceptorByUserIdAndStockCode(getAcceptUserId(digitalDocument), digitalDocument.getStockCode())
            .orElseThrow(() -> new InternalServerException(createErrorMessage(digitalDocument.getType())));
    }

    public User getAcceptUserForPostDetail(DigitalDocument digitalDocument) {
        if (isEtcDocumentOrCertificationOfContents(digitalDocument)) {
            return null;
        }
        return stockAcceptorUserHistoryService.getUserByStockAcceptorUserHistory(
            digitalDocument.getStockCode(), getAcceptUserId(digitalDocument)
        );
    }

    private boolean isEtcDocumentOrCertificationOfContents(DigitalDocument digitalDocument) {
        return (digitalDocument.getType() == DigitalDocumentType.ETC_DOCUMENT
            || digitalDocument.getType() == DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT);
    }

    private Long getAcceptUserId(DigitalDocument digitalDocument) {
        final Long acceptUserId = digitalDocument.getAcceptUserId();

        if (acceptUserId == null) {
            throw new InternalServerException(createErrorMessage(digitalDocument.getType()));
        }
        return acceptUserId;
    }

    private String createErrorMessage(DigitalDocumentType digitalDocumentType) {
        return "전자문서 %s 정보가 없습니다.".formatted(digitalDocumentType.getAcceptUserDisplayName());
    }
}
