package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.DigitalDocumentUserAnswerDto;
import ag.act.entity.Board;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.UserVerificationHistory;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("VariableDeclarationUsageDistance")
class UserUpdateDigitalDocumentUserDataApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private Board board;
    private User currentUser;
    private User acceptUser;
    private User postWriteUser;
    private Post post;
    private DigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private List<DigitalDocumentItem> digitalDocumentItemList;
    private List<DigitalDocumentItemUserAnswer> digitalDocumentItemUserAnswerList;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        currentUser = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());
        stock = itUtil.createStock();

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
        itUtil.createAllUserVerificationHistories(currentUser);
    }

    @Nested
    class WhenErrorTargetEndDate {

        @BeforeEach
        void setUp() {
            LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

            final LocalDateTime targetEndDate = LocalDateTime.now();
            final LocalDateTime targetStartDate = targetEndDate.minusDays(3);

            final String pdfPath = someString(10);

            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);

            itUtil.createDigitalDocumentUser(digitalDocument, currentUser, stock, pdfPath);
            digitalDocumentItemUserAnswerList = itUtil.createDigitalDocumentItemUserAnswerList(currentUser.getId(), digitalDocumentItemList);

            post.setDigitalDocument(digitalDocument);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "이미 위임기간이 종료되어 제출하실 수 없습니다.");
        }
    }

    @Nested
    class WhenErrorPdfPath {

        @BeforeEach
        void setUp() {
            LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

            final LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetStartDate = now.minusDays(3);
            LocalDateTime targetEndDate = now.plusDays(1);

            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            digitalDocument.setDigitalDocumentItemList(digitalDocumentItemList);

            itUtil.createDigitalDocumentUser(digitalDocument, currentUser, stock);
            digitalDocumentItemUserAnswerList = itUtil.createDigitalDocumentItemUserAnswerList(currentUser.getId(), digitalDocumentItemList);

            post.setDigitalDocument(digitalDocument);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(
                response,
                400,
                "전자문서의 PDF 파일 생성전에는 제출하실 수 없습니다."
            );
        }
    }

    @Nested
    class WhenUpdateStatus {
        private MyDataSummary myDataSummary;
        private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate;

        @BeforeEach
        void setUp() {
            final LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
            final LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetStartDate = now.minusDays(3);
            LocalDateTime targetEndDate = now.plusDays(1);
            final String pdfPath = someString(10);

            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());

            itUtil.createStockAcceptorUserHistory(stock.getCode(), acceptUser);

            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            digitalDocument.setDigitalDocumentItemList(digitalDocumentItemList);

            digitalDocumentUser = createDigitalDocumentUser(pdfPath);
            digitalDocumentItemUserAnswerList = itUtil.createDigitalDocumentItemUserAnswerList(currentUser.getId(), digitalDocumentItemList);

            post.setDigitalDocument(digitalDocument);

            myDataSummary = itUtil.createMyDataSummary(currentUser, stock.getCode(), referenceDate);
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), currentUser.getId(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = itUtil.getResult(response, SimpleStringResponse.class);

            final DigitalDocument selectDigitalDocument = itUtil.findByDigitalDocument(digitalDocument.getId()).orElseThrow();
            final DigitalDocumentUser selectDigitalDocumentUser = itUtil.findDigitalDocumentUserById(digitalDocumentUser.getId()).orElseThrow();
            final List<DigitalDocumentUserAnswerDto> selectAnswerList = itUtil.findUserAnswerList(digitalDocument.getId(), currentUser.getId());

            assertThat(result.getStatus(), is("ok"));
            assertThat(digitalDocument.getId(), is(selectDigitalDocument.getId()));
            assertThat(selectDigitalDocument.getShareholdingRatio().floatValue(), is(getShareholdingRatio(selectDigitalDocument)));
            assertThat(digitalDocumentUser.getId(), is(selectDigitalDocumentUser.getId()));
            assertThat(digitalDocumentItemUserAnswerList.size(), is(selectAnswerList.size()));
            assertThat(digitalDocument.getJoinUserCount() + 1, is(selectDigitalDocument.getJoinUserCount()));
            assertThat(
                digitalDocument.getJoinStockSum() + userHoldingStockOnReferenceDate.getQuantity(),
                is(selectDigitalDocument.getJoinStockSum())
            );
            assertThat(myDataSummary.getLoanPrice(), is(selectDigitalDocumentUser.getLoanPrice()));

            final UserVerificationHistory userVerificationHistory = itUtil.findFirstVerificationHistoryRepository(currentUser.getId());
            assertThat(userVerificationHistory.getVerificationType(), is(VerificationType.SIGNATURE));
            assertThat(userVerificationHistory.getOperationType(), is(VerificationOperationType.SIGNATURE));
            assertThat(userVerificationHistory.getUserIp(), is("127.0.0.1"));
            assertThat(userVerificationHistory.getDigitalDocumentUserId(), is(digitalDocumentUser.getId()));

            final String yyMMdd = KoreanDateTimeUtil.getTodayLocalDate().format(DateTimeFormatUtil.yyMMdd());
            final Long issuedNumber = selectDigitalDocumentUser.getIssuedNumber();
            assertThat(
                digitalDocument.getType().generateDocumentNo(digitalDocument.getId(), yyMMdd, issuedNumber),
                is(selectDigitalDocument.getType().generateDocumentNo(selectDigitalDocument.getId(), yyMMdd, issuedNumber))
            );
        }

        private float getShareholdingRatio(DigitalDocument selectDigitalDocument) {
            final double ratio = selectDigitalDocument.getJoinStockSum() * 100.0 / stock.getTotalIssuedQuantity();
            return ((Double) DecimalFormatUtil.twoDecimalPlaceDouble(ratio)).floatValue();
        }
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, digitalDocument.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private DigitalDocumentUser createDigitalDocumentUser(String pdfPath) {
        DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, currentUser, stock, pdfPath);
        digitalDocumentUser.setOriginalPageCount(someLongBetween(1L, 10L));
        digitalDocumentUser.setAttachmentPageCount(someLongBetween(1L, 10L));

        final Long documentUserId = digitalDocumentUser.getId();
        itUtil.createUserVerificationHistory(currentUser, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE_SAVE, documentUserId);
        itUtil.createUserVerificationHistory(currentUser, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, documentUserId);

        return itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }
}
