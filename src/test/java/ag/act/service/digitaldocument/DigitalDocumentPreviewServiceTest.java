package ag.act.service.digitaldocument;

import ag.act.dto.download.DownloadFile;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentPreviewServiceTest {
    @Mock
    PreviewDigitalDocumentRequest previewRequest;
    @Mock
    DigitalDocumentFill digitalDocumentFill;
    @InjectMocks
    private DigitalDocumentPreviewService service;
    @Mock
    private DigitalDocumentPreviewFillService digitalDocumentPreviewFillService;
    @Mock
    private DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    @Mock
    private PDFRenderService pdfRenderService;
    private DigitalDocumentType digitalDocumentType;
    private String htmlString;
    private DownloadFile downloadFile;

    @BeforeEach
    void setUp() {
        htmlString = someString(50);
        digitalDocumentType = someThing(DigitalDocumentType.values());
        byte[] pdfBytes = someString(10).getBytes();

        given(digitalDocumentPreviewFillService.fill(previewRequest))
            .willReturn(digitalDocumentFill);
        given(pdfRenderService.renderPdf(htmlString))
            .willReturn(pdfBytes);
        given(previewRequest.getType())
            .willReturn(digitalDocumentType.name());
        given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(digitalDocumentFill, digitalDocumentType.getTemplateName()))
            .willReturn(htmlString);

        downloadFile = service.createPreviewDocument(previewRequest);
    }

    @Nested
    class WhenCreatePreviewDocument {
        @Test
        void shouldGenerateHtmlString() {
            then(digitalDocumentHtmlGenerator).should()
                .fillAndGetHtmlString(digitalDocumentFill, digitalDocumentType.getTemplateName());
        }

        @Test
        void shouldRenderPdf() {
            then(pdfRenderService).should().renderPdf(htmlString);
        }

        @Test
        void shouldDownloadPreviewFile() {
            assertThat(downloadFile.getFileName(), is("digital-document-preview.pdf"));
            assertThat(downloadFile.getContentType(), is(MediaType.APPLICATION_PDF));
            assertThat(downloadFile.getResource(), is(notNullValue()));
        }
    }
}
