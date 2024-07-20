package ag.act.service.poll.download.csv;

import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.service.download.csv.PollCsvDownloadProcessor;
import ag.act.service.download.csv.PollCsvRecordGenerator;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.user.UserService;
import ag.act.util.CSVWriterFactory;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PollCsvDownloadProcessorTest {
    private static final String PERSON_POSTFIX = "-주주수";
    private static final String STOCK_QUANTITY_POSTFIX = "-주식수";
    private static final List<String> DEFAULT_HEADERS = List.of(
        "유저아이디", "유저이름", "종목코드", "종목명", "게시물번호", "보유주식수"
    );

    @InjectMocks
    private PollCsvDownloadProcessor processor;
    @Mock
    private CSVWriterFactory csvWriterFactory;
    @Mock
    private PollCsvRecordGenerator pollCsvRecordGenerator;
    @Mock
    private PollAnswerService pollAnswerService;
    @Mock
    private UserService userService;

    @SuppressWarnings("unused")
    @Nested
    class DownloadCsvFile {
        @Mock
        private HttpServletResponse response;
        @Mock
        private ServletOutputStream outputStream;
        @Mock
        private Poll poll;
        @Mock
        private PollItem pollItem;
        @Mock
        private PollAnswer pollAnswer;
        private Long pollId;
        @Mock
        private CSVWriter csvWriter;
        @Mock
        private Post post;
        @Mock
        private User user;
        @Mock
        private Page<User> usersVotedInPoll;
        private String firstItemText;
        private String[] firstRecord;

        @BeforeEach
        void setUp() throws IOException {
            // Given
            pollId = someLong();
            Long userId = someLong();
            firstItemText = someString(10);
            firstRecord = new String[]{someString(50)};
            final List<User> usersVotedInPollContent = List.of(user);
            final List<Post> posts = List.of(post);

            given(post.getFirstPoll()).willReturn(poll);
            given(poll.getId()).willReturn(pollId);
            given(poll.getPollItemList()).willReturn(List.of(pollItem));
            given(response.getOutputStream()).willReturn(outputStream);
            given(csvWriterFactory.create(any(ByteArrayOutputStream.class))).willReturn(csvWriter);
            given(pollItem.getId()).willReturn(someLong());
            given(pollItem.getText()).willReturn(firstItemText);
            given(userService.findAllVotedInPoll(eq(pollId), any(Pageable.class))).willReturn(usersVotedInPoll);

            given(usersVotedInPoll.stream()).willReturn(Stream.of(user));
            given(usersVotedInPoll.getContent()).willReturn(usersVotedInPollContent);
            given(usersVotedInPoll.isEmpty()).willReturn(true);

            given(user.getId()).willReturn(userId);
            given(pollAnswerService.getAllByPollIdAndUserId(pollId, userId)).willReturn(List.of(pollAnswer));
            given(pollCsvRecordGenerator.toCsvRecord(List.of(pollItem), List.of(pollAnswer), user, post))
                .willReturn(firstRecord);

            // When
            processor.download(response, posts);
        }

        @Test
        void shouldWriteHeaders() {
            List<String> headers = Stream.concat(
                DEFAULT_HEADERS.stream(),
                Stream.of(firstItemText + PERSON_POSTFIX, firstItemText + STOCK_QUANTITY_POSTFIX)
            ).toList();

            then(csvWriter).should().writeNext(headers.toArray(String[]::new));
        }

        @Test
        void shouldWriteFirstRecord() {
            then(csvWriter).should().writeNext(firstRecord);
        }

        @Test
        void shouldWriteFinalCsvToOutputStream() throws IOException {
            ArgumentCaptor<ByteArrayOutputStream> tempStreamArgumentCaptor = ArgumentCaptor.forClass(ByteArrayOutputStream.class);

            then(csvWriterFactory).should().create(tempStreamArgumentCaptor.capture());
            ByteArrayOutputStream finalCsvFileOutputStream = tempStreamArgumentCaptor.getValue();
            then(outputStream).should().write(finalCsvFileOutputStream.toByteArray());
        }
    }
}
