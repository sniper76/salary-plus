package ag.act.facade;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.service.digitaldocument.DigitalDocumentDownloadService;
import ag.act.util.DownloadFileUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentDownloadFacadeTest {

    @InjectMocks
    private DigitalDocumentDownloadFacade facade;
    @Mock
    private DigitalDocumentDownloadService digitalDocumentDownloadService;

    private List<MockedStatic<?>> statics;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        statics = List.of(
            mockStatic(RequestContextHolder.class),
            mockStatic(DownloadFileUtil.class),
            mockStatic(FilenameUtils.class)
        );
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class CreateDigitalDocumentZipFile {

        @Mock
        private DigitalDocumentDownload digitalDocumentDownload;
        private Long digitalDocumentId;
        private Long digitalDocumentDownloadId;
        private Boolean isSecured;

        @BeforeEach
        void setUp() {
            isSecured = someBoolean();
            digitalDocumentId = someLong();
            digitalDocumentDownloadId = someLong();

            given(digitalDocumentDownloadService.createDigitalDocumentDownload(digitalDocumentId))
                .willReturn(digitalDocumentDownload);
            given(digitalDocumentDownload.getId()).willReturn(digitalDocumentDownloadId);
            willDoNothing().given(digitalDocumentDownloadService).invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownloadId, isSecured);
            given(digitalDocumentDownloadService.updateDigitalDocumentZipFileInProgress(digitalDocumentDownloadId))
                .willReturn(digitalDocumentDownload);
            given(digitalDocumentDownloadService.isDigitalDocumentFinished(digitalDocumentId)).willReturn(true);
        }

        @Nested
        class CreateDigitalDocumentZipFileSuccessfully extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                facade.createDigitalDocumentZipFile(digitalDocumentId, isSecured);
            }
        }

        @Nested
        class WhenUnfinishedUserDigitalDocumentExists extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                facade.createDigitalDocumentZipFile(digitalDocumentId, isSecured);
            }

            @Test
            void shouldCleanupAllUnfinishedUserDigitalDocuments() {
                then(digitalDocumentDownloadService).should().cleanupAllUnfinishedUserDigitalDocuments(digitalDocumentId);
            }
        }

        @Nested
        class WhenDigitalDocumentNotFinished {
            @BeforeEach
            void setUp() {
                given(digitalDocumentDownloadService.isDigitalDocumentFinished(digitalDocumentId)).willReturn(false);
                facade.createDigitalDocumentZipFile(digitalDocumentId, isSecured);
            }

            @Test
            void shouldNotCleanupAllUnfinishedUserDigitalDocuments() {
                then(digitalDocumentDownloadService).should(never()).cleanupAllUnfinishedUserDigitalDocuments(digitalDocumentId);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldCallCreateDigitalDocumentZipFile() {
                then(digitalDocumentDownloadService).should().createDigitalDocumentDownload(digitalDocumentId);
            }

            @Test
            void shouldCallInvokeZipFilesLambda() {
                then(digitalDocumentDownloadService).should().invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownloadId, isSecured);
            }

            @Test
            void shouldCallInProgressDigitalDocumentZipFile() {
                then(digitalDocumentDownloadService).should().updateDigitalDocumentZipFileInProgress(digitalDocumentDownloadId);
            }
        }
    }

    @Nested
    class UpdateDigitalDocumentDownloadZipFile {
        @Mock
        private ag.act.model.DigitalDocumentZipFileCallbackRequest request;
        @Mock
        private DigitalDocumentDownload digitalDocumentDownload;
        private Long digitalDocumentDownloadId;
        private String zipFilePath;

        @BeforeEach
        void setUp() {

            digitalDocumentDownloadId = someLong();
            zipFilePath = someString(10);

            given(request.getDigitalDocumentDownloadId()).willReturn(digitalDocumentDownloadId);
            given(request.getZipFilePath()).willReturn(zipFilePath);
            given(digitalDocumentDownloadService.completeDigitalDocumentDownloadZipFile(digitalDocumentDownloadId, zipFilePath))
                .willReturn(digitalDocumentDownload);

            facade.updateDownloadZipFile(request);
        }

        @Test
        void shouldCallCompleteDigitalDocumentDownloadZipFile() {
            then(digitalDocumentDownloadService).should().completeDigitalDocumentDownloadZipFile(digitalDocumentDownloadId, zipFilePath);
        }
    }

    @Nested
    class DownloadDigitalDocumentUserResponseCsv {

        private Long digitalDocumentId;
        private String csvFilename;
        private DownloadFile downloadFile;

        @BeforeEach
        void setUp() {

            digitalDocumentId = someLong();
            csvFilename = someString(5);
            final String encodedCsvFilename = someString(5);

            given(DownloadFileUtil.getDownloadFileName(csvFilename)).willReturn(encodedCsvFilename);
            given(RequestContextHolder.getResponse()).willReturn(response);
            given(digitalDocumentDownloadService.getCsvFilename(digitalDocumentId)).willReturn(csvFilename);
            willDoNothing().given(digitalDocumentDownloadService).downloadDigitalDocumentUserResponseInCsv(digitalDocumentId, response);

            downloadFile = facade.downloadUserResponseInCsv(digitalDocumentId);
        }

        @Test
        void shouldReturnDownloadFile() {
            assertThat(downloadFile.getFileName(), is(csvFilename));
            assertThat(downloadFile.getContentType(), nullValue());
            assertThat(downloadFile.getResource(), nullValue());
        }

        @Test
        void shouldCallGetCsvFilename() {
            then(digitalDocumentDownloadService).should().getCsvFilename(digitalDocumentId);
        }
    }
}
