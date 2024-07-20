package ag.act.validator;

import ag.act.dto.admin.LinkDto;
import ag.act.enums.AppLinkType;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import ag.act.model.PopupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupValidator {
    private final DefaultRequestValidator defaultRequestValidator;
    private final LinkTypeValidator linkTypeValidator;

    public void validateUpdate(PopupRequest popupRequest) {
        validate(popupRequest);
    }

    public void validate(PopupRequest popupRequest) {
        validateMandatory(popupRequest);

        final PushTargetType stockTargetType = PushTargetType.fromValue(popupRequest.getStockTargetType());

        validatePopupDisplayTargetType(popupRequest);
        validateStockGroupId(popupRequest, stockTargetType);
        validateStock(popupRequest, stockTargetType);
        validateTargetDateTime(popupRequest);

        final LinkDto linkDto = generateLinkDto(popupRequest);

        linkTypeValidator.validate(linkDto, stockTargetType);
    }

    private LinkDto generateLinkDto(PopupRequest popupRequest) {
        return LinkDto.builder()
            .linkTitle(popupRequest.getLinkTitle())
            .postId(popupRequest.getPostId())
            .linkType(AppLinkType.fromValue(popupRequest.getLinkType()))
            .build();
    }

    private PopupDisplayTargetType validatePopupDisplayTargetType(PopupRequest popupRequest) {
        return PopupDisplayTargetType.fromValue(popupRequest.getDisplayTargetType());
    }

    private void validateTargetDateTime(PopupRequest popupRequest) {
        if (popupRequest.getTargetEndDatetime().isBefore(popupRequest.getTargetStartDatetime())) {
            throw new BadRequestException("팝업 게시 종료일이 시작일보다 이전일 수 없습니다.");
        }
    }

    private void validateMandatory(PopupRequest popupRequest) {
        defaultRequestValidator.validateNotNull(popupRequest.getDisplayTargetType(), "타겟 화면을 선택하세요.");
        defaultRequestValidator.validateNotNull(popupRequest.getStockTargetType(), "타겟 종목/그룹을 선택하세요.");
        defaultRequestValidator.validateNotNull(popupRequest.getTitle(), "제목을 확인하세요.");
        defaultRequestValidator.validateNotNull(popupRequest.getContent(), "내용을 확인하세요.");
    }

    private void validateStockGroupId(PopupRequest popupRequest, PushTargetType stockTargetType) {
        if (stockTargetType == PushTargetType.STOCK_GROUP) {
            defaultRequestValidator.validateNotNull(popupRequest.getStockGroupId(), "종목그룹 아이디를 확인하세요.");
        }
    }

    private void validateStock(PopupRequest popupRequest, PushTargetType stockTargetType) {
        if (stockTargetType == PushTargetType.STOCK) {
            defaultRequestValidator.validateNotNull(popupRequest.getStockCode(), "종목을 확인하세요.");
        }
    }
}
