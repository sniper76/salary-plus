package ag.act.api.admin.campaign;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Campaign;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.campaign.CampaignDownload;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeFormatUtil;
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
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class RequestCampaignZipFileApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns/{campaignId}/zip-file-request?isSecured={isSecured}";

    private String jwt;
    private User user;
    private Long campaignId;
    private Campaign campaign;
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

    private Campaign createCampaign(LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        stock = itUtil.createStock(someStockCode());

        return itUtil.createCampaign(
            someAlphanumericString(10), List.of(stock), targetStartDate, targetEndDate, LocalDate.now().minusDays(10)
        );
    }

    @Nested
    class WhenZipFileRequestIsSuccess {

        @Nested
        class WhenCampaignFinished {

            @BeforeEach
            void setUp() {
                isSecured = Boolean.TRUE;
                campaign = createCampaign(
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().minusDays(1)
                );
                campaignId = campaign.getId();

                itUtil.createCampaignDownload(campaignId, user.getId(), false);
                itUtil.createCampaignDownload(campaignId, user.getId(), true);
            }

            @Nested
            class AndAllUserResponseCompleted {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertCampaignDownloadInDatabase();
                    assertLambdaRequest(isSecured, getZipFileName(false));
                }
            }

            @Nested
            class AndIsNotSecured {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    isSecured = Boolean.FALSE;
                    final SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertCampaignDownloadInDatabase();
                    assertLambdaRequest(isSecured, getZipFileName(false));
                }
            }

            @Nested
            class AndAllUserResponseNotCompleted {

                private DigitalDocumentUser digitalDocumentUser;

                @BeforeEach
                void setUp() {
                    final DigitalDocument digitalDocument = itUtil.findDigitalDocumentByPostId(campaign.getSourcePostId());

                    digitalDocumentUser = itUtil.createDigitalDocumentUser(
                        digitalDocument, user, stock, someString(10), DigitalDocumentAnswerStatus.SAVE);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

                    assertThat(result.getStatus(), is("ok"));
                    assertCampaignDownloadInDatabase();
                    assertUnfinishedDigitalDocumentUserNotExistInDatabase();
                    assertLambdaRequest(isSecured, getZipFileName(false));
                }

                private void assertUnfinishedDigitalDocumentUserNotExistInDatabase() {
                    final Long id = digitalDocumentUser.getId();
                    assertThat(existsDigitalDocumentUserInDatabase(id), is(false));
                }
            }
        }
    }

    @Nested
    class WhenCampaignNotFinished {

        private DigitalDocumentUser digitalDocumentUser;

        @BeforeEach
        void setUp() {
            campaign = createCampaign(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(1)
            );
            campaignId = campaign.getId();

            itUtil.createCampaignDownload(campaignId, user.getId(), false);
            itUtil.createCampaignDownload(campaignId, user.getId(), true);

            final DigitalDocument digitalDocument = itUtil.findDigitalDocumentByPostId(campaign.getSourcePostId());

            digitalDocumentUser = itUtil.createDigitalDocumentUser(
                digitalDocument, user, stock, someString(10), DigitalDocumentAnswerStatus.SAVE);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final SimpleStringResponse result = itUtil.getResult(callApi(status().isOk()), SimpleStringResponse.class);

            assertThat(result.getStatus(), is("ok"));
            assertCampaignDownloadInDatabase();
            assertUnfinishedDigitalDocumentUserExistInDatabase();
            assertLambdaRequest(isSecured, getZipFileName(true));
        }

        private void assertUnfinishedDigitalDocumentUserExistInDatabase() {
            assertThat(existsDigitalDocumentUserInDatabase(digitalDocumentUser.getId()), is(true));
        }

    }

    private String getZipFileName(boolean isTemp) {
        final String yyMMdd = KoreanDateTimeUtil.toKoreanTime(campaign.getCreatedAt()).format(DateTimeFormatUtil.yyMMdd());
        return "%s_%s.zip".formatted(campaign.getTitle() + (isTemp ? "_temp" : ""), yyMMdd);
    }

    private boolean existsDigitalDocumentUserInDatabase(Long digitalDocumentUserId) {
        return itUtil.findDigitalDocumentUserById(digitalDocumentUserId).isPresent();
    }

    private void assertLambdaRequest(boolean isSecured, String zipFileName) {
        then(lambdaService).should().invokeLambdaAsync(anyString(), lambdaRequestBodyCaptor.capture());
        final String lambdaRequest = lambdaRequestBodyCaptor.getValue();
        final String yyMMdd = KoreanDateTimeUtil.toKoreanTime(campaign.getCreatedAt()).format(DateTimeFormatUtil.yyMMdd());

        assertThat(lambdaRequest, containsString("\"destinationDirectory\":\"contents/campaign/%s/destination\"".formatted(campaignId)));
        assertThat(lambdaRequest, containsString("\"zipFilename\":\"%s\"".formatted(zipFileName)));
        assertThat(lambdaRequest, containsString("\"fileType\":\"CAMPAIGN_DIGITAL_DOCUMENT\""));

        if (isSecured) {
            assertThat(lambdaRequest, containsString("\"password\":\"%s\"".formatted(yyMMdd)));
        } else {
            assertThat(lambdaRequest, not(containsString("\"password\"")));
        }
    }

    private void assertCampaignDownloadInDatabase() {

        final List<CampaignDownload> campaignDownloadList = itUtil.findAllCampaignDownload(campaignId);

        assertThat(campaignDownloadList.size(), is(3));

        assertThat(campaignDownloadList.get(0).getCampaignId(), is(campaignId));
        assertThat(campaignDownloadList.get(0).getRequestUserId(), is(user.getId()));
        assertThat(campaignDownloadList.get(0).getIsLatest(), is(false));
        assertThat(campaignDownloadList.get(0).getZipFileStatus(), is(ZipFileStatus.REQUEST));
        assertThat(campaignDownloadList.get(0).getDownloadCount(), is(0));

        assertThat(campaignDownloadList.get(1).getCampaignId(), is(campaignId));
        assertThat(campaignDownloadList.get(1).getRequestUserId(), is(user.getId()));
        assertThat(campaignDownloadList.get(1).getIsLatest(), is(false));
        assertThat(campaignDownloadList.get(1).getZipFileStatus(), is(ZipFileStatus.REQUEST));
        assertThat(campaignDownloadList.get(1).getDownloadCount(), is(0));

        assertThat(campaignDownloadList.get(2).getCampaignId(), is(campaignId));
        assertThat(campaignDownloadList.get(2).getRequestUserId(), is(user.getId()));
        assertThat(campaignDownloadList.get(2).getIsLatest(), is(true));
        assertThat(campaignDownloadList.get(2).getZipFileStatus(), is(ZipFileStatus.IN_PROGRESS));
        assertThat(campaignDownloadList.get(2).getDownloadCount(), is(0));

        final CampaignDownload actual = campaignDownloadList.get(2);
        final CampaignDownload expected = itUtil.findCampaign(campaignId).getLatestCampaignDownload()
            .orElseThrow();
        assertThat(actual.getId(), equalTo(expected.getId()));
        assertThat(actual.getCampaignId(), equalTo(expected.getCampaignId()));
        assertThat(actual.getRequestUserId(), equalTo(expected.getRequestUserId()));
        assertThat(actual.getIsLatest(), equalTo(expected.getIsLatest()));
        assertThat(actual.getZipFileStatus(), equalTo(expected.getZipFileStatus()));
        assertThat(actual.getDownloadCount(), equalTo(expected.getDownloadCount()));

    }

    private MvcResult callApi(ResultMatcher badRequest) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, campaignId, isSecured)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(badRequest)
            .andReturn();
    }
}
