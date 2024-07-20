package ag.act.validator;

import ag.act.dto.admin.LinkDto;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkTypeValidator {
    private final DefaultRequestValidator defaultRequestValidator;

    public void validate(LinkDto linkDto, PushTargetType stockTargetType) {
        if (linkDto.getLinkType() == AppLinkType.LINK) {
            defaultRequestValidator.validateNotNull(linkDto.getLinkTitle(), "링크 제목을 확인하세요.");
            defaultRequestValidator.validateNotNull(linkDto.getPostId(), "링크 대상 게시글 번호를 확인하세요.");
        }
        if (linkDto.getLinkType() == AppLinkType.NONE) {
            if (linkDto.getLinkTitle() != null || linkDto.getPostId() != null) {
                throw new BadRequestException("링크 타입 없을때 링크 제목과 게시글 번호는 입력할 필요없습니다.");
            }
        }
        if (linkDto.getLinkType() != AppLinkType.LINK && linkDto.getPostId() != null) {
            throw new BadRequestException("링크 타입 이외에는 게시글 번호를 입력할 필요없습니다.");
        }
        stockTargetType.validateAppLinkType(linkDto.getLinkType());
    }
}
