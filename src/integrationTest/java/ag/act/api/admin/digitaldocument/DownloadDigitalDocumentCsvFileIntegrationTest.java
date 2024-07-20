package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DownloadDigitalDocumentCsvFileIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/digital-document/{digitalDocumentId}/csv-download";

    private Stock stock;
    private User acceptLoginUser;
    private LocalDate referenceDate;
    private Long digitalDocumentId;
    private User user1;
    private User user2;
    private User user3;
    private DigitalDocumentUser digitalDocumentUser1;
    private DigitalDocumentUser digitalDocumentUser2;
    private DigitalDocumentUser digitalDocumentUser3;
    private List<DigitalDocumentItemUserAnswer> userAnswerList1;
    private List<DigitalDocumentItemUserAnswer> userAnswerList2;
    private List<DigitalDocumentItemUserAnswer> userAnswerList3;
    private User adminLoginUser;

    @BeforeEach
    void setUp() {
        itUtil.init();

        user1 = itUtil.createUserWithAddress();
        user2 = itUtil.createUserWithAddress();
        user3 = itUtil.createUserWithAddress();
        stock = itUtil.createStock();
        adminLoginUser = itUtil.createAdminUser();
        acceptLoginUser = createAcceptUserAsLeader();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final DigitalDocument digitalDocument = createDigitalDocument();
        digitalDocumentId = digitalDocument.getId();
        final List<DigitalDocumentItem> digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);

        digitalDocumentUser1 = createDigitalDocumentUserAndAnswer(user1, digitalDocument);
        digitalDocumentUser2 = createDigitalDocumentUserAndAnswer(user2, digitalDocument);
        digitalDocumentUser3 = createDigitalDocumentUserAndAnswer(user3, digitalDocument);

        itUtil.createMyDataSummary(user1, stock.getCode(), referenceDate);
        itUtil.createMyDataSummary(user2, stock.getCode(), referenceDate);
        itUtil.createMyDataSummary(user3, stock.getCode(), referenceDate);

        userAnswerList1 = itUtil.createDigitalDocumentItemUserAnswerList(user1.getId(), digitalDocumentItemList);
        userAnswerList2 = itUtil.createDigitalDocumentItemUserAnswerList(user2.getId(), digitalDocumentItemList);
        userAnswerList3 = itUtil.createDigitalDocumentItemUserAnswerList(user3.getId(), digitalDocumentItemList);
    }

    private DigitalDocumentUser createDigitalDocumentUserAndAnswer(User user, DigitalDocument document) {
        final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        itUtil.createUserHoldingStock(stock.getCode(), user);
        return itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
    }

    private DigitalDocument createDigitalDocument() {
        final User postWriteUser = itUtil.createAdminUser();
        final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(3);
        final LocalDateTime targetEndDate = LocalDateTime.now().plusDays(1);

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final Post post = itUtil.createPost(board, postWriteUser.getId());
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptLoginUser, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
        );
        post.setDigitalDocument(digitalDocument);

        return digitalDocument;
    }

    private User createAcceptUserAsLeader() {
        final User acceptUser = itUtil.createAcceptorUser();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());

        return acceptUser;
    }

    @Nested
    class WhenUserIsAdmin {

        private String jwt;

        @BeforeEach
        void setUp() {
            jwt = itUtil.createJwt(adminLoginUser.getId());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldDownloadAllData() throws Exception {
            assertCsvResponse(callApi(jwt));
        }

        private void assertCsvResponse(MvcResult response) throws UnsupportedEncodingException {

            final String actual = response.getResponse().getContentAsString();

            final String expectedResult1 = itUtil.getExpectedCsvRecord(user1, digitalDocumentUser1, userAnswerList1);
            final String expectedResult2 = itUtil.getExpectedCsvRecord(user2, digitalDocumentUser2, userAnswerList2);
            final String expectedResult3 = itUtil.getExpectedCsvRecord(user3, digitalDocumentUser3, userAnswerList3);

            final String expectedResult = """
                번호,이름,생년월일,성별,주소,상세주소,우편번호,전화번호,주식명,주식수,평균매수가,차입금,작성일시,가입일자,제1-1안,제1-2안
                %s
                %s
                %s
                """.formatted(expectedResult1, expectedResult2, expectedResult3);

            assertThat(actual, is(expectedResult));
        }
    }

    @Nested
    class WhenUserIsAcceptor {

        private String jwt;

        @BeforeEach
        void setUp() {
            jwt = itUtil.createJwt(acceptLoginUser.getId());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldDownloadAllData() throws Exception {
            assertCsvResponse(callApi(jwt));
        }

        private void assertCsvResponse(MvcResult response) throws UnsupportedEncodingException {

            final String actual = response.getResponse().getContentAsString();

            final String expectedResult1 = itUtil.getExpectedCsvRecordForAcceptor(user1, digitalDocumentUser1, userAnswerList1);
            final String expectedResult2 = itUtil.getExpectedCsvRecordForAcceptor(user2, digitalDocumentUser2, userAnswerList2);
            final String expectedResult3 = itUtil.getExpectedCsvRecordForAcceptor(user3, digitalDocumentUser3, userAnswerList3);

            final String expectedResult = """
                번호,이름,생년월일,성별,주식명,주식수,작성일시,제1-1안,제1-2안
                %s
                %s
                %s
                """.formatted(expectedResult1, expectedResult2, expectedResult3);

            assertThat(actual, is(expectedResult));
        }
    }

    private MvcResult callApi(String jwt) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, digitalDocumentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}
