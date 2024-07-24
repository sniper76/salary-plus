package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SlackChannel;
import ag.act.model.SendEmailRequest;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import static ag.act.TestUtil.someEmail;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class DigitalDocumentEmailSenderIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/digital-document/{digitalDocumentId}/email";
    private static final String successMessage = "주주명부 열람/등사 메일 발송중 오류가 발생하었습니다. id: %s";

    private String leaderJwt;
    private Stock stock;
    private Solidarity solidarity;
    private SendEmailRequest request;
    private User leaderUser;
    private Long digitalDocumentId;
    private Board board;
    private final String email = someEmail();

    @BeforeEach
    void setUp() {
        itUtil.init();
        leaderUser = itUtil.createUser();
        leaderJwt = itUtil.createJwt(leaderUser.getId());
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.HOLDER_LIST_READ_AND_COPY);
    }

    @Nested
    class WhenError {

        @Nested
        class WhenIsNotLeader {

            @BeforeEach
            void setUp() {
                request = genRequest(email);
                digitalDocumentId = somePositiveLong();
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표만 가능한 기능입니다.");
            }
        }

        @Nested
        class WhenEmailSenderError {
            private Exception exception;
            private String errorMessage;

            @BeforeEach
            void setUp() {
                errorMessage = someAlphanumericString(11);
                exception = new RuntimeException(errorMessage);

                mockDigitalDocuments();
                request = genRequest(email);

                given(sesService.sendRawEmail(any(SendRawEmailRequest.class))).willThrow(exception);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                callApi(status().isInternalServerError());

                assertSlackMessageSender();
            }

            private void assertSlackMessageSender() {
                then(slackMessageSender).should().sendSlackMessage(
                    successMessage.formatted(digitalDocumentId),
                    SlackChannel.ACT_EMAIL_ALERT
                );
            }
        }
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            mockDigitalDocuments();
            request = genRequest(email);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            itUtil.assertSimpleOkay(callApi(status().isOk()));
        }
    }

    private void mockDigitalDocuments() {
        itUtil.createSolidarityLeader(solidarity, leaderUser.getId());
        final Post post = itUtil.createPost(board, leaderUser.getId());
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, leaderUser);
        digitalDocumentId = digitalDocument.getId();
        final DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, leaderUser, stock);
        digitalDocumentUser.setPdfPath("contents/digitaldocument/%s/test.pdf".formatted(digitalDocument.getId()));
        itUtil.updateDigitalDocumentUser(digitalDocumentUser);
        itUtil.createHolderListReadAndCopyList(digitalDocumentId, "3월말");
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, digitalDocumentId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(leaderJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private SimpleStringResponse getDataResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private SendEmailRequest genRequest(String receiver) {
        return new SendEmailRequest()
            .recipientEmail(receiver);
    }
}
