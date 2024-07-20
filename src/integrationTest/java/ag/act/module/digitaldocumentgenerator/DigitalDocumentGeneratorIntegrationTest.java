package ag.act.module.digitaldocumentgenerator;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.document.DigitalDocumentGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.util.DateTimeFormatUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static ag.act.TestUtil.someFilename;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class DigitalDocumentGeneratorIntegrationTest extends AbstractCommonIntegrationTest {
    private final String originalSignatureSampleImageFilename = someFilename();
    private final String originalIdCardSampleImageFilename = someFilename();
    private MockMultipartFile signatureSampleImage;
    private MockMultipartFile idCardSampleImage;
    @Autowired
    private DigitalDocumentGenerator generator;
    private Stock stock;
    private User user;
    private Post post;
    private DigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        itUtil.init();
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    private void assertPath(String path) {
        assertThat(path, startsWith(
            String.format("contents/digitaldocument/%s/source/%s_%s_%s(%s)",
                digitalDocument.getId(), stock.getName(), post.getTitle(),
                user.getName(), DateTimeFormatUtil.yyMMdd().format(user.getBirthDate())
            )));
    }

    private void mockGenerateDigitalDocument(DigitalDocumentType digitalDocumentType) {
        stock = itUtil.createStock(someStockCode());

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.CO_HOLDING_ARRANGEMENTS);
        final User postWriteUser = itUtil.createUser();
        post = itUtil.createPost(board, postWriteUser.getId());

        user = itUtil.createUser();
        stock = itUtil.createStock();

        final User acceptUser = itUtil.createUser();
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);

        digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, digitalDocumentType);

        digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock);

        given(ActUserProvider.getNoneNull()).willReturn(user);

        // JsonMyDataStock 은 Mocking 한다.
        mockUserHoldingStock();
    }

    private void mockUserHoldingStock() {
        final long quantity = 987654321L;
        final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(digitalDocument.getStockCode(), user);
        userHoldingStock.setQuantity(quantity);
        itUtil.updateUserHoldingStock(userHoldingStock);
        final UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(
            digitalDocument.getStockCode(), user.getId(), digitalDocumentUser.getStockReferenceDate()
        );
        userHoldingStockOnReferenceDate.setQuantity(quantity);
    }

    private void mockDigitalDocumentItems() {
        final List<DigitalDocumentItem> digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        digitalDocument.setDigitalDocumentItemList(digitalDocumentItemList);
        digitalDocument.setDigitalDocumentUserList(List.of(digitalDocumentUser));
        itUtil.updateDigitalDocument(digitalDocument);

        for (DigitalDocumentItem item : digitalDocumentItemList) {
            itUtil.createDigitalDocumentItemUserAnswer(item, user.getId());
        }
    }

    private void mockSignatureImage() {
        signatureSampleImage = new MockMultipartFile(
            "signImage",
            originalSignatureSampleImageFilename,
            MediaType.IMAGE_PNG_VALUE,
            "test image".getBytes()
        );
    }

    private void mockIdCardImage() {
        idCardSampleImage = new MockMultipartFile(
            "idCardImage",
            originalIdCardSampleImageFilename,
            MediaType.IMAGE_PNG_VALUE,
            "test image".getBytes()
        );
    }

    @Nested
    class WhenGenerateDigitalProxy {
        @BeforeEach
        void setUp() {
            mockGenerateDigitalDocument(DigitalDocumentType.DIGITAL_PROXY);
            mockDigitalDocumentItems();
            mockSignatureImage();
            mockIdCardImage();
        }

        private GenerateDigitalDocumentDto getGenerateDigitalDocumentDto() {
            return GenerateDigitalDocumentDto.builder()
                .signatureImage(signatureSampleImage)
                .attachingFilesDto(
                    AttachingFilesDto.builder()
                        .idCardImage(idCardSampleImage)
                        .build()
                )
                .build();
        }

        @Test
        void shouldGenerateDigitalProxy() {

            // Given
            GenerateDigitalDocumentDto dto = getGenerateDigitalDocumentDto();

            // When
            PdfDataDto actual = generator.generate(dto, digitalDocument, digitalDocumentUser);

            // Then
            assertPath(actual.getPath());
            assertThat(actual.getOriginalPageCount(), is(1L));
            assertThat(actual.getAttachmentPageCount(), is(1L)); // id card image
        }
    }

    @Nested
    class WhenGenerateJointOwnershipDocument {
        private final String originalBankAccountSample1Filename = someFilename();
        private final String originalBankAccountSample2Filename = someFilename();
        private MockMultipartFile bankAccountSample1Image;
        private MockMultipartFile bankAccountSample2Image;

        @BeforeEach
        void setUp() {
            mockGenerateDigitalDocument(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
            mockSignatureImage();
            mockIdCardImage();

            mockBankAccountSample1Image();
            mockBankAccountSample2Image();
        }

        private GenerateDigitalDocumentDto getGenerateDigitalDocumentDto() {
            return GenerateDigitalDocumentDto.builder()
                .signatureImage(signatureSampleImage)
                .attachingFilesDto(
                    AttachingFilesDto.builder()
                        .idCardImage(idCardSampleImage)
                        .bankAccountImages(List.of(bankAccountSample1Image, bankAccountSample2Image))
                        .build()
                )
                .build();
        }

        @Test
        void shouldGenerateJointOwnershipDocument() {

            // Given
            GenerateDigitalDocumentDto dto = getGenerateDigitalDocumentDto();

            // When
            PdfDataDto actual = generator.generate(dto, digitalDocument, digitalDocumentUser);

            // Then
            assertPath(actual.getPath());
            assertThat(actual.getOriginalPageCount(), is(1L));
            assertThat(actual.getAttachmentPageCount(), is(3L)); // id card image + bank account image 1,2
        }

        private void mockBankAccountSample1Image() {
            bankAccountSample1Image = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountSample1Filename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        private void mockBankAccountSample2Image() {
            bankAccountSample2Image = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountSample2Filename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }
    }

    @Nested
    class WhenGenerateOtherDocument {
        @BeforeEach
        void setUp() {
            mockGenerateDigitalDocument(DigitalDocumentType.ETC_DOCUMENT);
            mockSignatureImage();
        }

        private GenerateDigitalDocumentDto getGenerateDigitalDocumentDto() {
            return GenerateDigitalDocumentDto.builder()
                .signatureImage(signatureSampleImage)
                .build();
        }

        @Test
        void shouldGenerateOtherDocument() {

            // Given
            GenerateDigitalDocumentDto dto = getGenerateDigitalDocumentDto();

            // When
            PdfDataDto actual = generator.generate(dto, digitalDocument, digitalDocumentUser);

            // Then
            assertPath(actual.getPath());
            assertThat(actual.getOriginalPageCount(), is(1L));
            assertThat(actual.getAttachmentPageCount(), is(0L)); // no images
        }
    }

}
