package ag.act.api.digitaldocument.authentication.digitaldelegation;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
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

import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UserDigitalDocumentDownloadIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}/download-document";

    private String stockCode;
    private String jwt;
    private User user;
    private Stock stock;
    private Long digitalDocumentId;
    private LocalDate referenceDate;
    private List<DigitalDocumentItem> digitalDocumentItemList;
    private String pdfPath;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate;
    private DigitalDocument digitalDocument;
    private Long stockQuantity;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        stockCode = someStockCode();
        stockQuantity = someLongBetween(100L, 1000L);
        stock = itUtil.createStock(stockCode);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(user, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);
    }

    @Nested
    class WhenUserHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stockCode, user.getId(), referenceDate);
            userHoldingStockOnReferenceDate.setQuantity(stockQuantity);
            itUtil.updateUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDate);

            digitalDocument = mockDigitalProxyDocument();
            mockDigitalProxyDocumentItemList(digitalDocument);
            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            callApi(status().isOk());
            assertDocumentResponse();
        }
    }

    @Nested
    class WhenUserCurrentlyHasUserHoldingStockWithHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, user);
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stockCode, user.getId(), referenceDate);
            userHoldingStockOnReferenceDate.setQuantity(stockQuantity);
            itUtil.updateUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDate);

            digitalDocument = mockDigitalProxyDocument();
            mockDigitalProxyDocumentItemList(digitalDocument);
            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            callApi(status().isOk());
            assertDocumentResponse();
        }
    }

    @Nested
    class WhenUserDoesNotHaveUserHoldingStockOnReference {

        @BeforeEach
        void setUp() {
            digitalDocument = mockDigitalProxyDocument();
            mockDigitalProxyDocumentItemList(digitalDocument);
            digitalDocumentId = digitalDocument.getId();

        }

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isForbidden());

            itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
        }
    }

    @Nested
    class WhenDigitalDocumentIsHolderListReadAndCopy {

        @BeforeEach
        void setUp() {
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stockCode, user.getId(), referenceDate);
            userHoldingStockOnReferenceDate.setQuantity(stockQuantity);
            itUtil.updateUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDate);

            digitalDocument = createHolderListReadAndCopyDigitalDocument();
            createHolderListReadAndCopyDigitalDocumentUser(digitalDocument);

            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            callApi(status().isOk());
            assertDocumentResponse();
        }

        private DigitalDocumentUser createHolderListReadAndCopyDigitalDocumentUser(DigitalDocument document) {
            pdfPath = "%s/%s".formatted(user.getId(), someString(10));
            itUtil.createUserHoldingStock(stock.getCode(), user);
            return itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
        }

        private DigitalDocument createHolderListReadAndCopyDigitalDocument() {
            final User postWriteUser = itUtil.createAdminUser();
            final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(3);
            final LocalDateTime targetEndDate = LocalDateTime.now().plusDays(1);

            final Board board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.HOLDER_LIST_READ_AND_COPY);
            final Post post = itUtil.createPost(board, postWriteUser.getId());
            final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
                post,
                stock,
                user,
                DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT,
                targetStartDate,
                targetEndDate,
                referenceDate
            );
            digitalDocument.setJoinStockSum(stockQuantity);
            final DigitalDocument savedDigitalDocument = itUtil.updateDigitalDocument(digitalDocument);

            post.setDigitalDocument(savedDigitalDocument);
            itUtil.updatePost(post);

            return savedDigitalDocument;
        }
    }


    private DigitalDocument mockDigitalProxyDocument() {
        final User acceptUser = itUtil.createAcceptorUser();
        final User adminUser = itUtil.createAdminUser();
        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final Post post = itUtil.createPost(board, adminUser.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        digitalDocument.setStockReferenceDate(referenceDate);
        digitalDocument.setJoinStockSum(stockQuantity);

        digitalDocument.setJsonAttachOption(new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.OPTIONAL.name())
            .signImage(AttachOptionType.OPTIONAL.name())
            .bankAccountImage(AttachOptionType.OPTIONAL.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name()));

        digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private DigitalDocument mockDigitalProxyDocumentItemList(DigitalDocument digitalDocument) {
        pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
        itUtil.createDigitalDocumentItemUserAnswerList(user.getId(), digitalDocumentItemList);
        return digitalDocument;
    }

    private void assertDocumentResponse() {
        final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocumentId);

        final DigitalDocumentUser digitalDocumentUserFromDatabase =
            itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                .orElseThrow();

        assertThat(digitalDocumentUserFromDatabase.getPdfPath(), is(pdfPath));
        assertThat(digitalDocumentFromDatabase.getJoinStockSum(), is(userHoldingStockOnReferenceDate.getQuantity()));
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, digitalDocumentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt))))
            .andExpect(resultMatcher)
            .andReturn();
    }
}
