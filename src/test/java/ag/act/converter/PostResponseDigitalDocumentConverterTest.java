package ag.act.converter;

import ag.act.converter.digitaldocument.DigitalDocumentAcceptUserResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentDownloadResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentJsonAttachOptionConverter;
import ag.act.converter.digitaldocument.DigitalDocumentStockResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentUserResponseConverter;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someCompanyRegistrationNumber;
import static ag.act.TestUtil.someLocalDateTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostResponseDigitalDocumentConverterTest {
    @InjectMocks
    private PostResponseDigitalDocumentConverter converter;
    @Mock
    private DigitalDocumentStockResponseConverter digitalDocumentStockResponseConverter;
    @Mock
    private DigitalDocumentUserResponseConverter digitalDocumentUserResponseConverter;
    @Mock
    private DigitalDocumentAcceptUserResponseConverter digitalDocumentAcceptUserResponseConverter;
    @Mock
    private ag.act.model.DigitalDocumentStockResponse digitalDocumentStockResponse;
    @Mock
    private ag.act.model.DigitalDocumentUserResponse digitalDocumentUserResponse;
    @Mock
    private ag.act.model.DigitalDocumentAcceptUserResponse digitalDocumentAcceptUserResponse;
    @Mock
    private DigitalDocumentDownloadResponseConverter digitalDocumentDownloadResponseConverter;
    @Mock
    private DigitalDocumentJsonAttachOptionConverter digitalDocumentJsonAttachOptionConverter;

    @Nested
    class ConvertWithRawData {

        @Mock
        private DigitalDocumentUser digitalDocumentUser;
        @Mock
        private Stock stock;
        @Mock
        private User user;
        @Mock
        private User solidarityLeader;
        private DigitalDocument digitalDocument;
        private DigitalDocumentAnswerStatus answerStatus;
        private LocalDate stockReferenceDate;
        private Long stockCount;
        private UserDigitalDocumentResponse actualResponse;
        private Double shareholdingRatio;

        @BeforeEach
        void setUp() {
            answerStatus = someEnum(DigitalDocumentAnswerStatus.class);
            stockReferenceDate = KoreanDateTimeUtil.getTodayLocalDate();
            stockCount = someLong();
            shareholdingRatio = someLongBetween(0L, 100L) * 1.0;
            digitalDocument = stubDigitalDocument();

            given(digitalDocumentUser.getDigitalDocumentAnswerStatus()).willReturn(answerStatus);
            given(digitalDocumentUser.getStockCount()).willReturn(stockCount);

            given(digitalDocumentStockResponseConverter.convert(stock, digitalDocument, stockCount))
                .willReturn(digitalDocumentStockResponse);
            given(digitalDocumentUserResponseConverter.convert(user, digitalDocument.getType().isNeedAddress()))
                .willReturn(digitalDocumentUserResponse);
            given(digitalDocumentAcceptUserResponseConverter.convert(solidarityLeader))
                .willReturn(digitalDocumentAcceptUserResponse);

            actualResponse = converter.convert(digitalDocument, digitalDocumentUser, stock, user, solidarityLeader);
        }

        @Test
        void shouldReturnCorrectResponse() {
            assertThat(actualResponse.getId(), is(digitalDocument.getId()));
            assertThat(actualResponse.getDigitalDocumentType(), is(digitalDocument.getType().name()));
            assertThat(actualResponse.getJoinUserCount(), is(digitalDocument.getJoinUserCount()));
            assertThat(actualResponse.getShareholdingRatio(), is(shareholdingRatio.floatValue()));
            assertThat(actualResponse.getTargetStartDate(), is(DateTimeConverter.convert(digitalDocument.getTargetStartDate())));
            assertThat(actualResponse.getTargetEndDate(), is(DateTimeConverter.convert(digitalDocument.getTargetEndDate())));
            assertThat(actualResponse.getAnswerStatus(), is(answerStatus.name()));
            assertThat(actualResponse.getStock(), is(digitalDocumentStockResponse));
            assertThat(actualResponse.getUser(), is(digitalDocumentUserResponse));
            assertThat(actualResponse.getAcceptUser(), is(digitalDocumentAcceptUserResponse));
            assertThat(actualResponse.getItems(), is(List.of()));
        }

        @Test
        void shouldCallDigitalDocumentStockResponseConverter() {
            then(digitalDocumentStockResponseConverter).should().convert(stock, digitalDocument, stockCount);
        }

        @Test
        void shouldCallDigitalDocumentUserResponseConverter() {
            then(digitalDocumentUserResponseConverter).should().convert(user, digitalDocument.getType().isNeedAddress());
        }

        @Test
        void shouldCallDigitalDocumentAcceptUserResponseConverter() {
            then(digitalDocumentAcceptUserResponseConverter).should().convert(solidarityLeader);
        }

        private DigitalDocument stubDigitalDocument() {
            DigitalDocument digitalDocument = new DigitalDocument();

            final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();
            final LocalDateTime targetEndDate = targetStartDate.plusDays(someIntegerBetween(1, 20));

            digitalDocument.setPostId(someLong());
            digitalDocument.setStockCode(stock.getCode());
            digitalDocument.setStockReferenceDate(someLocalDateTime().toLocalDate());
            digitalDocument.setTitle(someAlphanumericString(10));
            digitalDocument.setContent(someAlphanumericString(10));
            digitalDocument.setCompanyName(someAlphanumericString(10));
            digitalDocument.setCompanyName(someAlphanumericString(10));
            digitalDocument.setCompanyRegistrationNumber(someCompanyRegistrationNumber());
            digitalDocument.setShareholderMeetingType(someAlphanumericString(10));
            digitalDocument.setShareholderMeetingName(someAlphanumericString(10));
            digitalDocument.setShareholderMeetingDate(LocalDateTime.now());
            digitalDocument.setDesignatedAgentNames(someAlphanumericString(10));
            digitalDocument.setJoinStockSum(someLongBetween(0L, 100L));
            digitalDocument.setJoinUserCount(someIntegerBetween(0, 100));
            digitalDocument.setType(someEnum(DigitalDocumentType.class));
            digitalDocument.setStatus(ag.act.model.Status.ACTIVE);
            digitalDocument.setTargetStartDate(targetStartDate);
            digitalDocument.setTargetEndDate(targetEndDate);
            digitalDocument.setShareholdingRatio(shareholdingRatio);
            digitalDocument.setStockReferenceDate(stockReferenceDate);

            return digitalDocument;
        }
    }
}
