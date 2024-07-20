package ag.act.api.digitaldocument.authentication.digitaldelegation;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
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

import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UserDigitalEtcCompleteIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String stockCode;
    private String jwt;
    private User user;
    private Board board;
    private Stock stock;
    private Long digitalDocumentId;
    private LocalDate referenceDate;
    private String pdfPath;
    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.ETC);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(user, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        final DigitalDocument digitalDocument = mockDigitalEtcDocument();
        mockDigitalEtcDocumentUser(digitalDocument);
        digitalDocumentId = digitalDocument.getId();
    }

    private DigitalDocument mockDigitalEtcDocument() {
        User acceptUser = itUtil.createAcceptorUser();
        User adminUser = itUtil.createAdminUser();
        final Post post = itUtil.createPost(board, adminUser.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.ETC_DOCUMENT);
        digitalDocument.setStockReferenceDate(referenceDate);

        digitalDocument.setJsonAttachOption(new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.OPTIONAL.name())
            .signImage(AttachOptionType.OPTIONAL.name())
            .bankAccountImage(AttachOptionType.OPTIONAL.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name()));

        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private DigitalDocument mockDigitalEtcDocumentUser(DigitalDocument digitalDocument) {
        pdfPath = someString(20);
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, pdfPath, DigitalDocumentAnswerStatus.SAVE);
        return digitalDocument;
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
            itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                .orElseThrow();

        assertThat(digitalDocumentUserFromDatabase.getPdfPath(), is(pdfPath));
        assertThat(digitalDocumentFromDatabase.getJoinStockSum(), is(userHoldingStock.getQuantity()));
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
    class WhenUserCurrentlyHasUserHoldingStock {

        @BeforeEach
        void setUp() {
            userHoldingStock = itUtil.createUserHoldingStock(stockCode, user);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }
    }

    @Nested
    class WhenUserDoesNotHaveUserHoldingStock {

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isForbidden());

            itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
        }
    }
}
