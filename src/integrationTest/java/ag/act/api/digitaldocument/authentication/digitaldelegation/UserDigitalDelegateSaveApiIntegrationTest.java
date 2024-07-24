package ag.act.api.digitaldocument.authentication.digitaldelegation;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.SlackChannel;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.exception.MarkAnyDNAException;
import ag.act.model.UserDigitalDocumentResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.List;

import static ag.act.TestUtil.createMockMultipartFile;
import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class UserDigitalDelegateSaveApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String stockCode;
    private String jwt;
    private User user;
    private Board board;
    private Stock stock;
    private Long digitalDocumentId;
    private LocalDate referenceDate;
    private List<DigitalDocumentItem> digitalDocumentItemList;
    private MockMultipartFile signImageFile;
    private MockMultipartFile idCardImageFile;
    private MockMultipartFile bankAccountFile1;
    private MockMultipartFile bankAccountFile2;

    @BeforeEach
    void setUp() {
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(user, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        final DigitalDocument digitalDocument = mockDigitalProxyDocument();
        digitalDocumentId = digitalDocument.getId();

        signImageFile = createMockMultipartFile("signImage");
        idCardImageFile = createMockMultipartFile("idCardImage");
        bankAccountFile1 = createMockMultipartFile("bankAccountImages");
        bankAccountFile2 = createMockMultipartFile("bankAccountImages");
    }

    private DigitalDocument mockDigitalProxyDocument() {
        User acceptUser = itUtil.createAcceptorUser();
        User adminUser = itUtil.createAdminUser();
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        final Post post = itUtil.createPost(board, adminUser.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        digitalDocument.setStockReferenceDate(referenceDate);

        digitalDocument.setJsonAttachOption(new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.REQUIRED.name())
            .signImage(AttachOptionType.REQUIRED.name())
            .bankAccountImage(AttachOptionType.REQUIRED.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name()));

        digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private UserDigitalDocumentResponse getPostDetailsDataResponse() throws Exception {
        final MvcResult response = callApi(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            UserDigitalDocumentResponse.class
        );
    }

    private void assertDocumentResponse(UserDigitalDocumentResponse result) {

        final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocumentId);

        final DigitalDocumentUser digitalDocumentUser =
            itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                .orElseThrow();

        assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
        assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
        assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
        assertThat(result.getUser().getId(), is(user.getId()));
        assertThat(result.getStock().getCode(), is(stock.getCode()));
        assertThat(digitalDocumentUser.getPurchasePrice(), is(0L));

        then(markAnyDNAClient).should().makeDna(any());
    }

    private String genAnswerData() {
        String answers = "";
        for (int k = 0; k < digitalDocumentItemList.size(); k++) {
            DigitalDocumentItem item = digitalDocumentItemList.get(k);
            if (item.getDefaultSelectValue() == null) {
                continue;
            }
            if (k > 1) {
                answers += ",";
            }
            answers += item.getId() + ":" + someEnum(DigitalAnswerType.class).name();
        }
        return answers;
    }

    @NotNull
    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                multipart(TARGET_API, digitalDocumentId)
                    .file(signImageFile)
                    .file(idCardImageFile)
                    .file(bankAccountFile1)
                    .file(bankAccountFile2)
                    .param("answerData", genAnswerData())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(matcher)
            .andReturn();
    }

    @Nested
    class WhenUserHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStockOnReferenceDate(stockCode, user.getId(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }
    }

    @Nested
    class WhenUserCurrentlyHasUserHoldingStock {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, user);
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

    @Nested
    class WhenErrorOccursInMarkAnyServer {

        private String errorMessage;
        private MarkAnyDNAException markAnyDnaException;
        private static final int ISSUED_NUMBER = 1;

        @BeforeEach
        void setUp() {
            errorMessage = someAlphanumericString(11);

            markAnyDnaException = new MarkAnyDNAException(errorMessage);
            digitalDocumentId = mockDigitalProxyDocument().getId();
            itUtil.createUserHoldingStock(stockCode, user);

            given(markAnyDNAClient.makeDna(any(byte[].class))).willThrow(markAnyDnaException);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API + " but sending a slack message")
        @Test
        void shouldReturnSuccessButSendSlackMessage() throws Exception {
            final UserDigitalDocumentResponse postDetailsDataResponse = getPostDetailsDataResponse();

            assertDocumentResponse(postDetailsDataResponse);

            assertSlackMessageSender();
        }

        private void assertSlackMessageSender() {
            then(slackMessageSender).should().sendSlackMessage(
                "[MarkAnyDNA] docId:(%s) userId:(%s) issuedNumber:(%s) errorMessage:%s %s".formatted(
                    digitalDocumentId,
                    user.getId(),
                    ISSUED_NUMBER,
                    markAnyDnaException.getClass().getSimpleName(),
                    errorMessage
                ),
                SlackChannel.ACT_MARKANY_ALERT
            );
        }
    }
}
