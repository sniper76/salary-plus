package ag.act.api.publicopen;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DownloadFileType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.ByteArrayInputStream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class DownloadDigitalDocumentZipFileIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/public/download/{downloadFileTypeName}/{zipFileKey}";

    private User user;
    private Long digitalDocumentId;
    private String latestZipFileKey;
    private String latestZipFileContent;
    private String oldZipFileKey;
    private String inProgressZipFileKey;
    private String downloadFileTypeName;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();

        downloadFileTypeName = DownloadFileType.ZIP.name();
        final DigitalDocument digitalDocument = mockDigitalProxyDocument();
        digitalDocumentId = digitalDocument.getId();

        inProgressZipFileKey = someAlphanumericString(5);
        oldZipFileKey = someAlphanumericString(10);
        latestZipFileKey = someAlphanumericString(30);

        final String latestZipFilePath = someAlphanumericString(40);
        latestZipFileContent = someAlphanumericString(50);

        stubInProgress();
        stubOldZipFile();
        stubLatestZipFile(latestZipFilePath);

        // S3 는 Mocking 처리한다.
        given(itUtil.getS3ServiceMockBean().readObject(latestZipFilePath)).willReturn(new ByteArrayInputStream(latestZipFileContent.getBytes()));
    }

    private void stubLatestZipFile(String latestZipFilePath) {
        final DigitalDocumentDownload latestDownload = itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), true);
        latestDownload.setZipFileStatus(ZipFileStatus.COMPLETE);
        latestDownload.setZipFileKey(latestZipFileKey);
        latestDownload.setZipFilePath(latestZipFilePath);
        itUtil.updateDigitalDocumentDownload(latestDownload);
    }

    private void stubOldZipFile() {
        final DigitalDocumentDownload oldDownload = itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), false);
        oldDownload.setZipFileStatus(ZipFileStatus.COMPLETE);
        oldDownload.setZipFileKey(oldZipFileKey);
        oldDownload.setZipFilePath(someAlphanumericString(20));
        itUtil.updateDigitalDocumentDownload(oldDownload);
    }

    private void stubInProgress() {
        final DigitalDocumentDownload inProgressDownload = itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), false);
        inProgressDownload.setZipFileStatus(ZipFileStatus.IN_PROGRESS);
        inProgressDownload.setZipFileKey(inProgressZipFileKey);
        itUtil.updateDigitalDocumentDownload(inProgressDownload);
    }

    private DigitalDocument mockDigitalProxyDocument() {
        final Stock stock = itUtil.createStock(someStockCode());
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(someEnum(DigitalDocumentType.class));
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    @Nested
    class WhenZipFileDownloadIsSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(latestZipFileKey, status().isOk());

            final String actual = response.getResponse().getContentAsString();
            assertThat(actual, is(latestZipFileContent));

        }
    }

    @Nested
    class WhenZipFileDownloadIsNotLatest {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(oldZipFileKey, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "ZIP 파일을 더 이상 다운로드 할 수 없습니다.");

        }
    }

    @Nested
    class WhenZipFileDownloadIsNotYetPrepared {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(inProgressZipFileKey, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "ZIP 파일이 준비되지 않았습니다.");
        }
    }

    @Nested
    class WhenDigitalDocumentDownloadNotFound {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            MvcResult response = callApi(someAlphanumericString(15), status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "파일을 찾을 수 없습니다.");
        }
    }

    @NotNull
    private MvcResult callApi(String zipFileKey, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, downloadFileTypeName, zipFileKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
