package ag.act.facade.admin.post;

import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.converter.post.createpostrequestdto.CreatePostRequestDtoConverter;
import ag.act.core.guard.BoardGroupPostGuard;
import ag.act.core.guard.UseGuards;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.GetPostsSearchDto;
import ag.act.dto.download.DownloadFile;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.Post;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.service.poll.PollDownloadService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockServiceValidator;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostDeleteService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostUpdateService;
import ag.act.util.DownloadFileUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminPostFacade {
    private final PostService postService;
    private final StockServiceValidator stockServiceValidator;
    private final BoardGroupPostResponseConverter boardGroupPostResponseConverter;
    private final StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    private final StockBoardGroupPostUpdateService stockBoardGroupPostUpdateService;
    private final StockBoardGroupPostDeleteService stockBoardGroupPostDeleteService;
    private final StockBoardGroupPostService stockBoardGroupPostService;
    private final PollDownloadService pollDownloadService;
    private final CreatePostRequestDtoConverter createPostRequestDtoConverter;

    public ag.act.model.GetBoardGroupPostResponse getPosts(GetPostsSearchDto getPostsSearchDto) {
        if (getPostsSearchDto.isSearchByStockCode()) {
            stockServiceValidator.validateStockCode(getPostsSearchDto.getSearchKeyword());
        }

        return boardGroupPostResponseConverter.convert(postService.getBoardPosts(getPostsSearchDto));
    }

    public ag.act.model.PostDetailsDataResponse createPost(
        String stockCode,
        String boardGroupName,
        ag.act.model.CreatePostRequest createPostRequest
    ) {
        return stockBoardGroupPostCreateService.createBoardGroupPost(
            createPostRequestDtoConverter.convert(stockCode, boardGroupName, createPostRequest)
        );
    }

    @UseGuards({BoardGroupPostGuard.class})
    public ag.act.model.PostDetailsDataResponse updatePost(
        String stockCode, String boardGroupName, Long postId, ag.act.model.UpdatePostRequest updatePostRequest
    ) {
        return stockBoardGroupPostUpdateService.updateBoardGroupPost(
            new UpdatePostRequestDto(stockCode, boardGroupName, postId, updatePostRequest)
        );
    }

    @UseGuards({BoardGroupPostGuard.class})
    public ag.act.model.PostDetailsDataResponse getPostDetails(String stockCode, String boardGroupName, Long postId) {
        return stockBoardGroupPostService.getPostDetails(postId, stockCode, boardGroupName);
    }

    @UseGuards({BoardGroupPostGuard.class})
    public ag.act.model.SimpleStringResponse deletePost(String stockCode, String boardGroupName, Long postId) {
        return stockBoardGroupPostDeleteService.deleteBoardGroupPost(
            new DeletePostRequestDto(stockCode, boardGroupName, postId)
        );
    }

    @SuppressWarnings("ConstantConditions")
    public DownloadFile downloadPostPollResultsInCsv(Long postId) {
        final HttpServletResponse response = RequestContextHolder.getResponse();
        final Post post = postService.getPost(postId);
        final String csvFilename = pollDownloadService.getCsvFilename(post);
        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            pollDownloadService.downloadPostPollResultsInCsv(post, response);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("설문결과 CSV 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }
}
