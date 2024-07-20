package ag.act.service.post.duplication;

import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.dto.PostDuplicateDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.enums.BoardCategory;
import ag.act.exception.BadRequestException;
import ag.act.model.PostDetailsDataResponse;
import ag.act.service.BoardService;
import ag.act.service.post.PostImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class PostDuplicateService {
    private final BoardService boardService;
    private final PostImageService postImageService;
    private final PostUpdateResponseConverter postUpdateResponseConverter;
    private final PostDuplicateProcessor postDuplicateProcessor;

    public int duplicatePosts(PostDuplicateDto postDuplicateDto) {
        List<String> stockCodes = postDuplicateDto.getStockCodes();
        Post post = postDuplicateDto.getPost();
        Board board = postDuplicateDto.getBoard();
        PostUserProfile postUserProfile = postDuplicateDto.getPostUserProfile();
        List<PostImage> postImages = postDuplicateDto.getPostImages();
        BoardCategory category = board.getCategory();

        final AtomicInteger count = new AtomicInteger();

        stockCodes
            .forEach(stockCode -> {
                final Optional<Board> newBoardOptional = boardService.findBoard(stockCode, category);
                if (newBoardOptional.isEmpty() || isSameBoard(board, newBoardOptional.get())) {
                    return;
                }

                postDuplicateProcessor.copyPost(post, postUserProfile, postImages, newBoardOptional.get());

                count.incrementAndGet();
            });

        return count.get();
    }

    public PostDetailsDataResponse duplicatePost(PostDuplicateDto postDuplicateDto) {
        final String stockCode = postDuplicateDto.getStockCodes().stream().findFirst()
            .orElseThrow(() -> new BadRequestException("종목 코드가 존재하지 않습니다."));

        final Board newBoard = getBoardNoneNull(stockCode, postDuplicateDto.getBoard().getCategory());

        final Post post = postDuplicateProcessor.copyPost(
            postDuplicateDto.getPost(),
            postDuplicateDto.getPostUserProfile(),
            postDuplicateDto.getPostImages(),
            newBoard
        );

        return new ag.act.model.PostDetailsDataResponse()
            .data(
                postUpdateResponseConverter.convert(
                    post,
                    post.getBoard(),
                    false,
                    postImageService.getFileContentsByPostId(post.getId()),
                    post.getPostUserProfile()
                )
            );
    }

    private Board getBoardNoneNull(String stockCode, BoardCategory category) {
        return boardService.findBoard(stockCode, category)
            .orElseThrow(() -> new BadRequestException("복제 대상 종목의 카테고리 그룹이 없습니다."));
    }

    private boolean isSameBoard(Board board, Board newBoard) {
        return Objects.equals(board.getId(), newBoard.getId());
    }
}
