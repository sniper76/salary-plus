package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostApiDelegate;
import ag.act.converter.post.createpostrequestdto.CreatePostRequestDtoConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.annotation.PageableOverrider;
import ag.act.core.guard.ContentCreateGuard;
import ag.act.core.guard.ContentUpdateGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.IsSolidarityLeaderGuard;
import ag.act.core.guard.PostDetailGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.digitaldocument.HolderListReadAndCopyDocumentDto;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.facade.digitaldocument.holderlistreadandcopy.HolderListReadAndCopyPostFacade;
import ag.act.facade.digitaldocument.holderlistreadandcopy.HolderListReadAndCopyPostRequestGenerator;
import ag.act.facade.virtualboard.VirtualBoardGroupPostFacade;
import ag.act.model.CreatePostRequest;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdatePostRequest;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostService;
import ag.act.validator.GetBoardGroupPostRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockBoardGroupPostApiDelegateImpl implements StockBoardGroupPostApiDelegate {

    private final StockBoardGroupPostService stockBoardGroupPostService;
    private final VirtualBoardGroupPostFacade virtualBoardGroupPostFacade;
    private final StockBoardGroupPostCustomSortWrapper stockBoardGroupPostCustomSortWrapper;
    private final GetBoardGroupPostRequestValidator getBoardGroupPostRequestValidator;
    private final CreatePostRequestDtoConverter createPostRequestDtoConverter;
    private final HolderListReadAndCopyPostFacade holderListReadAndCopyPostFacade;
    private final HolderListReadAndCopyPostRequestGenerator holderListReadAndCopyPostRequestGenerator;

    @UseGuards({IsActiveUserGuard.class, ContentCreateGuard.class})
    @Override
    public ResponseEntity<ag.act.model.PostDetailsDataResponse> createBoardGroupPost(
        String stockCode,
        String boardGroupName,
        @HtmlContentTarget CreatePostRequest createPostRequest
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostService.createBoardGroupPost(
                createPostRequestDtoConverter.convert(stockCode, boardGroupName, createPostRequest)
            )
        );
    }

    @UseGuards({IsActiveUserGuard.class})
    @Override
    public ResponseEntity<SimpleStringResponse> deleteBoardGroupPost(
        String stockCode,
        String boardGroupName,
        Long postId
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostService.deleteBoardGroupPost(
                new DeletePostRequestDto(stockCode, boardGroupName, postId)
            )
        );
    }

    @UseGuards({IsActiveUserGuard.class, ContentUpdateGuard.class})
    @Override
    public ResponseEntity<ag.act.model.PostDetailsDataResponse> updateBoardGroupPost(
        String stockCode,
        String boardGroupName,
        Long postId,
        @HtmlContentTarget UpdatePostRequest updatePostRequest
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostService.updateBoardGroupPost(
                new UpdatePostRequestDto(stockCode, boardGroupName, postId, updatePostRequest)
            )
        );
    }

    @UseGuards({PostDetailGuard.class})
    @Override
    public ResponseEntity<ag.act.model.PostDataResponse> getBoardGroupPostDetail(
        String stockCode,
        String boardGroupName,
        Long postId
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostService.getBoardGroupPostDetailWithUpdateViewCount(postId, stockCode, boardGroupName)
        );
    }

    @PageableOverrider(
        defaultSize = 10,
        defaultSort = "activeStartDate:DESC",
        possibleSortNames = {"createdAt", "activeStartDate", "viewUserCount", "viewCount", "likeCount"}
    )
    @Override
    public ResponseEntity<GetBoardGroupPostResponse> getBoardGroupPosts(
        String stockCode,
        String boardGroupName,
        String boardCategory,
        List<String> boardCategories,
        Integer page,
        Integer size,
        List<String> sorts,
        Boolean isExclusiveToHolders,
        Boolean isExclusiveToPublic,
        Boolean isNotDeleted
    ) {

        List<String> boardCategoryNames = getBoardGroupPostRequestValidator.validateAndGetCategories(boardCategory, boardCategories);
        getBoardGroupPostRequestValidator.validateBoardGroupAndCategories(boardGroupName, boardCategoryNames);

        final GetBoardGroupPostDto getBoardGroupPostDto = GetBoardGroupPostDto.builder()
            .stockCode(stockCode)
            .boardGroupName(boardGroupName)
            .boardCategories(boardCategoryNames)
            .isExclusiveToHolders(isExclusiveToHolders)
            .isExclusiveToPublic(isExclusiveToPublic)
            .isNotDeleted(isNotDeleted)
            .build();

        GetBoardGroupPostResponse response = stockBoardGroupPostCustomSortWrapper.runWith(page, size, sorts,
            (pageRequest) -> VirtualBoardGroup.isVirtualBoardGroupName(getBoardGroupPostDto.getBoardGroupName())
                ? virtualBoardGroupPostFacade.getVirtualBoardGroupPosts(getBoardGroupPostDto, pageRequest)
                : stockBoardGroupPostService.getBoardGroupPosts(getBoardGroupPostDto, pageRequest)
        );

        return ResponseEntity.ok(response);
    }

    @UseGuards({IsActiveUserGuard.class, ContentCreateGuard.class, IsSolidarityLeaderGuard.class})
    @Override
    public ResponseEntity<PostDetailsDataResponse> createPostHolderListReadAndCopy(
        String stockCode,
        String boardGroupName,
        String request,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        return ResponseEntity.ok(
            holderListReadAndCopyPostFacade.createPostWithHolderListReadAndCopyDocument(
                new HolderListReadAndCopyDocumentDto(
                    stockCode,
                    boardGroupName,
                    holderListReadAndCopyPostRequestGenerator.generate(stockCode, request),
                    signImage,
                    idCardImage,
                    bankAccountImages,
                    hectoEncryptedBankAccountPdf
                )
            )
        );
    }
}
