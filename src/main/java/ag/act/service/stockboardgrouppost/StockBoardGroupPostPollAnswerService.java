package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.post.poll.PollAnswerResponseConverter;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.dto.poll.PollAnswersByTypeDto;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PostPollAnswerRequest;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.PollAnswerGenerator;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockBoardGroupPostPollAnswerService {
    private static final long ZERO_STOCK_QUANTITY = 0L;
    private final PollAnswerService pollAnswerService;
    private final PollAnswerResponseConverter pollAnswerResponseConverter;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final PollAnswerGenerator pollAnswerGenerator;
    private final UserHoldingStockService userHoldingStockService;
    private final GlobalBoardManager globalBoardManager;

    public PollAnswerDataArrayResponse createBoardGroupPostPollAnswer(
        String stockCode,
        String boardGroupName,
        Long postId,
        Long pollId,
        PostPollAnswerRequest postPollAnswerRequest
    ) {
        validatePostAndPoll(stockCode, boardGroupName, postId, pollId, postPollAnswerRequest.getPollAnswer());

        User user = ActUserProvider.getNoneNull();
        List<PollAnswer> pollAnswerList = getAnswerListWithStockQuantity(
            user.getId(), pollId, getStockQuantity(user.getId(), stockCode), postPollAnswerRequest.getPollAnswer()
        );
        List<PollAnswer> saveAnswerList = saveAnswerList(pollAnswerList);

        return toPollAnswerDataArrayResponse(saveAnswerList);
    }

    public PollAnswerDataArrayResponse updateBoardGroupPostPollAnswer(
        String stockCode,
        String boardGroupName,
        Long postId,
        Long pollId,
        PostPollAnswerRequest postPollAnswerRequest
    ) {
        validatePostAndPoll(stockCode, boardGroupName, postId, pollId, postPollAnswerRequest.getPollAnswer());

        final User user = ActUserProvider.getNoneNull();
        final List<PollAnswer> exitingPollAnswers = pollAnswerService.getAllByPollIdAndUserId(pollId, user.getId());
        final PollAnswersByTypeDto pollAnswersByTypeDto = pollAnswerGenerator.getPollAnswersByTypeDto(
            postPollAnswerRequest.getPollAnswer(),
            exitingPollAnswers
        );
        final List<PollAnswer> pollAnswersForDelete = pollAnswersByTypeDto.deletePollAnswers();
        final List<PollAnswer> pollAnswersForInsert = pollAnswersByTypeDto.insertPollAnswers();
        final List<PollAnswer> matchedPollAnswers = pollAnswersByTypeDto.matchPollAnswers();

        pollAnswerService.deleteAll(pollAnswersForDelete);

        List<PollAnswer> savedNewPollAnswers = saveAnswerList(
            makePollAnswers(
                pollId,
                user.getId(),
                getStockQuantity(user.getId(), stockCode),
                pollAnswersForInsert
            )
        );

        List<PollAnswer> finalPollAnswerList = Stream.concat(
                savedNewPollAnswers.stream(),
                matchedPollAnswers.stream()
            )
            .sorted(Comparator.comparingLong(PollAnswer::getPollItemId))
            .toList();

        return toPollAnswerDataArrayResponse(finalPollAnswerList);
    }

    private void validatePostAndPoll(
        String stockCode, String boardGroupName, Long postId, Long pollId, List<CreatePollAnswerItemRequest> createPollAnswerItems
    ) {
        final Post post = stockBoardGroupPostValidator.validateBoardGroupPost(postId, stockCode, boardGroupName, StatusUtil.getDeleteStatuses());

        validatePoll(post.getPolls(), pollId, createPollAnswerItems);
    }

    private void validatePoll(
        List<Poll> pollsOfPost, Long pollId, List<CreatePollAnswerItemRequest> pollAnswer
    ) {
        final Poll poll = getPoll(pollId, pollsOfPost);

        stockBoardGroupPostValidator.validatePollBeforeAnswer(poll, pollId);
        stockBoardGroupPostValidator.validatePollSingle(pollAnswer, poll.getSelectionOption());
        stockBoardGroupPostValidator.validatePollAnswers(pollAnswer, poll.getPollItemList());
    }

    private Poll getPoll(Long pollId, List<Poll> polls) {
        return polls.stream()
            .filter(it -> Objects.equals(it.getId(), pollId))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("해당하는 설문 정보가 존재하지 않습니다. id:%d".formatted(pollId)));
    }

    private Long getStockQuantity(Long userId, String stockCode) {
        if (globalBoardManager.isGlobalStockCode(stockCode)) {
            return ZERO_STOCK_QUANTITY;
        }

        return userHoldingStockService.findUserHoldingStock(userId, stockCode)
            .orElseThrow(() -> new NotFoundException("보유하고 있지 않은 주식입니다."))
            .getQuantity();
    }

    private List<PollAnswer> getAnswerListWithStockQuantity(
        Long userId, Long pollId, Long quantity, List<CreatePollAnswerItemRequest> requestList
    ) {
        return requestList.stream()
            .map(itemRequest -> pollAnswerService.makePollAnswer(pollId, userId, itemRequest.getPollItemId(), quantity))
            .toList();
    }

    private List<PollAnswer> makePollAnswers(Long pollId, Long userId, Long quantity, List<PollAnswer> itemList) {
        return itemList.stream()
            .map(pollAnswer -> pollAnswerService.makePollAnswer(pollId, userId, pollAnswer.getPollItemId(), quantity))
            .toList();
    }

    private List<PollAnswer> saveAnswerList(List<PollAnswer> requestList) {
        return pollAnswerService.saveAll(requestList);
    }

    private PollAnswerDataArrayResponse toPollAnswerDataArrayResponse(List<PollAnswer> answerList) {
        return new PollAnswerDataArrayResponse()
            .data(pollAnswerResponseConverter.convert(answerList));
    }
}
