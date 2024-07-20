package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentFilenameGenerator;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.DigitalDocumentTypeGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlDocumentDto;
import ag.act.module.markany.dna.MarkAnyDigitalDocument;
import ag.act.module.markany.dna.MarkAnyService;
import ag.act.util.FilenameUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBytes.someBytes;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultDigitalDocumentGeneratorTest {
    @InjectMocks
    private DefaultDigitalDocumentGenerator generator;

    private List<MockedStatic<?>> statics;
    @Mock
    private DigitalDocumentTypeGenerator digitalDocumentTypeGenerator;
    @Mock
    private MarkAnyService markAnyService;
    @Mock
    private DigitalDocumentFilenameGenerator digitalDocumentFilenameGenerator;
    @Mock
    private DigitalDocumentUploadService digitalDocumentUploadService;
    @Mock
    private MultipartFile signatureImage;
    @Mock
    private AttachingFilesDto attachingFilesDto;
    @Mock
    private DigitalDocument digitalDocument;
    @Mock
    private DigitalDocumentUser digitalDocumentUser;
    @Mock
    private DefaultDigitalDocumentGenerator defaultDigitalDocumentGenerator;
    @Mock
    private PdfDataDto expectedPdfDataDto;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(FilenameUtil.class));
    }

    @Nested
    class WhenGeneratePdf {

        private byte[] dnaPdfBytes;
        private PdfDataDto actualPdfDataDto;

        @BeforeEach
        void setUp() {

            final GenerateDigitalDocumentDto generateDto = generateDto();
            final DigitalDocumentType digitalDocumentType = someEnum(DigitalDocumentType.class);
            final Long userId = someLong();
            final String originalFilename = someAlphaString();
            final Long digitalDocumentId = someLong();
            final String fullPath = someString(10);
            final String generatedPdfUrl = someAlphaString();
            final byte[] sourcePdfBytes = someBytes();
            dnaPdfBytes = someBytes();

            given(FilenameUtil.getDigitalDocumentUploadPath(digitalDocumentId, originalFilename))
                .willReturn(fullPath);

            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(digitalDocument.getType()).willReturn(digitalDocumentType);
            given(digitalDocumentTypeGenerator.generate(any(GenerateHtmlDocumentDto.class))).willReturn(expectedPdfDataDto);
            given(expectedPdfDataDto.getPdfBytes()).willReturn(sourcePdfBytes);
            given(digitalDocumentUser.getUserId()).willReturn(userId);
            given(digitalDocumentFilenameGenerator.generate(digitalDocument, userId))
                .willReturn(originalFilename);
            given(digitalDocumentUploadService.uploadPdf(expectedPdfDataDto, fullPath, originalFilename)).willReturn(generatedPdfUrl);
            given(markAnyService.makeDna(any(MarkAnyDigitalDocument.class))).willReturn(dnaPdfBytes);

            actualPdfDataDto = generator.generate(generateDto, digitalDocument, digitalDocumentUser);
        }

        @Test
        void shouldReturnPdfDataDto() {
            assertThat(actualPdfDataDto, is(expectedPdfDataDto));
        }

        @Test
        void shouldUpdatePdfBytesWithPdfDnaBytes() {
            then(actualPdfDataDto).should().setPdfBytes(dnaPdfBytes);
        }

        @Test
        void shouldMakeDna() {
            then(markAnyService).should().makeDna(any(MarkAnyDigitalDocument.class));
        }
    }

    private GenerateDigitalDocumentDto generateDto() {
        return GenerateDigitalDocumentDto
            .builder()
            .signatureImage(signatureImage)
            .attachingFilesDto(attachingFilesDto)
            .build();
    }
}
