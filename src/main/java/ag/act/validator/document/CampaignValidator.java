package ag.act.validator.document;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.CreatePostRequest;
import ag.act.validator.DefaultRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {
    private final DefaultRequestValidator defaultRequestValidator;

    public void validate(CreateCampaignRequest createCampaignRequest) {
        validateBoardGroup(createCampaignRequest);
        validateStockGroupId(createCampaignRequest.getStockGroupId());
        validateBoardCategory(createCampaignRequest.getCreatePostRequest());
    }

    private void validateBoardCategory(CreatePostRequest createPostRequest) {
        final BoardCategory boardCategory = BoardCategory.fromValue(createPostRequest.getBoardCategory());
        if (!BoardCategory.getDigitalDocumentCampaignBoardCategories().contains(boardCategory)) {
            throw new BadRequestException("캠페인 등록은 설문과 10초서명만 가능합니다.");
        }
    }

    private void validateStockGroupId(Long stockGroupId) {
        defaultRequestValidator.validateNotNull(stockGroupId, "종목그룹 아이디를 확인하세요.");
    }

    private void validateBoardGroup(CreateCampaignRequest createCampaignRequest) {
        BoardGroup.fromValue(createCampaignRequest.getBoardGroupName());
    }
}
