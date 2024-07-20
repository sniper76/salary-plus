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
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.model.JsonAttachOption;
import ag.act.model.SimpleStringResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;

import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UserDigitalDelegateCompleteIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String stockCode;
    private String jwt;
    private User currentUser;
    private Board board;
    private Stock stock;
    private Long digitalDocumentId;
    private LocalDate referenceDate;
    private List<DigitalDocumentItem> digitalDocumentItemList;
    private String pdfPath;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate;

    @BeforeEach
    void setUp() {
        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(currentUser, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);
        itUtil.createAllUserVerificationHistories(currentUser);

        final DigitalDocument digitalDocument = mockDigitalProxyDocument();
        mockDigitalProxyDocumentItemList(digitalDocument);
        digitalDocumentId = digitalDocument.getId();
    }

    private DigitalDocument mockDigitalProxyDocument() {
        User acceptUser = itUtil.createAcceptorUser();
        itUtil.createStockAcceptorUserHistory(stock.getCode(), acceptUser);
        User adminUser = itUtil.createAdminUser();
        final Post post = itUtil.createPost(board, adminUser.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        digitalDocument.setStockReferenceDate(referenceDate);

        digitalDocument.setJsonAttachOption(new JsonAttachOption()
            .idCardImage(AttachOptionType.OPTIONAL.name())
            .signImage(AttachOptionType.OPTIONAL.name())
            .bankAccountImage(AttachOptionType.OPTIONAL.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name()));

        digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private DigitalDocument mockDigitalProxyDocumentItemList(DigitalDocument digitalDocument) {
        pdfPath = someString(20);
        mockDigitalDocumentUser(digitalDocument);

        itUtil.createDigitalDocumentItemUserAnswerList(currentUser.getId(), digitalDocumentItemList);
        return digitalDocument;
    }

    @NotNull
    private DigitalDocumentUser mockDigitalDocumentUser(DigitalDocument digitalDocument) {
        final DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(
            digitalDocument,
            currentUser,
            stock,
            pdfPath,
            DigitalDocumentAnswerStatus.SAVE
        );
        digitalDocumentUser.setOriginalPageCount(someLongBetween(1L, 10L));
        digitalDocumentUser.setAttachmentPageCount(someLongBetween(1L, 10L));

        final Long documentUserId = digitalDocumentUser.getId();
        itUtil.createUserVerificationHistory(currentUser, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE_SAVE, documentUserId);
        itUtil.createUserVerificationHistory(currentUser, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, documentUserId);

        return itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }

    private SimpleStringResponse getPostDetailsDataResponse() throws Exception {
        final MvcResult response = callApi(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private void assertDocumentResponse(SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));

        final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocumentId);

        final DigitalDocumentUser digitalDocumentUserFromDatabase =
            itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), currentUser.getId())
                .orElseThrow();

        assertThat(digitalDocumentUserFromDatabase.getPdfPath(), is(pdfPath));
        assertThat(digitalDocumentFromDatabase.getJoinStockSum(), is(userHoldingStockOnReferenceDate.getQuantity()));
    }

    @NotNull
    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, digitalDocumentId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    @Nested
    class WhenUserHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stockCode, currentUser.getId(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }
    }

    @Nested
    class WhenUserCurrentlyHasUserHoldingStockWithHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, currentUser);
            userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(stockCode, currentUser.getId(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }
    }

    @Nested
    class WhenUserDoesNotHaveUserHoldingStockOnReference {

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isForbidden());

            itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
        }
    }
}
