package ag.act.api.stockboardgrouppost.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.Board;
import ag.act.entity.CorporateUser;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.parser.DateTimeParser;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someFilename;
import static ag.act.itutil.PdfTestHelper.assertPdfBytesEqualsToFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("VariableDeclarationUsageDistance")
class UserCreateDigitalDocumentJointOwnershipDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";
    private final String originalSignImageFilename = someFilename();
    private final String originalIdCardImageFilename = someFilename();
    private final String originalBankAccountFilename1 = someFilename();
    private final String originalBankAccountFilename2 = someFilename();
    private final String originalHectoEncryptedStringFileName = someFilename();
    private String jwt;
    private Stock stock;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private DigitalDocument digitalDocument;
    private LocalDate referenceDate;
    private MyDataSummary myDataSummary;
    private MockMultipartFile signatureImageFile;
    @Value("classpath:/digitaldocument/samples/signature.png")
    private Resource signatureImage;
    private MockMultipartFile idCardImageFile;
    @Value("classpath:/digitaldocument/samples/id-card.jpg")
    private Resource idCardImage;
    private MockMultipartFile bankAccountFile1;
    @Value("classpath:/digitaldocument/samples/bank-account-1.jpg")
    private Resource bankAccount1Image;
    private MockMultipartFile bankAccountFile2;
    @Value("classpath:/digitaldocument/samples/bank-account-2.jpg")
    private Resource bankAccount2Image;
    private ag.act.model.JsonAttachOption jsonAttachOption;

    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void defaultSetup() {
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime targetStartDate = now.minusDays(3);
        final LocalDateTime targetEndDate = now.plusDays(1);

        Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.CO_HOLDING_ARRANGEMENTS);
        Post post = itUtil.createPost(board, postWriteUser.getId());
        digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT,
            targetStartDate, targetEndDate, referenceDate, jsonAttachOption
        );
        post.setDigitalDocument(digitalDocument);

        myDataSummary = itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
    }

    private void defaultSetupWithBankAccountImagesRequired() {
        jsonAttachOption = new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.REQUIRED.name())
            .signImage(AttachOptionType.REQUIRED.name())
            .bankAccountImage(AttachOptionType.REQUIRED.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());

        defaultSetup();
    }

    private MockMultipartFile getSignatureImage() throws IOException {
        byte[] signImageBytes = Files.readAllBytes(signatureImage.getFile().toPath());
        return new MockMultipartFile(
            "signImage", "signature.png", MediaType.IMAGE_PNG_VALUE, signImageBytes
        );
    }

    private MockMultipartFile getIdCardImage() throws IOException {
        byte[] idCardImageBytes = Files.readAllBytes(idCardImage.getFile().toPath());
        return new MockMultipartFile(
            "idCardImage", "id-card.jpg", MediaType.IMAGE_JPEG_VALUE, idCardImageBytes
        );
    }

    @Nested
    class WhenCreateJointOwnership {
        private final String corporateNo = "123456-1234567";
        private final String corporateName = "테스트법인";
        private byte[] userSignedPdf;

        @BeforeEach
        void setUp() throws IOException {
            user.setZipcode(someString(6));
            user.setAddress(someString(6));
            user.setAddressDetail(someString(6));
            itUtil.updateUser(user);

            signatureImageFile = getSignatureImage();
            idCardImageFile = getIdCardImage();

            given(itUtil.getS3ServiceMockBean().putObject(any(UploadFilePathDto.class), any(InputStream.class)))
                .willAnswer(invocation -> {
                    InputStream inputStream = invocation.getArgument(1);
                    userSignedPdf = inputStream.readAllBytes();
                    return true;
                });
        }

        private void setUpEntitiesForJointOwnershipDocumentFill() {
            user.setName("홍길동");
            user.setBirthDate(DateTimeParser.parseDate("19700101"));
            user.setFirstNumberOfIdentification(1);
            itUtil.updateUser(user);

            final JsonMyData jsonMyData = new JsonMyData();
            final JsonMyDataStock jsonMyDataStock = new JsonMyDataStock();
            jsonMyDataStock.setCode(stock.getCode());

            final long quantity = 300L;
            jsonMyDataStock.setQuantity(quantity);
            jsonMyDataStock.setMyDataProdCode(stock.getCode());
            jsonMyDataStock.setReferenceDate(referenceDate);
            jsonMyData.setJsonMyDataStockList(List.of(jsonMyDataStock));
            myDataSummary.setJsonMyData(jsonMyData);
            itUtil.updateMyDataSummary(myDataSummary);

            acceptUser.setName("홍길순");
            acceptUser.setBirthDate(DateTimeParser.parseDate("19641225"));
            acceptUser.setFirstNumberOfIdentification(2);
            itUtil.updateUser(acceptUser);

            final StockAcceptorUserHistory stockAcceptorUserHistory = itUtil.findStockAcceptorUserHistory(stock.getCode(), acceptUser.getId())
                .orElseThrow();
            stockAcceptorUserHistory.setName(acceptUser.getName());
            stockAcceptorUserHistory.setBirthDate(acceptUser.getBirthDate());
            stockAcceptorUserHistory.setFirstNumberOfIdentification(acceptUser.getFirstNumberOfIdentification());
            itUtil.updateStockAcceptorUserHistory(stockAcceptorUserHistory);

            digitalDocument.setCompanyName("주식회사 액트");
            digitalDocument.setCompanyRegistrationNumber("123-45-67890");
            digitalDocument.setContent("공동보유의 내용이 들어갑니다.");
            itUtil.updateDigitalDocument(digitalDocument);

            userHoldingStock.setQuantity(quantity);
            itUtil.updateUserHoldingStock(userHoldingStock);
            final UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(
                stock.getCode(), user.getId(), referenceDate
            );
            userHoldingStockOnReferenceDate.setQuantity(quantity);
            itUtil.updateUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDate);
        }

        private void assertResponse(MvcResult response, byte[] userSignedPdf, File expectedPdfFile) throws Exception {
            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocument.getId());
            final ag.act.model.JsonAttachOption jsonAttachOptionDatabase = digitalDocumentFromDatabase.getJsonAttachOption();
            final DigitalDocumentUser digitalDocumentUser =
                itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                    .orElseThrow();

            assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
            assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
            assertThat(result.getJoinUserCount(), is(digitalDocumentFromDatabase.getJoinUserCount()));
            assertThat(result.getJoinStockSum(), is(digitalDocumentFromDatabase.getJoinStockSum()));
            assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
            assertThat(result.getUser().getId(), is(user.getId()));
            assertThat(result.getStock().getCode(), is(stock.getCode()));
            assertThat(result.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(digitalDocument.getType(), is(digitalDocument.getType()));
            assertThat(digitalDocumentFromDatabase.getTitle(), is(digitalDocument.getTitle()));
            assertThat(digitalDocumentFromDatabase.getContent(), is(digitalDocument.getContent()));
            assertThat(jsonAttachOptionDatabase.getSignImage(), is(jsonAttachOption.getSignImage()));
            assertThat(jsonAttachOptionDatabase.getIdCardImage(), is(jsonAttachOption.getIdCardImage()));
            assertThat(jsonAttachOptionDatabase.getBankAccountImage(), is(jsonAttachOption.getBankAccountImage()));
            assertThat(jsonAttachOptionDatabase.getHectoEncryptedBankAccountPdf(), is(jsonAttachOption.getHectoEncryptedBankAccountPdf()));

            //PdfTestHelper.generatePdfFile(userSignedPdf, expectedPdfFile.getName());
            //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
            assertPdfBytesEqualsToFile(userSignedPdf, expectedPdfFile);
        }

        @Nested
        class WithBankAccountImages {

            @Nested
            class WhenAcceptorNormalUserWatermarkActLogo {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_with_bank_account_images_watermark_act_logo.pdf")
                private File expectedPdfFile;

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                public void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(bankAccountFile1)
                                .file(bankAccountFile2)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    assertResponse(response, userSignedPdf, expectedPdfFile);
                }
            }

            @SuppressWarnings("LineLength")
            @Nested
            class WhenAcceptorNormalUserWatermarkActLogoWithDate {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_with_bank_account_images_watermark_act_logo_with_date.pdf")
                private File expectedPdfFile;

                @BeforeEach
                void setUp() {
                    digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.ACT_LOGO_WITH_DATE);
                    itUtil.updateDigitalDocument(digitalDocument);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                public void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(bankAccountFile1)
                                .file(bankAccountFile2)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    //assertResponse(response, userSignedPdf, expectedPdfFile);
                }
            }

            @SuppressWarnings("LineLength")
            @Nested
            class WhenAcceptorCorporateUserWatermarkActLogo {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_corporate_user_with_bank_account_images_watermark_act_logo.pdf")
                private File expectedPdfFileForCorporateUser;

                @BeforeEach
                void setUp() {
                    final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, corporateName);
                    corporateUser.setUserId(acceptUser.getId());
                    itUtil.updateCorporateUser(corporateUser);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                public void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(bankAccountFile1)
                                .file(bankAccountFile2)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    assertResponse(response, userSignedPdf, expectedPdfFileForCorporateUser);
                }
            }

            @Nested
            class WhenAcceptorCorporateUser {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_corporate_user_with_bank_account_images.pdf")
                private File expectedPdfFileForCorporateUser;

                @BeforeEach
                void setUp() {
                    final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, corporateName);
                    corporateUser.setUserId(acceptUser.getId());
                    itUtil.updateCorporateUser(corporateUser);

                    digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.NONE);
                    itUtil.updateDigitalDocument(digitalDocument);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                public void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(bankAccountFile1)
                                .file(bankAccountFile2)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    assertResponse(response, userSignedPdf, expectedPdfFileForCorporateUser);
                }
            }

            @BeforeEach
            void setUp() throws IOException {
                defaultSetupWithBankAccountImagesRequired();
                setUpEntitiesForJointOwnershipDocumentFill();

                bankAccountFile1 = getBankAccount1Image();
                bankAccountFile2 = getBankAccount2Image();

                digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.ACT_LOGO);
                itUtil.updateDigitalDocument(digitalDocument);
            }

            private MockMultipartFile getBankAccount1Image() throws IOException {
                byte[] bankAccountImageBytes = Files.readAllBytes(bankAccount1Image.getFile().toPath());
                return new MockMultipartFile(
                    "bankAccountImages", "bank-account-1.jpg", MediaType.IMAGE_JPEG_VALUE, bankAccountImageBytes
                );
            }

            private MockMultipartFile getBankAccount2Image() throws IOException {
                byte[] bankAccountImageBytes = Files.readAllBytes(bankAccount2Image.getFile().toPath());
                return new MockMultipartFile(
                    "bankAccountImages", "bank-account-2.jpg", MediaType.IMAGE_JPEG_VALUE, bankAccountImageBytes
                );
            }
        }

        @Nested
        class WithHectoEncryptedPdf {

            @Nested
            class WhenAcceptorNormalUser {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_with_hecto_pdf.pdf")
                private File expectedPdfFile;

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(hectoEncryptedStringFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    assertResponse(response, userSignedPdf, expectedPdfFile);
                }
            }

            @Nested
            class WhenAcceptorCorporateUser {
                @Value("classpath:/digitaldocument/usersignedoutput/joint_ownership_document_corporate_user_with_hecto_pdf.pdf")
                private File expectedPdfFileForCorporateUser;

                @BeforeEach
                void setUp() {
                    final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, corporateName);
                    corporateUser.setUserId(acceptUser.getId());
                    itUtil.updateCorporateUser(corporateUser);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = mockMvc
                        .perform(
                            multipart(TARGET_API, digitalDocument.getId())
                                .file(signatureImageFile)
                                .file(idCardImageFile)
                                .file(hectoEncryptedStringFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwt)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

                    assertResponse(response, userSignedPdf, expectedPdfFileForCorporateUser);
                }
            }

            @BeforeEach
            void setUp() throws IOException {
                defaultSetupWithHectoEncryptedPdfRequired();
                setUpEntitiesForJointOwnershipDocumentFill();

                hectoEncryptedStringFile = getHectoEncryptedStringFile();

                digitalDocument.setIdCardWatermarkType(IdCardWatermarkType.ACT_LOGO);
                itUtil.updateDigitalDocument(digitalDocument);
            }

            private void defaultSetupWithHectoEncryptedPdfRequired() {
                jsonAttachOption = new ag.act.model.JsonAttachOption()
                    .idCardImage(AttachOptionType.REQUIRED.name())
                    .signImage(AttachOptionType.REQUIRED.name())
                    .bankAccountImage(AttachOptionType.NONE.name())
                    .hectoEncryptedBankAccountPdf(AttachOptionType.REQUIRED.name());

                defaultSetup();
            }
        }
    }

    @Nested
    class WhenCreateJointOwnershipInvalidAddress {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            signatureImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                originalIdCardImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename1,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename2,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signatureImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "공동보유약정 위임자의 주소 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateJointOwnerErrorSignImage {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                originalIdCardImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename1,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename2,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateJointOwnerErrorIdCardImage {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            signatureImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename1,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename2,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signatureImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateJointOwnerErrorBankAccountImage {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            signatureImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                originalIdCardImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signatureImageFile)
                        .file(idCardImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateJointOwnerErrorIdCardImageWithHecto {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            signatureImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            hectoEncryptedStringFile = new MockMultipartFile(
                "hectoEncryptedBankAccountPdf",
                originalHectoEncryptedStringFileName,
                MediaType.TEXT_PLAIN_VALUE,
                "test string".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signatureImageFile)
                        .file(hectoEncryptedStringFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateJointOwnerErrorBankAccountImageWithHecto {

        @BeforeEach
        void setUp() {
            defaultSetupWithBankAccountImagesRequired();

            signatureImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                originalIdCardImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signatureImageFile)
                        .file(idCardImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }
}
