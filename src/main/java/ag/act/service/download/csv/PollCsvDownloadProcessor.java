package ag.act.service.download.csv;

import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.service.download.AbstractCsvDownloadProcessor;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.user.UserService;
import ag.act.util.CSVWriterFactory;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PollCsvDownloadProcessor extends AbstractCsvDownloadProcessor {
    private static final int PAGE_SIZE = 50;
    private static final String PERSON_POSTFIX = "-주주수";
    private static final String STOCK_QUANTITY_POSTFIX = "-주식수";
    private static final List<String> DEFAULT_HEADERS = List.of(
        "유저아이디", "유저이름", "종목코드", "종목명", "게시물번호", "보유주식수"
    );

    private final PollCsvRecordGenerator pollCsvRecordGenerator;
    private final PollAnswerService pollAnswerService;
    private final UserService userService;

    public PollCsvDownloadProcessor(
        CSVWriterFactory csvWriterFactory,
        PollCsvRecordGenerator pollCsvRecordGenerator,
        PollAnswerService pollAnswerService,
        UserService userService
    ) {
        super(csvWriterFactory);
        this.pollCsvRecordGenerator = pollCsvRecordGenerator;
        this.pollAnswerService = pollAnswerService;
        this.userService = userService;
    }

    @SuppressWarnings({"RightCurly"})
    public void download(HttpServletResponse response, List<Post> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return;
        }

        try (ByteArrayOutputStream tempStream = new ByteArrayOutputStream()) {
            try (CSVWriter csvWriter = initializeCsvWriter(tempStream)) {
                Poll firstPoll = getPollNonNull(posts.get(0));
                List<PollItem> sortedPollItemsForHeader = getSortedPollItems(firstPoll);
                writeHeaders(csvWriter, findExtraHeaders(sortedPollItemsForHeader));

                posts.forEach(post -> {
                    Poll poll = getPollNonNull(post);
                    Long pollId = poll.getId();
                    List<PollItem> sortedPollItems = getSortedPollItems(poll);

                    Pageable pageable = getPageable();
                    Page<User> page;
                    do {
                        page = userService.findAllVotedInPoll(pollId, pageable);
                        generateCsvChunk(post, poll, sortedPollItems, page, csvWriter);
                        pageable = pageable.next();
                    } while (!page.isEmpty());
                });
            }

            response.getOutputStream().write(tempStream.toByteArray());
        } catch (IOException e) {
            throw new InternalServerException("설문결과 CSV 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    private Poll getPollNonNull(Post post) {
        return Optional.ofNullable(post.getFirstPoll())
            .orElseThrow(() -> new NotFoundException("해당 게시글의 설문을 찾을 수 없습니다."));
    }

    private List<PollItem> getSortedPollItems(Poll poll) {
        return poll.getPollItemList().stream()
            .sorted(Comparator.comparing(PollItem::getId))
            .toList();
    }

    private List<String> findExtraHeaders(List<PollItem> pollItems) {
        return pollItems.stream()
            .flatMap(pollItem ->
                Stream.of(PERSON_POSTFIX, STOCK_QUANTITY_POSTFIX)
                    .map(postfix -> pollItem.getText() + postfix)
            )
            .toList();
    }

    private void writeHeaders(CSVWriter csvWriter, List<String> extraHeaders) {
        if (extraHeaders.isEmpty()) {
            throw new InternalServerException("설문결과 CSV 다운로드 중 오류가 발생했습니다.");
        }

        csvWriter.writeNext(
            Stream.concat(DEFAULT_HEADERS.stream(), extraHeaders.stream())
                .toArray(String[]::new)
        );
    }

    private void generateCsvChunk(
        Post post, Poll poll, List<PollItem> sortedPollItems, Page<User> users, CSVWriter csvWriter
    ) {
        final Map<Long, List<PollAnswer>> answersByUserId = generateAnswersByUserId(users, poll.getId());

        users.stream().forEach(user ->
            csvWriter.writeNext(
                pollCsvRecordGenerator.toCsvRecord(
                    sortedPollItems, answersByUserId.get(user.getId()), user, post
                )
            )
        );
    }

    private Map<Long, List<PollAnswer>> generateAnswersByUserId(Page<User> users, Long pollId) {
        return users.getContent().stream()
            .collect(Collectors.toMap(
                User::getId,
                user -> pollAnswerService.getAllByPollIdAndUserId(pollId, user.getId())
            ));
    }

    private Pageable getPageable() {
        return PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"));
    }
}
