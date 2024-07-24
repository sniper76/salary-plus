package ag.act.api.publicopen;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.entity.campaign.CampaignDownload;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DownloadFileType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DownloadCampaignZipFileIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/public/download/{downloadFileTypeName}/{zipFileKey}";

    private User adminUser;
    private String latestZipFileContent;
    private Long campaignId;
    private Stock stock1;
    private LocalDate referenceDate;
    private String campaignTitle;
    private String jwt;
    private String oldZipFileKey;
    private String inProgressZipFileKey;
    private String latestZipFileKey;
    private String downloadFileTypeName;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        downloadFileTypeName = DownloadFileType.ZIP.name();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
        campaignTitle = someAlphanumericString(10);
        final Campaign campaign = createCampaign(campaignTitle);
        campaignId = campaign.getId();
        final Long sourcePostId = campaign.getSourcePostId();

        mockEtcDigitalDocument(sourcePostId);

        inProgressZipFileKey = someAlphanumericString(5);
        oldZipFileKey = someAlphanumericString(10);
        latestZipFileKey = "LATEST_ZIP_FILE_KEY_" + someAlphanumericString(30);

        final String latestZipFilePath = someAlphanumericString(40);
        latestZipFileContent = someAlphanumericString(50);

        stubCampaignDownload(someAlphanumericString(40), false, ZipFileStatus.IN_PROGRESS, inProgressZipFileKey);
        stubCampaignDownload(someAlphanumericString(40), false, ZipFileStatus.COMPLETE, oldZipFileKey);
        stubCampaignDownload(latestZipFilePath, true, ZipFileStatus.COMPLETE, latestZipFileKey);

        // S3 는 Mocking 처리한다.
        given(itUtil.getS3ServiceMockBean().readObject(latestZipFilePath)).willReturn(new ByteArrayInputStream(latestZipFileContent.getBytes()));
    }

    private Campaign createCampaign(String campaignTitle) {
        final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(5);
        final LocalDateTime targetEndDate = LocalDateTime.now().minusDays(1);
        final StockGroup stockGroup = itUtil.createStockGroup(someString(10));

        stock1 = itUtil.createStock(someStockCode());
        Stock stock2 = itUtil.createStock(someStockCode());

        itUtil.createStockGroupMapping(stock1.getCode(), stockGroup.getId());
        itUtil.createStockGroupMapping(stock2.getCode(), stockGroup.getId());

        itUtil.createBoard(stock2);

        return itUtil.createCampaign(
            campaignTitle, List.of(stock1, stock2), targetStartDate, targetEndDate, referenceDate
        );
    }

    private void stubCampaignDownload(String latestZipFilePath, boolean isLatest, ZipFileStatus zipFileStatus, String latestZipFileKey) {
        final CampaignDownload campaignDownload = itUtil.createCampaignDownload(campaignId, adminUser.getId(), isLatest);
        campaignDownload.setZipFileStatus(zipFileStatus);
        campaignDownload.setZipFileKey(latestZipFileKey);
        campaignDownload.setZipFilePath(latestZipFilePath);

        updateAllCampaignDownloadToNotLatest();

        itUtil.updateCampaignDownload(campaignDownload);
    }

    private void updateAllCampaignDownloadToNotLatest() {
        itUtil.findAllCampaignDownload(campaignId)
            .stream()
            .peek(it -> it.setIsLatest(false))
            .forEach(itUtil::updateCampaignDownload);
    }

    private DigitalDocument mockEtcDigitalDocument(Long sourcePostId) {
        final Post post = itUtil.findPost(sourcePostId).orElseThrow();
        final User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock1, acceptUser);
        digitalDocument.setType(DigitalDocumentType.ETC_DOCUMENT);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    @Nested
    class WhenZipFileDownloadIsSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(latestZipFileKey, downloadFileTypeName, status().isOk());

            final String actual = response.getResponse().getContentAsString();
            assertThat(actual, is(latestZipFileContent));
        }
    }

    @Nested
    class WhenZipFileDownloadIsNotLatest {

        @BeforeEach
        void setUp() {
            updateAllCampaignDownloadToNotLatest();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(oldZipFileKey, downloadFileTypeName, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "ZIP 파일을 더 이상 다운로드 할 수 없습니다.");
        }
    }

    @Nested
    class WhenZipFileDownloadIsNotYetPrepared {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(inProgressZipFileKey, downloadFileTypeName, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "ZIP 파일이 준비되지 않았습니다.");
        }
    }

    @Nested
    class WhenCampaignNotFound {

        @BeforeEach
        void setUp() {
            campaignId = createCampaign(campaignTitle).getId();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult response = callApi(someAlphanumericString(15), downloadFileTypeName, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "파일을 찾을 수 없습니다.");
        }
    }

    @Nested
    class WhenWrongDownloadFileTypeNameProvided {

        private String wrongDownloadFileTypeName;

        @BeforeEach
        void setUp() {
            wrongDownloadFileTypeName = someAlphanumericString(10);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(inProgressZipFileKey, wrongDownloadFileTypeName, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "지원하지 않는 DownloadFileType %s 입니다.".formatted(wrongDownloadFileTypeName));
        }
    }

    @NotNull
    private MvcResult callApi(String zipFileKey, String downloadFileTypeName1, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, downloadFileTypeName1, zipFileKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
