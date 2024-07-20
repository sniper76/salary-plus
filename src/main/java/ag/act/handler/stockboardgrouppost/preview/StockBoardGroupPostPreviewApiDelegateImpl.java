package ag.act.handler.stockboardgrouppost.preview;

import ag.act.api.StockBoardGroupPostPreviewApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.facade.virtualboard.VirtualBoardGroupPostFacade;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostService;
import ag.act.util.PostPreviewsSizeProvider;
import ag.act.validator.GetBoardGroupPostRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockBoardGroupPostPreviewApiDelegateImpl implements StockBoardGroupPostPreviewApiDelegate {

    private static final String PREVIEW_SORT = "activeStartDate:DESC";
    private static final int PREVIEW_PAGE = 1;
    private final StockBoardGroupPostService stockBoardGroupPostService;
    private final VirtualBoardGroupPostFacade virtualBoardGroupPostFacade;
    private final GetBoardGroupPostRequestValidator getBoardGroupPostRequestValidator;
    private final PageDataConverter pageDataConverter;
    private final PostPreviewsSizeProvider postPreviewsSizeProvider;

    public ResponseEntity<GetBoardGroupPostResponse> getBoardGroupPostPreviews(
        String stockCode,
        String boardGroupName,
        String boardCategory,
        List<String> boardCategories
    ) {
        List<String> boardCategoryNames = getBoardGroupPostRequestValidator.validateAndGetCategories(boardCategory, boardCategories);
        getBoardGroupPostRequestValidator.validateBoardGroupAndCategories(boardGroupName, boardCategoryNames);

        final GetBoardGroupPostDto boardGroupPostDto = GetBoardGroupPostDto.builder()
            .stockCode(stockCode)
            .boardGroupName(boardGroupName)
            .boardCategories(boardCategories)
            .isExclusiveToHolders(Boolean.FALSE) //주주에게만 공개 글만 조회 필터 적용X
            .isExclusiveToPublic(Boolean.FALSE) //주주에게만 공개 글 제외하여 조회 필터 적용X
            .isNotDeleted(Boolean.TRUE) // 삭제되지 않은 글만 조회
            .build();

        final PageRequest pageRequest = pageDataConverter.convert(PREVIEW_PAGE, postPreviewsSizeProvider.get(), PREVIEW_SORT);

        return ResponseEntity.ok(getResponse(boardGroupPostDto, pageRequest));
    }

    private GetBoardGroupPostResponse getResponse(GetBoardGroupPostDto getBoardGroupPostDto, PageRequest pageRequest) {
        return VirtualBoardGroup.isVirtualBoardGroupName(getBoardGroupPostDto.getBoardGroupName())
            ? virtualBoardGroupPostFacade.getVirtualBoardGroupPosts(getBoardGroupPostDto, pageRequest)
            : stockBoardGroupPostService.getBoardGroupPosts(getBoardGroupPostDto, pageRequest);
    }
}
