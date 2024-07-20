package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.model.UpdatePostRequest;
import ag.act.util.DateTimeUtil;
import ag.act.validator.post.StockBoardGroupPostCategoryChangeValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostUpdatePreProcessor {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final StockBoardGroupPostCategoryChangeValidator stockBoardGroupPostCategoryChangeValidator;

    public Post proceed(UpdatePostRequestDto updatePostRequestDto) {

        final Post post = stockBoardGroupPostCategoryChangeValidator.validate(
            updatePostRequestDto.getPostId(),
            updatePostRequestDto.getStockCode(),
            updatePostRequestDto.getBoardGroupName(),
            updatePostRequestDto.getUpdatePostRequest().getBoardCategory()
        );

        stockBoardGroupPostValidator.validateAuthor(ActUserProvider.getNoneNull(), post.getUserId(), "게시글");
        stockBoardGroupPostValidator.validateChangeAnonymous(post, updatePostRequestDto.getUpdatePostRequest().getIsAnonymous());

        //ready in progress 경우 종료일 현재시간 이후로 가능
        //설문 모두싸인 동일하게
        //앱 수정시 사용
        stockBoardGroupPostValidator.validateUpdateTargetDateWhenPollsAreNull(post, updatePostRequestDto);
        stockBoardGroupPostValidator.validateUpdateTargetDateForDigitalProxy(post, updatePostRequestDto);
        stockBoardGroupPostValidator.validateUpdateTargetDateForDigitalDocument(post, updatePostRequestDto);

        if (isNotGlobalEventAndNotEmptyActiveDate(updatePostRequestDto, post)) {
            throw new BadRequestException("게시 시작일이나 종료일은 이벤트/공지 게시글만 수정 가능합니다.");
        }
        if (isGlobalEventCategory(post.getBoard().getCategory())) {
            updateActiveStartAndEndDates(post, updatePostRequestDto);
        }

        //polls 수정시
        stockBoardGroupPostValidator.validateUpdateTargetDateForMultiplePolls(post, updatePostRequestDto.getUpdatePostRequest().getPolls());

        return post;
    }

    private boolean isNotGlobalEventAndNotEmptyActiveDate(UpdatePostRequestDto updatePostRequestDto, Post post) {
        return !isGlobalEventCategory(post.getBoard().getCategory())
            && (updatePostRequestDto.getUpdatePostRequest().getActiveStartDate() != null
            || updatePostRequestDto.getUpdatePostRequest().getActiveEndDate() != null);
    }

    private void updateActiveStartAndEndDates(Post post, UpdatePostRequestDto updatePostRequestDto) {
        final LocalDateTime currentLocalDateTime = LocalDateTime.now();

        //게시 시작일 이후는 일자 수정불가
        final UpdatePostRequest updatePostRequest = updatePostRequestDto.getUpdatePostRequest();
        if (isActiveStartDateInFuture(post, currentLocalDateTime)) {
            setNewActiveStartDate(post, updatePostRequest, currentLocalDateTime);
        }
        //게시 종료일은 수정가능
        setNewActiveEndDate(post, updatePostRequest);
    }

    private void setNewActiveEndDate(Post post, UpdatePostRequest updatePostRequest) {
        if (post.getActiveEndDate() == null && updatePostRequest.getActiveEndDate() == null) {
            return;
        }

        final LocalDateTime requestActiveEndDate = DateTimeConverter.convert(updatePostRequest.getActiveEndDate());

        if (post.getActiveStartDate().isAfter(requestActiveEndDate)) {
            throw new BadRequestException("게시 종료일을 시작일보다 과거로 수정할 수는 없습니다.");
        }

        post.setActiveEndDate(requestActiveEndDate);
    }

    private void setNewActiveStartDate(Post post, UpdatePostRequest updatePostRequest, LocalDateTime currentLocalDateTime) {
        if (updatePostRequest.getActiveStartDate() == null) {
            return;
        }

        final LocalDateTime requestActiveStartDate = DateTimeConverter.convert(updatePostRequest.getActiveStartDate());

        if (!DateTimeUtil.isSimilarLocalDateTime(post.getActiveStartDate(), requestActiveStartDate)) {
            if (currentLocalDateTime.isAfter(requestActiveStartDate)) {
                throw new BadRequestException("게시 시작일은 현재일 이후로 수정할 수 없습니다.");
            }

            post.setActiveStartDate(requestActiveStartDate);
        }
    }

    public boolean isActiveStartDateInFuture(Post post, LocalDateTime currentLocalDateTime) {
        return post.getActiveStartDate().isAfter(currentLocalDateTime);
    }

    private boolean isGlobalEventCategory(BoardCategory boardCategory) {
        return BoardGroup.GLOBALEVENT.getCategories().contains(boardCategory);
    }
}
