package ag.act.service.download.csv;

import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.repository.DigitalDocumentItemRepository;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.service.digitaldocument.answer.DigitalDocumentItemUserAnswerService;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import ag.act.service.download.csv.record.AdminDigitalDocumentCsvRecordGeneratorImpl;
import ag.act.service.download.csv.record.DigitalDocumentCsvRecordGeneratorResolver;
import ag.act.service.user.UserService;
import ag.act.util.CSVWriterFactory;
import ag.act.util.DownloadFileUtil;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentCsvDownloadProcessorTest {

    @InjectMocks
    private DigitalDocumentCsvDownloadProcessor processor;
    private List<MockedStatic<?>> statics;
    @Mock
    private DigitalDocumentUserRepository digitalDocumentUserRepository;
    @Mock
    private DigitalDocumentItemRepository digitalDocumentItemRepository;
    @Mock
    private DigitalDocumentItemUserAnswerService digitalDocumentItemUserAnswerService;
    @Mock
    private DigitalDocumentCsvRecordGeneratorResolver digitalDocumentCsvRecordGeneratorResolver;
    @Mock
    private AdminDigitalDocumentCsvRecordGeneratorImpl adminDigitalDocumentCsvRecordGenerator;
    @Mock
    private CSVWriterFactory csvWriterFactory;
    @Mock
    private UserService userService;
    @Mock
    private DigitalDocument digitalDocument;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream outputStream;
    @Mock
    private CSVWriter csvWriter;
    @Mock
    private DigitalDocumentItem digitalDocumentItem;
    @Mock
    private Page<DigitalDocumentUser> digitalDocumentUserPage;
    @Mock
    private DigitalDocumentItemUserAnswer digitalDocumentItemUserAnswer;
    @Mock
    private DigitalDocumentUser digitalDocumentUser;
    private Long digitalDocumentId;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(RequestContextHolder.class), mockStatic(DownloadFileUtil.class));

        given(digitalDocumentCsvRecordGeneratorResolver.resolve()).willReturn(adminDigitalDocumentCsvRecordGenerator);
    }

    @Nested
    class DownloadCsvFile {

        private String firstItemTitle;
        private String[] firstRecord;
        private List<String> headers;

        @BeforeEach
        void setUp() throws IOException {
            digitalDocumentId = someLong();
            firstItemTitle = someString(10);
            firstRecord = new String[] {someString(50)};
            headers = List.of(
                "번호", "이름", "생년월일", "성별", "주소", "상세주소", "우편번호", "전화번호", "주식명", "주식수", "평균매수가", "차입금", "작성일시"
            );
            final Long userId = someLong();
            final List<Long> digitalDocumentIds = List.of(digitalDocumentId);
            final List<DigitalDocumentItemUserAnswer> digitalDocumentItemUserAnswerList = List.of(digitalDocumentItemUserAnswer);
            final List<DigitalDocumentUser> digitalDocumentUserContents = List.of(digitalDocumentUser);

            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(response.getOutputStream()).willReturn(outputStream);

            given(csvWriterFactory.create(outputStream)).willReturn(csvWriter);

            given(digitalDocumentItemRepository.findAllByDigitalDocumentIdAndIsLastItemTrueOrderByIdAsc(digitalDocumentId))
                .willReturn(List.of(digitalDocumentItem));
            given(digitalDocumentItem.getTitle()).willReturn(firstItemTitle);
            given(digitalDocumentUserRepository.findAllByDigitalDocumentIdInAndDigitalDocumentAnswerStatusIn(
                eq(digitalDocumentIds),
                eq(List.of(DigitalDocumentAnswerStatus.COMPLETE)),
                any(Pageable.class)
            )).willReturn(digitalDocumentUserPage);

            given(digitalDocumentItemUserAnswerService.getUserAnswersMapByUserId(digitalDocumentId, List.of(userId)))
                .willReturn(Map.of(userId, digitalDocumentItemUserAnswerList));
            given(digitalDocumentUserPage.getNumber()).willReturn(0);
            given(digitalDocumentUserPage.getSize()).willReturn(2);
            given(digitalDocumentUserPage.getContent()).willReturn(digitalDocumentUserContents);
            given(digitalDocumentUserPage.iterator()).willReturn(digitalDocumentUserContents.iterator());
            given(digitalDocumentUserPage.hasNext()).willReturn(false);
            given(digitalDocumentUserPage.isEmpty()).willReturn(true);

            given(digitalDocumentUser.getUserId()).willReturn(userId);
            given(digitalDocumentUser.getDigitalDocumentId()).willReturn(digitalDocumentId);
            given(adminDigitalDocumentCsvRecordGenerator.toCsvRecord(any(DigitalDocumentCsvRecordInputDto.class))).willReturn(firstRecord);
            given(adminDigitalDocumentCsvRecordGenerator.getHeaders())
                .willReturn(headers);

            processor.download(response, digitalDocumentIds);
        }

        @Test
        void shouldCallFindAllByDigitalDocumentIdAndIsLastItemTrueOrderByIdAsc() {
            then(digitalDocumentItemRepository).should().findAllByDigitalDocumentIdAndIsLastItemTrueOrderByIdAsc(digitalDocumentId);
        }

        @Test
        void shouldCallResponseFlushBuffer() throws IOException {
            then(response).should().flushBuffer();
        }

        @Test
        void shouldWriteHeaders() {
            then(csvWriter).should().writeNext(
                Stream.concat(headers.stream(), Stream.of(firstItemTitle)).toArray(String[]::new)
            );
        }

        @Test
        void shouldWriteFirstRecord() {
            then(csvWriter).should().writeNext(firstRecord);
        }
    }
}
