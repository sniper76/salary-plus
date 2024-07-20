package ag.act.handler.admin.post;

import ag.act.api.AdminBoardGroupPostApiDelegate;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.GetPostsSearchDto;
import ag.act.facade.admin.post.AdminPostFacade;
import ag.act.model.CreatePostRequest;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdatePostRequest;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminBoardGroupPostApiDelegateImpl implements AdminBoardGroupPostApiDelegate {
    private final AdminPostFacade adminPostFacade;
    private final PageDataConverter pageDataConverter;
    private final AppRenewalDateProvider appRenewalDateProvider;


    @Override
    public ResponseEntity<GetBoardGroupPostResponse> getPosts(
        String boardGroup,
        String boardCategory,
        String searchType,
        String searchKeyword,
        Instant searchStartDate,
        Instant searchEndDate,
        String status,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final GetPostsSearchDto getPostsSearchDto = GetPostsSearchDto.builder()
            .searchKeyword(searchKeyword)
            .searchType(searchType)
            .boardGroupName(boardGroup)
            .boardCategoryName(boardCategory)
            .searchStartDate(getSearchStartDate(searchStartDate))
            .searchEndDate(getSearchEndDate(searchEndDate))
            .statusName(status)
            .pageRequest(pageDataConverter.convert(page, size, sorts))
            .build();

        return ResponseEntity.ok(adminPostFacade.getPosts(getPostsSearchDto));
    }

    @Override
    @UseGuards(IsAdminGuard.class)
    public ResponseEntity<PostDetailsDataResponse> createPost(
        String stockCode,
        String boardGroupName,
        @HtmlContentTarget CreatePostRequest createPostRequest
    ) {
        return ResponseEntity.ok(adminPostFacade.createPost(stockCode, boardGroupName, createPostRequest));
    }

    @Override
    @UseGuards(IsAdminGuard.class)
    public ResponseEntity<PostDetailsDataResponse> updatePost(
        String stockCode,
        String boardGroupName,
        Long postId,
        @HtmlContentTarget UpdatePostRequest updatePostRequest
    ) {
        return ResponseEntity.ok(adminPostFacade.updatePost(stockCode, boardGroupName, postId, updatePostRequest));
    }

    @Override
    public ResponseEntity<PostDetailsDataResponse> getPostDetails(String stockCode, String boardGroupName, Long postId) {
        return ResponseEntity.ok(adminPostFacade.getPostDetails(stockCode, boardGroupName, postId));
    }

    @Override
    @UseGuards(IsAdminGuard.class)
    public ResponseEntity<SimpleStringResponse> deletePost(String stockCode, String boardGroupName, Long postId) {
        return ResponseEntity.ok(adminPostFacade.deletePost(stockCode, boardGroupName, postId));
    }

    private LocalDateTime getSearchStartDate(Instant searchStartDate) {
        return Optional.ofNullable(searchStartDate)
            .map(DateTimeConverter::convert)
            .orElseGet(appRenewalDateProvider::getStartOfDay);
    }

    private LocalDateTime getSearchEndDate(Instant searchEndDate) {
        return Optional.ofNullable(searchEndDate)
            .map(DateTimeConverter::convert)
            .orElseGet(DateTimeUtil::getInfiniteEndDate);
    }
}
