package ag.act.validator.post;

import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.repository.BoardRepository;
import ag.act.repository.PostRepository;
import ag.act.util.StatusUtil;
import ag.act.validator.DefaultObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockBoardGroupPostCategoryChangeValidator {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final DefaultObjectValidator defaultObjectValidator;
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public Post validate(
        Long postId,
        String stockCode,
        String boardGroupName,
        String boardCategoryName
    ) {
        final Post post = getPostForUpdate(postId);
        validateStatus(post);

        final Optional<BoardCategory> changeBoardCategory = Optional.ofNullable(BoardCategory.fromValue(boardCategoryName));
        final BoardGroup changeBoardGroup = BoardGroup.fromValue(boardGroupName);

        if (changeBoardCategory.isEmpty() || !isChangeableBoardGroup(changeBoardGroup)) {
            return post;
        }

        validateNewCategoryBelongsToSameBoardGroup(post.getBoard().getCategory(), changeBoardCategory.get());

        // TODO 데이터 변경 파트가 여기 있는데. 어찌 해야 할지.. 감이 안 옴. ㅠㅠ
        final Board afterBoard = getBoardByStockCodeAndCategory(stockCode, changeBoardCategory.get());
        post.setBoardId(afterBoard.getId());
        post.setBoard(afterBoard);

        stockBoardGroupPostValidator.validateBoardGroupAndStockCodeOfBoardGroupPost(post, stockCode, changeBoardGroup);

        return post;
    }

    private Board getBoardByStockCodeAndCategory(String stockCode, BoardCategory boardCategory) {
        return boardRepository.findByStockCodeAndCategory(stockCode, boardCategory)
            .orElseThrow(() -> new BadRequestException("게시판을 찾을 수 없습니다."));
    }

    private void validateStatus(Post post) {
        defaultObjectValidator.validateStatus(post, StatusUtil.getDeleteStatuses(), "이미 삭제된 게시글입니다.");
    }

    private boolean isChangeableBoardGroup(BoardGroup changeBoardGroup) {
        return !changeBoardGroup.isActionBoardGroup();
    }

    private void validateNewCategoryBelongsToSameBoardGroup(BoardCategory currentBoardCategory, BoardCategory changeBoardCategory) {
        if (currentBoardCategory.getBoardGroup() != changeBoardCategory.getBoardGroup()) {
            throw new BadRequestException("동일 그룹내의 게시물만 카테고리 이동이 가능합니다.");
        }
    }

    private Post getPostForUpdate(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BadRequestException("수정할 게시글이 없습니다."));
    }
}
