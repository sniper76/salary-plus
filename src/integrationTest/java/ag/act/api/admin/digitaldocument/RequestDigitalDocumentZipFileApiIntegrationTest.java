package ag.act.api.admin.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class RequestDigitalDocumentZipFileApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/digital-document/{digitalDocumentId}/zip-file-request?isSecured={isSecured}";

    private String jwt;
    private User user;
    private Long digitalDocumentId;
    private Post post;
    private Stock stock;
    private Boolean isSecured = Boolean.TRUE;
    @Captor
    private ArgumentCaptor<String> lambdaRequestBodyCaptor;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());

        // S3 람다는 Mocking 처리한다.
        willDoNothing().given(lambdaService).invokeLambdaAsync(anyString(), anyString());
    }

    private DigitalDocument mockDigitalProxyDocument(LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        stock = itUtil.createStock(someStockCode());
        final Board board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();

        return itUtil.createDigitalDocument(
            post,
            stock,
            acceptUser,
            DigitalDocumentType.DIGITAL_PROXY,
            targetStartDate,
            targetEndDate,
            LocalDate.now().minusDays(10)
        );
    }

    @Nested
    class WhenZipFileRequestIsSuccess {
        private DigitalDocument digitalDocument;

        @Nested
        class WhenDigitalDocumentFinished {

            @BeforeEach
            void setUp() {
                isSecured = Boolean.TRUE;
                digitalDocument = mockDigitalProxyDocument(
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().minusDays(1)
                );
                digitalDocumentId = digitalDocument.getId();

                itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), false);
                itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), true);
            }

            @Nested
            class AndAllUserResponseCompleted {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final ag.act.model.SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertDigitalDocumentDownloadInDatabase();
                    assertLambdaRequest(isSecured, getZipFileName(false));
                }
            }

            @Nested
            class AndIsNotSecured {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    isSecured = Boolean.FALSE;
                    final ag.act.model.SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertDigitalDocumentDownloadInDatabase();
                    final String zipFileName = getZipFileName(false);
                    assertLambdaRequest(isSecured, zipFileName);
                }
            }

            @Nested
            class AndSomeUnfinishedUserDigitalDocumentExist {

                private DigitalDocumentUser digitalDocumentUser;

                @BeforeEach
                void setUp() {
                    digitalDocumentUser = itUtil.createDigitalDocumentUser(
                        digitalDocument, user, stock, someString(10), DigitalDocumentAnswerStatus.SAVE);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final ag.act.model.SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertDigitalDocumentDownloadInDatabase();
                    assertUnfinishedDigitalDocumentUsersNotExistFromDatabase();
                    final String zipFileName = getZipFileName(false);
                    assertLambdaRequest(isSecured, zipFileName);
                }

                private void assertUnfinishedDigitalDocumentUsersNotExistFromDatabase() {
                    assertThat(existsDigitalDocumentUserInDatabase(digitalDocumentUser.getId()), is(false));
                }
            }
        }
    }

    @Nested
    class WhenDigitalDocumentNotFinished {

        private DigitalDocumentUser digitalDocumentUser;

        @BeforeEach
        void setUp() {
            final DigitalDocument digitalDocument = mockDigitalProxyDocument(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(1)
            );
            digitalDocumentId = digitalDocument.getId();

            itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), false);
            itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), true);

            digitalDocumentUser = itUtil.createDigitalDocumentUser(
                digitalDocument, user, stock, someString(10), DigitalDocumentAnswerStatus.SAVE);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

            assertThat(result.getStatus(), is("ok"));
            assertDigitalDocumentDownloadInDatabase();
            assertUnfinishedDigitalDocumentUserExistInDatabase();
            assertLambdaRequest(isSecured, getZipFileName(true));
        }

        private void assertUnfinishedDigitalDocumentUserExistInDatabase() {
            assertThat(existsDigitalDocumentUserInDatabase(digitalDocumentUser.getId()), is(true));
        }
    }

    private String getZipFileName(boolean isTemp) {
        final String yyMMdd = KoreanDateTimeUtil.getFormattedCurrentDateTime("yyMMdd");
        return "%s_%s_%s.zip".formatted(stock.getName(), post.getTitle() + (isTemp ? "_temp" : ""), yyMMdd);
    }

    private boolean existsDigitalDocumentUserInDatabase(Long digitalDocumentUserId) {
        return itUtil.findDigitalDocumentUserById(digitalDocumentUserId).isPresent();
    }

    private void assertLambdaRequest(Boolean isSecured, String zipFileName) {
        then(lambdaService).should().invokeLambdaAsync(anyString(), lambdaRequestBodyCaptor.capture());
        final String lambdaRequest = lambdaRequestBodyCaptor.getValue();

        assertThat(lambdaRequest,
            containsString("\"destinationDirectory\":\"contents/digitaldocument/%s/destination\"".formatted(digitalDocumentId)));
        assertThat(lambdaRequest, containsString("\"zipFilename\":\"%s\"".formatted(zipFileName)));
        assertThat(lambdaRequest, containsString("\"fileType\":\"DIGITAL_DOCUMENT\""));

        if (isSecured) {
            assertThat(lambdaRequest, containsString("\"password\":\"%s\"".formatted(stock.getCode())));
        } else {
            assertThat(lambdaRequest, not(containsString("\"password\"")));
        }
    }

    private void assertDigitalDocumentDownloadInDatabase() {

        final List<DigitalDocumentDownload> digitalDocumentDownloadList = itUtil.findAllDigitalDocumentDownload(digitalDocumentId);

        assertThat(digitalDocumentDownloadList.size(), is(3));

        assertThat(digitalDocumentDownloadList.get(0).getDigitalDocumentId(), is(digitalDocumentId));
        assertThat(digitalDocumentDownloadList.get(0).getRequestUserId(), is(user.getId()));
        assertThat(digitalDocumentDownloadList.get(0).getIsLatest(), is(false));
        assertThat(digitalDocumentDownloadList.get(0).getZipFileStatus(), is(ZipFileStatus.REQUEST));
        assertThat(digitalDocumentDownloadList.get(0).getDownloadCount(), is(0));

        assertThat(digitalDocumentDownloadList.get(1).getDigitalDocumentId(), is(digitalDocumentId));
        assertThat(digitalDocumentDownloadList.get(1).getRequestUserId(), is(user.getId()));
        assertThat(digitalDocumentDownloadList.get(1).getIsLatest(), is(false));
        assertThat(digitalDocumentDownloadList.get(1).getZipFileStatus(), is(ZipFileStatus.REQUEST));
        assertThat(digitalDocumentDownloadList.get(1).getDownloadCount(), is(0));

        assertThat(digitalDocumentDownloadList.get(2).getDigitalDocumentId(), is(digitalDocumentId));
        assertThat(digitalDocumentDownloadList.get(2).getRequestUserId(), is(user.getId()));
        assertThat(digitalDocumentDownloadList.get(2).getIsLatest(), is(true));
        assertThat(digitalDocumentDownloadList.get(2).getZipFileStatus(), is(ZipFileStatus.IN_PROGRESS));
        assertThat(digitalDocumentDownloadList.get(2).getDownloadCount(), is(0));

        final DigitalDocumentDownload actual = digitalDocumentDownloadList.get(2);
        final DigitalDocumentDownload expected = itUtil.findDigitalDocument(digitalDocumentId).getLatestDigitalDocumentDownload()
            .orElseThrow();
        assertThat(actual.getId(), equalTo(expected.getId()));
        assertThat(actual.getDigitalDocumentId(), equalTo(expected.getDigitalDocumentId()));
        assertThat(actual.getRequestUserId(), equalTo(expected.getRequestUserId()));
        assertThat(actual.getIsLatest(), equalTo(expected.getIsLatest()));
        assertThat(actual.getZipFileStatus(), equalTo(expected.getZipFileStatus()));
        assertThat(actual.getDownloadCount(), equalTo(expected.getDownloadCount()));

    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, digitalDocumentId, isSecured)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
