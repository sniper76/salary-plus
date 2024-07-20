package ag.act.converter.post.poll;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.poll.PollResultSummaryDto;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.repository.interfaces.PollItemCount;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PollResponseConverter {
    private final PollResultSummaryConverter pollResultSummaryConverter;

    public PollResponse convert(Poll poll, Long postId) {
        if (poll == null) {
            return null;
        }

        final List<PollItemResponse> pollItemResponses = getPollItemResponses(poll, Map.of());
        return new ag.act.model.PollResponse()
            .id(poll.getId())
            .title(poll.getTitle())
            .postId(postId)
            .voteType(poll.getVoteType().name())
            .selectionOption(poll.getSelectionOption().name())
            .status(poll.getStatus().name())
            .targetEndDate(DateTimeConverter.convert(poll.getTargetEndDate()))
            .targetStartDate(DateTimeConverter.convert(poll.getTargetStartDate()))
            .createdAt(DateTimeConverter.convert(poll.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(poll.getUpdatedAt()))
            .pollItems(pollItemResponses)
            ;
    }

    public PollResponse convertWithAnswer(
        Poll poll, List<PollItemCount> voteItemList, List<PollAnswer> userAnswerList, Long postId
    ) {
        if (poll == null) {
            return null;
        }

        final PollResultSummaryDto resultSummaryDto = pollResultSummaryConverter.convert(voteItemList);

        final List<PollItemResponse> pollItemResponses = getPollItemResponses(poll, voteItemList);
        return new ag.act.model.PollResponse()
            .id(poll.getId())
            .title(poll.getTitle())
            .content(poll.getContent())
            .postId(postId)
            .voteType(poll.getVoteType().name())
            .selectionOption(poll.getSelectionOption().name())
            .status(poll.getStatus().name())
            .targetEndDate(DateTimeConverter.convert(poll.getTargetEndDate()))
            .targetStartDate(DateTimeConverter.convert(poll.getTargetStartDate()))
            .voteTotalCount(resultSummaryDto.voteTotalCount())
            .voteTotalStockSum(resultSummaryDto.voteSumStockQuantity())
            .createdAt(DateTimeConverter.convert(poll.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(poll.getUpdatedAt()))
            .pollItems(pollItemResponses)
            .answers(getPollAnswerResponses(poll, userAnswerList));
    }

    public List<PollResponse> convertWithAnswer(
        List<Poll> polls, Map<Long, List<PollItemCount>> voteItemMap, Map<Long, List<PollAnswer>> userAnswerMap, Long postId
    ) {
        if (polls == null) {
            return null;
        }

        return polls.stream()
            .map(poll -> {
                final PollResultSummaryDto resultSummaryDto = getResultSummaryDto(voteItemMap, poll.getId());

                final List<PollItemResponse> pollItemResponses = getPollItemResponses(poll, voteItemMap);

                return new PollResponse()
                    .id(poll.getId())
                    .title(poll.getTitle())
                    .content(poll.getContent())
                    .postId(postId)
                    .voteType(poll.getVoteType().name())
                    .selectionOption(poll.getSelectionOption().name())
                    .status(poll.getStatus().name())
                    .targetEndDate(DateTimeConverter.convert(poll.getTargetEndDate()))
                    .targetStartDate(DateTimeConverter.convert(poll.getTargetStartDate()))
                    .voteTotalCount(resultSummaryDto.voteTotalCount())
                    .voteTotalStockSum(resultSummaryDto.voteSumStockQuantity())
                    .createdAt(DateTimeConverter.convert(poll.getCreatedAt()))
                    .updatedAt(DateTimeConverter.convert(poll.getUpdatedAt()))
                    .pollItems(pollItemResponses)
                    .answers(getPollAnswerResponses(poll, userAnswerMap));
            }).toList();
    }

    private PollResultSummaryDto getResultSummaryDto(Map<Long, List<PollItemCount>> voteItemMap, Long pollId) {
        if (voteItemMap.isEmpty() || voteItemMap.get(pollId) == null) {
            return new PollResultSummaryDto(0, 0L);
        }
        return pollResultSummaryConverter.convert(voteItemMap.get(pollId));
    }

    @NotNull
    private List<ag.act.model.PollAnswerResponse> getPollAnswerResponses(Poll poll, List<PollAnswer> userAnswerList) {
        return userAnswerList.stream()
            .map(pollAnswer -> convertPollAnswer(poll.getId(), pollAnswer))
            .toList();
    }

    private List<ag.act.model.PollAnswerResponse> getPollAnswerResponses(Poll poll, Map<Long, List<PollAnswer>> userAnswerMap) {
        if (userAnswerMap == null || userAnswerMap.get(poll.getId()) == null) {
            return null;
        }

        return userAnswerMap.get(poll.getId()).stream()
            .map(pollAnswer -> convertPollAnswer(poll.getId(), pollAnswer))
            .toList();
    }

    @NotNull
    private List<ag.act.model.PollItemResponse> getPollItemResponses(Poll poll, List<PollItemCount> voteItemList) {
        return poll.getPollItemList()
            .stream()
            .map(pollItem -> convertPollItem(
                    poll.getId(),
                    pollItem,
                    voteItemList.stream().filter(it -> Objects.equals(pollItem.getId(), it.getPollItemId())).toList()
                )
            ).toList();
    }

    @NotNull
    private List<ag.act.model.PollItemResponse> getPollItemResponses(Poll poll, Map<Long, List<PollItemCount>> pollItemCountMap) {
        List<PollItemCount> pollItemCounts = new ArrayList<>();
        if (!pollItemCountMap.isEmpty() && pollItemCountMap.get(poll.getId()) != null) {
            pollItemCounts = pollItemCountMap.get(poll.getId());
        }
        final List<PollItemCount> pollItemCountList = pollItemCounts;
        return poll.getPollItemList()
            .stream()
            .map(pollItem -> convertPollItem(
                    poll.getId(),
                    pollItem,
                    pollItemCountList.stream().filter(it -> Objects.equals(pollItem.getId(), it.getPollItemId())).toList()
                )
            ).toList();
    }

    private ag.act.model.PollItemResponse convertPollItem(
        Long pollId, PollItem pollItem,
        List<PollItemCount> pollItemCountList
    ) {
        final PollResultSummaryDto resultSummaryDto = pollResultSummaryConverter.convert(pollItemCountList);

        return new ag.act.model.PollItemResponse()
            .id(pollItem.getId())
            .text(pollItem.getText())
            .pollId(pollId)
            .voteItemCount(resultSummaryDto.voteTotalCount())
            .voteItemStockSum(resultSummaryDto.voteSumStockQuantity())
            .status(pollItem.getStatus().name())
            .createdAt(DateTimeConverter.convert(pollItem.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(pollItem.getUpdatedAt()));
    }

    private ag.act.model.PollAnswerResponse convertPollAnswer(Long pollId, PollAnswer pollAnswer) {
        return new ag.act.model.PollAnswerResponse()
            .id(pollAnswer.getId())
            .pollId(pollId)
            .pollItemId(pollAnswer.getPollItemId())
            .userId(pollAnswer.getUserId())
            .status(pollAnswer.getStatus().name())
            .createdAt(DateTimeConverter.convert(pollAnswer.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(pollAnswer.getUpdatedAt()));
    }
}
