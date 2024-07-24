package ag.act.module.digitaldocumentgenerator;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.module.digitaldocumentgenerator.converter.BaseDigitalDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.converter.DigitalProxyFillConverter;
import ag.act.module.digitaldocumentgenerator.converter.JointOwnershipDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.converter.OtherDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.DigitalProxyGenerator;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.JointOwnershipDocumentGenerator;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.OtherDocumentGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyFill;
import ag.act.module.digitaldocumentgenerator.model.JointOwnershipDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.OtherDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.SinglePageImageFill;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFMergerService;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.util.MultipartFileToBase64Util;
import ag.act.module.digitaldocumentgenerator.validator.DigitalProxyFillValidator;
import ag.act.module.digitaldocumentgenerator.validator.JointOwnershipDocumentFillValidator;
import ag.act.module.digitaldocumentgenerator.validator.OtherDocumentFillValidator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AbstractDigitalDocumentTypeGeneratorIntegrationTest extends AbstractCommonIntegrationTest {
    @Autowired
    private DigitalProxyGenerator digitalProxyGenerator;
    @MockBean
    private DigitalProxyFillValidator digitalProxyFillValidator;
    @MockBean
    private DigitalProxyFillConverter digitalProxyFillConverter;
    @Autowired
    private JointOwnershipDocumentGenerator jointOwnershipDocumentGenerator;
    @MockBean
    private JointOwnershipDocumentFillValidator jointOwnershipDocumentFillValidator;
    @MockBean
    private JointOwnershipDocumentFillConverter jointOwnershipDocumentFillConverter;
    @Autowired
    private OtherDocumentGenerator otherDocumentGenerator;
    @MockBean
    private OtherDocumentFillValidator otherDocumentFillValidator;
    @MockBean
    private OtherDocumentFillConverter otherDocumentFillConverter;
    @MockBean
    private PDFMergerService pdfMergerService;
    @MockBean
    private PDFRenderService pdfRenderService;
    @MockBean
    private DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    @Mock
    private GenerateHtmlDocumentDto generateHtmlDocumentDto;
    @Mock
    private AttachingFilesDto attachingFilesDto;
    @Mock
    private MultipartFile idCardImage;
    @Mock
    private SinglePageImageFill idCardImageFill;
    @Mock
    private PDDocument pdDocument;
    private String documentHtmlString;
    private String idCardHtmlString;
    private List<MockedStatic<?>> statics;
    private byte[] digitalDocumentPdfBytes;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() throws IOException {
        statics = List.of(mockStatic(MultipartFileToBase64Util.class), mockStatic(PDDocument.class));

        documentHtmlString = someAlphaString();
        idCardHtmlString = someAlphaString();
        digitalDocumentPdfBytes = someString(5).getBytes();

        given(MultipartFileToBase64Util.convertMultipartFileToBase64(any()))
            .willReturn(someString(5));
        given(pdfMergerService.mergePdfSources(any())).willReturn(digitalDocumentPdfBytes);
        given(PDDocument.load(digitalDocumentPdfBytes)).willReturn(pdDocument);
        given(pdDocument.getNumberOfPages()).willReturn(someInteger());
    }

    private void givenIdCardImage(BaseDigitalDocumentFillConverter converter) {
        given(attachingFilesDto.getIdCardImage()).willReturn(idCardImage);
        given(converter.convert(idCardImage))
            .willReturn(idCardImageFill);
        given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(idCardImageFill, "single-page-image.ftlh"))
            .willReturn(idCardHtmlString);
    }

    @Nested
    class WhenTypeIsDigitalProxy extends DefaultTestCasesWithIdCardImage {
        @Mock
        private DigitalProxyFill digitalProxyFill;

        @BeforeEach
        void setUp() {

            // Given
            given(generateHtmlDocumentDto.getAttachingFilesDto())
                .willReturn(attachingFilesDto);
            given(attachingFilesDto.getIdCardImage()).willReturn(null);
            given(attachingFilesDto.getBankAccountImages()).willReturn(null);
            willDoNothing().given(digitalProxyFillValidator).validate(generateHtmlDocumentDto);
            given(digitalProxyFillConverter.convert(generateHtmlDocumentDto))
                .willReturn(digitalProxyFill);
            given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(digitalProxyFill, "digital-proxy-v5.ftlh"))
                .willReturn(documentHtmlString);
            given(pdfRenderService.renderPdf(documentHtmlString))
                .willReturn(digitalDocumentPdfBytes);
            givenIdCardImage(digitalProxyFillConverter);

            // When
            digitalProxyGenerator.generateDigitalDocumentPdf(generateHtmlDocumentDto);
        }

        @Test
        void shouldValidateDigitalDocumentFill() {
            then(digitalProxyFillValidator).should().validate(generateHtmlDocumentDto);
        }

        @Test
        void shouldConvertDigitalDocumentFill() {
            then(digitalProxyFillConverter).should().convert(generateHtmlDocumentDto);
        }

        @Test
        void shouldFillAndGetHtmlString() {
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(
                digitalProxyFill, "digital-proxy-v5.ftlh"
            );
        }
    }

    @Nested
    class WhenTypeIsJointOwnershipDocument extends DefaultTestCasesWithIdCardImage {
        @Mock
        private JointOwnershipDocumentFill jointOwnershipDocumentFill;
        @Mock
        private MultipartFile bankAccountImage1;
        @Mock
        private MultipartFile bankAccountImage2;
        @Mock
        private SinglePageImageFill bankAccountImageFill1;
        @Mock
        private SinglePageImageFill bankAccountImageFill2;
        private String bankAccountHtmlString1;
        private String bankAccountHtmlString2;

        @BeforeEach
        void setUp() {

            // Given
            given(generateHtmlDocumentDto.getAttachingFilesDto()).willReturn(attachingFilesDto);
            given(attachingFilesDto.getIdCardImage()).willReturn(null);
            given(attachingFilesDto.getBankAccountImages()).willReturn(List.of(bankAccountImage1, bankAccountImage2));
            willDoNothing().given(jointOwnershipDocumentFillValidator).validate(generateHtmlDocumentDto);
            given(jointOwnershipDocumentFillConverter.convert(generateHtmlDocumentDto))
                .willReturn(jointOwnershipDocumentFill);
            given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(jointOwnershipDocumentFill, "joint-ownership-document-v4.ftlh"))
                .willReturn(documentHtmlString);
            given(pdfRenderService.renderPdf(documentHtmlString))
                .willReturn(digitalDocumentPdfBytes);
            givenIdCardImage(jointOwnershipDocumentFillConverter);

            given(attachingFilesDto.getBankAccountImages())
                .willReturn(List.of(bankAccountImage1, bankAccountImage2));
            bankAccountHtmlString1 = someAlphaString();
            bankAccountHtmlString2 = someAlphaString();
            givenBankAccountImage(bankAccountImage1, bankAccountImageFill1, bankAccountHtmlString1);
            givenBankAccountImage(bankAccountImage2, bankAccountImageFill2, bankAccountHtmlString2);

            // When
            jointOwnershipDocumentGenerator.generateDigitalDocumentPdf(generateHtmlDocumentDto);
        }

        private void givenBankAccountImage(MultipartFile bankAccountImage, SinglePageImageFill bankAccountImageFill,
                                           String bankAccountHtmlString) {
            given(jointOwnershipDocumentFillConverter.convert(bankAccountImage))
                .willReturn(bankAccountImageFill);
            given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(bankAccountImageFill, "single-page-image.ftlh"))
                .willReturn(bankAccountHtmlString);
        }

        @Test
        void shouldValidateDigitalDocumentFill() {
            then(jointOwnershipDocumentFillValidator).should().validate(generateHtmlDocumentDto);
        }

        @Test
        void shouldConvertDigitalDocumentFill() {
            then(jointOwnershipDocumentFillConverter).should().convert(generateHtmlDocumentDto);
        }

        @Test
        void shouldFillAndGetHtmlString() {
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(
                jointOwnershipDocumentFill, "joint-ownership-document-v4.ftlh"
            );
        }

        @Test
        void shouldGenerateAdditionalPdfArray() {
            then(jointOwnershipDocumentFillConverter).should().convert(bankAccountImage1);
            then(jointOwnershipDocumentFillConverter).should().convert(bankAccountImage2);
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(
                bankAccountImageFill1, "single-page-image.ftlh"
            );
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(
                bankAccountImageFill2, "single-page-image.ftlh"
            );
            then(pdfRenderService).should().renderPdf(bankAccountHtmlString1);
            then(pdfRenderService).should().renderPdf(bankAccountHtmlString2);
        }
    }

    @Nested
    class WhenTypeIsOtherDocument extends DefaultTestCases {
        @Mock
        private OtherDocumentFill otherDocumentFill;

        @BeforeEach
        void setUp() {

            // Given
            given(generateHtmlDocumentDto.getAttachingFilesDto()).willReturn(attachingFilesDto);
            given(attachingFilesDto.getIdCardImage()).willReturn(null);
            given(attachingFilesDto.getBankAccountImages()).willReturn(null);
            willDoNothing().given(otherDocumentFillValidator).validate(generateHtmlDocumentDto);
            given(otherDocumentFillConverter.convert(generateHtmlDocumentDto))
                .willReturn(otherDocumentFill);
            given(digitalDocumentHtmlGenerator.fillAndGetHtmlString(otherDocumentFill, "other-document-v3.ftlh"))
                .willReturn(documentHtmlString);
            given(pdfRenderService.renderPdf(documentHtmlString))
                .willReturn(digitalDocumentPdfBytes);
            givenIdCardImage(otherDocumentFillConverter);

            // When
            otherDocumentGenerator.generateDigitalDocumentPdf(generateHtmlDocumentDto);
        }

        @Test
        void shouldValidateDigitalDocumentFill() {
            then(otherDocumentFillValidator).should().validate(generateHtmlDocumentDto);
        }

        @Test
        void shouldConvertDigitalDocumentFill() {
            then(otherDocumentFillConverter).should().convert(generateHtmlDocumentDto);
        }

        @Test
        void shouldFillAndGetHtmlString() {
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(
                otherDocumentFill, "other-document-v3.ftlh"
            );
        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldGeneratePdfBytesFromDocumentHtmlString() {
            then(pdfRenderService).should().renderPdf(documentHtmlString);
        }

        @Test
        void shouldMergePdfSources() {
            then(pdfMergerService).should(times(2)).mergePdfSources(any()); // first merge attachments, next merge it with the original document
        }
    }


    @SuppressWarnings("unused")
    class DefaultTestCasesWithIdCardImage {
        @Test
        void shouldFillIdCardImage() {
            then(digitalDocumentHtmlGenerator).should().fillAndGetHtmlString(idCardImageFill, "single-page-image.ftlh");
        }

        @Test
        void shouldGeneratePdfBytesFromIdCardHtmlString() {
            then(pdfRenderService).should().renderPdf(idCardHtmlString);
        }
    }
}
