package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontDto;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontProvider;
import ag.act.service.digitaldocument.DigitalDocumentTemplatePathService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Stream;

import static ag.act.TestUtil.getDigitalDocumentTemplatePath;
import static ag.act.itutil.PdfTestHelper.assertPdfTextEquals;
import static ag.act.itutil.PdfTestHelper.readFileToString;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"LineLength", "unused"})
class PDFRenderServiceTest {
    private static final String RESOURCE_PATH = "./src/test/resources/templates/digitaldocument/";
    @InjectMocks
    private PDFRenderService service;
    @Mock
    private PDFRendererBuilderFactory pdfRendererBuilderFactory;
    @Mock
    private DigitalDocumentTemplatePathService digitalDocumentTemplatePathService;
    @Mock
    private PDFFontProvider pdfFontProvider;
    @Mock
    private InputStream inputStream;
    @Mock
    private PDFFontDto pdfFontDto;

    @BeforeEach
    void setUp() throws Exception {
        given(pdfRendererBuilderFactory.create()).willReturn(new PdfRendererBuilder());
        given(digitalDocumentTemplatePathService.getPath()).willReturn(
            getDigitalDocumentTemplatePath()
        );
        given(pdfFontProvider.getPDFFontDto()).willReturn(pdfFontDto);
        given(pdfFontDto.inputStream()).willReturn(inputStream);
        given(pdfFontDto.fontFamily()).willReturn(someString(10));
    }

    @ParameterizedTest(name = "{index} => htmlFileName=''{0}'', expectedPdfFileName=''{1}''")
    @MethodSource("valueProvider")
    public void shouldRenderPdf(String htmlFileName, String expectedPdfFileName) {

        // Given
        String htmlString = readFileToString(RESOURCE_PATH + htmlFileName);

        // When
        byte[] actual = service.renderPdf(htmlString);

        // Then
        File expected = new File(RESOURCE_PATH + expectedPdfFileName);
        assertPdfTextEquals(actual, expected);
    }

    private static Stream<Arguments> valueProvider() {
        return Stream.of(
            Arguments.of("other-document.html", "other-document.pdf"),
            Arguments.of("digital-document-certification.html", "digital-document-certification.pdf")
        );
    }
}
