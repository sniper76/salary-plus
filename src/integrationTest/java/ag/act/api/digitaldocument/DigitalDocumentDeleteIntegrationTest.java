package ag.act.api.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
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
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DigitalDocumentDeleteIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";
    private static final int _2_TIMES = 2;
    private static final int _3_TIMES = 3;
    private String jwt;
    private User user;
    private Board board;
    private Stock stock;
    private Long digitalDocumentId;
    private LocalDate referenceDate;
    private String pdfPath;
    private DigitalDocument digitalDocument;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        final String stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        itUtil.createUserHoldingStock(stockCode, user);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.ETC);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(user, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        digitalDocument = mockDigitalProxyDocument();
        mockDigitalProxyDocumentUser(digitalDocument);
        digitalDocumentId = digitalDocument.getId();
        given(itUtil.getS3ServiceMockBean().removeObjectInRetry(pdfPath)).willReturn(true);
    }

    private DigitalDocument mockDigitalProxyDocument() {
        User acceptUser = itUtil.createAcceptorUser();
        User adminUser = itUtil.createAdminUser();
        final Post post = itUtil.createPost(board, adminUser.getId());

        digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        digitalDocument.setStockReferenceDate(referenceDate);

        digitalDocument.setJsonAttachOption(new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.OPTIONAL.name())
            .signImage(AttachOptionType.OPTIONAL.name())
            .bankAccountImage(AttachOptionType.OPTIONAL.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name()));

        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private DigitalDocumentUser mockDigitalProxyDocumentUser(DigitalDocument digitalDocument) {
        pdfPath = someString(20);
        return itUtil.createDigitalDocumentUser(digitalDocument, user, stock, pdfPath, DigitalDocumentAnswerStatus.SAVE);
    }

    @Nested
    class WhenDeleteDigitalDocument {

        @Nested
        class WhenDigitalDocumentDeleteAtOnce {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                SimpleStringResponse response = getDataResponse(mvcResult);
                assertThat(response.getStatus(), is("ok"));
                assertCallS3(_2_TIMES);
            }
        }

        @Nested
        class WhenDigitalDocumentDeleteWithRetry {

            @BeforeEach
            void setUp() {
                given(itUtil.getS3ServiceMockBean().removeObjectInRetry(anyString()))
                    .willReturn(true)
                    .willReturn(false)
                    .willReturn(true);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                SimpleStringResponse response = getDataResponse(mvcResult);
                assertThat(response.getStatus(), is("ok"));
                assertCallS3(_3_TIMES);
            }
        }
    }

    private void assertCallS3(int wantedNumberOfInvocations) {
        then(itUtil.getS3ServiceMockBean())
            .should(times((wantedNumberOfInvocations)))
            .removeObjectInRetry(anyString());
    }

    private ag.act.model.SimpleStringResponse getDataResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    @NotNull
    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, digitalDocumentId)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(matcher)
            .andReturn();
    }
}
