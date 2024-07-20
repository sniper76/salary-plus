package ag.act.validator.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.entity.Board;
import ag.act.entity.User;
import ag.act.model.CreatePostRequest;
import ag.act.service.BoardService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.user.UserAnonymousCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockBoardGroupPostCreateValidator {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final SolidarityLeaderService solidarityLeaderService;
    private final BoardService boardService;
    private final UserAnonymousCountService userAnonymousCountService;

    public Board validateForCreatePostAndGet(CreatePostRequestDto createPostRequestDto) {
        final String stockCode = createPostRequestDto.getStockCode();
        final CreatePostRequest createPostRequest = createPostRequestDto.getCreatePostRequest();
        final User user = ActUserProvider.getNoneNull();
        final GetBoardGroupPostDto boardGroupPostDto = createPostRequestDto.getBoardGroupPostDto();

        stockBoardGroupPostValidator.validateUserPermission(createPostRequest, isSolidarityLeader(stockCode, user));
        stockBoardGroupPostValidator.validateCategory(createPostRequest);

        final Board board = stockBoardGroupPostValidator.validateBoardStockCode(
            boardService.getBoard(boardGroupPostDto),
            stockCode
        );

        if (createPostRequest.getIsAnonymous()) {
            userAnonymousCountService.validateAndIncreasePostCount(user.getId());
        }

        return board;
    }

    private boolean isSolidarityLeader(String stockCode, User user) {
        return solidarityLeaderService.isLeader(user.getId(), stockCode);
    }
}
