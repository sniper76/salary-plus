package ag.act.service.download.csv;

import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class PollCsvRecordGenerator {
    private static final String ONE_PERSON = "1";
    private static final String PLACEHOLDER = "0";

    public String[] toCsvRecord(
        List<PollItem> sortedPollItems,
        List<PollAnswer> answers,
        User user,
        Post post
    ) {
        // for the given user, stock quantity the same for all answers
        final Long stockQuantity = answers.get(0).getStockQuantity();

        // "유저아이디", "유저이름", "종목코드", "종목명", "게시물번호", "보유주식수"
        final List<String> defaultValues = List.of(
            user.getId().toString(),
            user.getName(),
            post.getBoard().getStockCode(),
            post.getBoard().getStock().getName(),
            post.getId().toString(),
            stockQuantity.toString()
        );

        final List<String> userVoteResults = getUserVoteResultRow(sortedPollItems, answers, stockQuantity);

        return Stream.concat(
            defaultValues.stream(),
            userVoteResults.stream()
        ).toArray(String[]::new);
    }

    private List<String> getUserVoteResultRow(
        List<PollItem> sortedPollItems, List<PollAnswer> answers, Long stockQuantity
    ) {

        return sortedPollItems.stream()
            .flatMap(pollItem -> {
                boolean userVotedForItem = userVotedForPollItem(pollItem, answers);

                return userVotedForItem
                    ? Stream.of(ONE_PERSON, stockQuantity.toString())
                    : Stream.of(PLACEHOLDER, PLACEHOLDER);
            })
            .toList();
    }

    private boolean userVotedForPollItem(PollItem pollItem, List<PollAnswer> answers) {
        return answers.stream()
            .anyMatch(answer -> answer.getPollItemId().equals(pollItem.getId()));
    }
}
