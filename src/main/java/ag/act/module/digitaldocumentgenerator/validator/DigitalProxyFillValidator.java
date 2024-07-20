package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.dto.DigitalDocumentItemWithAnswerDto;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.repository.DigitalDocumentItemRepository;
import ag.act.validator.DefaultObjectValidator;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DigitalProxyFillValidator extends GrantorBaseDigitalDocumentFillValidator {
    private final DefaultObjectValidator defaultObjectValidator;
    private final DigitalDocumentAcceptorFillValidator digitalDocumentAcceptorFillValidator;
    private final DigitalDocumentItemRepository digitalDocumentItemRepository;

    public DigitalProxyFillValidator(
        DefaultObjectValidator defaultObjectValidator,
        DigitalDocumentAcceptorFillValidator digitalDocumentAcceptorFillValidator,
        DigitalDocumentItemRepository digitalDocumentItemRepository
    ) {
        this.defaultObjectValidator = defaultObjectValidator;
        this.digitalDocumentAcceptorFillValidator = digitalDocumentAcceptorFillValidator;
        this.digitalDocumentItemRepository = digitalDocumentItemRepository;
    }

    @Override
    protected void validateTypeSpecific(IGenerateHtmlDocumentDto dto) {
        final IDigitalProxy digitalProxy = (IDigitalProxy) dto.getDigitalDocument();
        final DigitalDocumentUser digitalDocumentUser = dto.getDigitalDocumentUser();

        validateShareHolderMeeting(digitalProxy);
        validateDesignatedAgentNames(digitalProxy);
        validateDigitalDocumentItems(digitalDocumentUser);

        digitalDocumentAcceptorFillValidator.validate(digitalProxy.getAcceptUserId());
    }

    private void validateShareHolderMeeting(IDigitalProxy digitalProxy) {
        if (StringUtils.isBlank(digitalProxy.getShareholderMeetingName())) {
            throw new InternalServerException("주주총회명이 존재하지 않습니다.");
        }
        if (digitalProxy.getShareholderMeetingDate() == null) {
            throw new InternalServerException("주주총회 일자가 존재하지 않습니다.");
        }
    }

    private void validateDesignatedAgentNames(IDigitalProxy digitalProxy) {
        if (StringUtils.isBlank(digitalProxy.getDesignatedAgentNames())) {
            throw new InternalServerException("수임인지정대리인이 존재하지 않습니다.");
        }
    }

    private void validateDigitalDocumentItems(DigitalDocumentUser digitalDocumentUser) {
        final List<DigitalDocumentItemWithAnswerDto> everyItemsWithAnswerByUser =
            digitalDocumentItemRepository.findEveryItemsWithAnswerByUser(
                digitalDocumentUser.getDigitalDocumentId(),
                digitalDocumentUser.getUserId()
            );

        if (Collections.isEmpty(everyItemsWithAnswerByUser)) {
            throw new InternalServerException("설문한 안건 내역이 존재하지 않습니다.");
        }

        everyItemsWithAnswerByUser.forEach(this::validateDigitalDocumentItem);
    }

    private void validateDigitalDocumentItem(DigitalDocumentItemWithAnswerDto item) {
        defaultObjectValidator.validateNotNull(item.getTitle(), "안건 제목이 존재하지 않습니다.");
        defaultObjectValidator.validateNotNull(item.getContent(), "안건 내용이 존재하지 않습니다.");
        defaultObjectValidator.validateNotNull(item.getIsLastItem(), "마지막 안건 여부가 존재하지 않습니다.");
        if (item.getIsLastItem()) {
            defaultObjectValidator.validateNotNull(item.getSelectValue(), "안건 선택값이 존재하지 않습니다.");
        }
    }
}
