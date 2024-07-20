package ag.act.service.download.zip;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.ZipFileDownload;
import ag.act.service.download.DownloadService;
import ag.act.util.DownloadFileUtil;
import ag.act.validator.ZipFileDownloadValidator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class ZipFileDownloadServiceTest {

    @InjectMocks
    private ZipFileDownloadService service;
    private List<MockedStatic<?>> statics;
    @Mock
    private ZipFileDownloadValidator zipFileDownloadValidator;
    @Mock
    private HttpServletResponse response;
    @Mock
    private DownloadService downloadService;

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
    class DownloadZipFile {
        @Mock
        private ZipFileDownload zipFileDownload;
        @Mock
        private ServletOutputStream outputStream;
        private DownloadFile actualDownloadFile;
        private String zipFilePath;
        private String filename;

        @BeforeEach
        void setUp() throws IOException {
            zipFilePath = someString(5);
            filename = FilenameUtils.getName(zipFilePath);

            given(RequestContextHolder.getResponse()).willReturn(response);
            given(response.getOutputStream()).willReturn(outputStream);
            given(FilenameUtils.getName(zipFilePath)).willReturn(filename);
            given(DownloadFileUtil.getDownloadFileName(filename)).willReturn(filename);
            given(zipFileDownload.getZipFilePath()).willReturn(zipFilePath);
            willDoNothing().given(zipFileDownloadValidator).validate(zipFileDownload);

            given(downloadService.downloadFile(zipFilePath, filename, "application/octet-stream"))
                .willReturn(DownloadFile.builder().fileName(filename).build());
            actualDownloadFile = service.downloadZipFile(zipFileDownload);
        }

        @Test
        void shouldCallValidate() {
            then(zipFileDownloadValidator).should().validate(zipFileDownload);
        }

        @Test
        void shouldReturnTheResource() {
            assertThat(actualDownloadFile.getFileName(), Matchers.is(filename));
        }
    }
}
