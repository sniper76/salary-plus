package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.facade.stock.StockFacade;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.repository.interfaces.DigitalDocumentUserSummary;
import ag.act.service.aws.S3Service;
import ag.act.service.digitaldocument.DigitalDocumentAcceptUserService;
import ag.act.service.digitaldocument.DigitalDocumentUserProcessService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.service.digitaldocument.answer.DigitalDocumentItemUserAnswerService;
import ag.act.service.digitaldocument.certification.DigitalDocumentCertificationService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.validator.document.DigitalDocumentValidator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.enums.DigitalDocumentAnswerStatus.SAVE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentUserServiceTest {
    @InjectMocks
    private DigitalDocumentUserService service;

    @Mock
    private DigitalDocumentValidator digitalDocumentValidator;
    @Mock
    private StockFacade stockFacade;
    @Mock
    private DigitalDocumentUserProcessService digitalDocumentUserDownloadService;
    @Mock
    private DigitalDocumentItemUserAnswerService digitalDocumentItemUserAnswerService;
    @Mock
    private PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    @Mock
    private MyDataSummaryService myDataSummaryService;
    @Mock
    private DigitalDocumentUserRepository digitalDocumentUserRepository;
    @Mock
    private DigitalDocumentRepository digitalDocumentRepository;
    @Mock
    private UserVerificationHistoryService userVerificationHistoryService;
    @Mock
    private S3Service s3Service;
    @Mock
    private DigitalDocumentAcceptUserService digitalDocumentAcceptUserService;
    @Mock
    private DigitalDocumentCertificationService digitalDocumentCertificationService;
    @Mock
    private DigitalDocument digitalDocument;
    @Mock
    private DigitalDocumentUser digitalDocumentUser;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    @Mock
    private Stock stock;
    @Mock
    private User user;
    @Mock
    private User acceptUser;
    @Mock
    private ag.act.model.UserDigitalDocumentResponse userDigitalDocumentResponse;
    @Mock
    private MultipartFile signFile;
    @Mock
    private MultipartFile idCardFile;
    @Mock
    private List<MultipartFile> bandAccountFiles;
    @Mock
    private MultipartFile hectoEncryptedBankAccountPdf;
    @Mock
    private DigitalDocumentUserSummary digitalDocumentUserSummary;
    @Mock
    private Post post;

    private List<MockedStatic<?>> statics;

    private Long digitalDocumentId;
    private Long userId;
    private String stockCode;
    private String answerData;
    private LocalDate referenceDate;
    private Long digitalDocumentUserId;

    @BeforeEach
    void setUp() {
        statics = List.of(
            mockStatic(ActUserProvider.class),
            mockStatic(DecimalFormatUtil.class),
            mockStatic(FilenameUtil.class)
        );

        digitalDocumentId = someLong();

        given(digitalDocumentUserRepository.findDigitalDocumentUserSummary(digitalDocumentId))
            .willReturn(digitalDocumentUserSummary);
        given(digitalDocumentUserSummary.getCountOfUser()).willReturn(someIntegerBetween(1, 2));
        given(digitalDocumentUserSummary.getSumOfStockCount()).willReturn(someLongBetween(100L, 2000L));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreateUserDigitalDocument {
        private DigitalDocumentType type;
        private double referenceRatio = 0;
        private long stockSum = 0L;
        private LocalDateTime targetEndDate;
        private Long referenceDateStockCount;
        private Long acceptUserId;

        @BeforeEach
        void setUp() {
            userId = someLong();
            acceptUserId = someLong();
            stockCode = someString(5);
            answerData = "1:ABSTENTION,2:REJECTION,3:APPROVAL";
            referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

            referenceRatio = someLong().doubleValue();
            referenceDateStockCount = someLong();
            stockSum = someLong();
            type = DigitalDocumentType.DIGITAL_PROXY;
            targetEndDate = LocalDateTime.now();
        }

        @Test
        void shouldSuccess() {
            // Given
            final Long totalIssuedQuantity = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);

            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.of(digitalDocument));
            given(digitalDocumentRepository.save(digitalDocument)).willReturn(digitalDocument);
            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(digitalDocument.getTargetEndDate()).willReturn(targetEndDate);
            given(digitalDocument.getType()).willReturn(type);
            given(digitalDocument.getStockCode()).willReturn(stockCode);
            given(digitalDocument.getJoinStockSum()).willReturn(stockSum);
            given(digitalDocument.getAcceptUserId()).willReturn(acceptUserId);
            given(stockFacade.findByCode(stockCode)).willReturn(stock);
            given(stock.getTotalIssuedQuantity()).willReturn(totalIssuedQuantity);
            given(DecimalFormatUtil.twoDecimalPlaceDouble(stockSum * 100.0 / totalIssuedQuantity))
                .willReturn(referenceRatio);

            willDoNothing().given(digitalDocumentItemUserAnswerService).saveAnswers(answerData, userId, digitalDocument);

            given(digitalDocumentUserDownloadService.createDigitalDocumentUserWithGeneratedPdf(
                digitalDocument, user, signFile, idCardFile, bandAccountFiles, hectoEncryptedBankAccountPdf)).willReturn(digitalDocumentUser);

            given(digitalDocument.getStockReferenceDate()).willReturn(referenceDate);
            given(digitalDocumentUser.getStockCount()).willReturn(referenceDateStockCount);

            given(digitalDocumentAcceptUserService.getAcceptUser(digitalDocument)).willReturn(acceptUser);
            given(postResponseDigitalDocumentConverter.convert(
                digitalDocument,
                digitalDocumentUser,
                stock,
                user,
                acceptUser
            )).willReturn(userDigitalDocumentResponse);

            // When
            ag.act.model.UserDigitalDocumentResponse actual = service.createUserDigitalDocumentWithImage(
                signFile, idCardFile, bandAccountFiles, hectoEncryptedBankAccountPdf, answerData, digitalDocumentId
            );

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual, is(userDigitalDocumentResponse));
        }
    }

    @Nested
    class WhenUpdateUserDigitalDocumentStatus {
        private LocalDateTime targetEndDate;
        private String pdfPath;

        @Mock
        private MyDataSummary myDataSummary;
        @Mock
        private UserHoldingStock userHoldingStock;
        @Mock
        private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someString(5);
            targetEndDate = LocalDateTime.now();
            pdfPath = someString(10);
        }

        @Test
        void shouldSuccess() {
            // Given
            final Long totalIssuedQuantity = someLong();
            final Long stockSum = someLong();
            final double referenceRatio = someLong().doubleValue();
            final String stockName = someString(5);
            final String postTitle = someString(5);

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(digitalDocument.getPost()).willReturn(post);
            given(post.getTitle()).willReturn(postTitle);
            given(digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId))
                .willReturn(Optional.of(digitalDocumentUser));

            given(digitalDocumentUser.getPdfPath()).willReturn(pdfPath);
            given(digitalDocument.getStockCode()).willReturn(stockCode);
            given(digitalDocument.getJoinStockSum()).willReturn(stockSum);
            given(stockFacade.findByCode(stockCode)).willReturn(stock);
            given(stock.getTotalIssuedQuantity()).willReturn(totalIssuedQuantity);
            given(stock.getName()).willReturn(stockName);
            given(DecimalFormatUtil.twoDecimalPlaceDouble(stockSum * 100.0 / totalIssuedQuantity))
                .willReturn(referenceRatio);

            given(myDataSummaryService.getByUserId(userId)).willReturn(myDataSummary);

            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.of(digitalDocument));
            given(digitalDocument.getTargetEndDate()).willReturn(targetEndDate);
            willDoNothing().given(digitalDocumentValidator).validateTargetEndDate(targetEndDate, "제출");
            given(digitalDocumentUserRepository.save(digitalDocumentUser)).willReturn(digitalDocumentUser);
            given(digitalDocumentUserRepository.findDigitalDocumentUserSummary(digitalDocumentId))
                .willReturn(digitalDocumentUserSummary);
            given(digitalDocumentUserSummary.getCountOfUser()).willReturn(someIntegerBetween(1, 2));
            given(digitalDocumentUserSummary.getSumOfStockCount()).willReturn(someLongBetween(100L, 2000L));

            given(userHoldingStockService.getUserHoldingStock(userId, stockCode))
                .willReturn(userHoldingStock);
            given(userHoldingStock.getQuantity()).willReturn(someLongBetween(1L, 10L));
            given(userHoldingStockOnReferenceDateService.getUserHoldingStockOnReferenceDate(
                userId, stockCode, referenceDate
            )).willReturn(Optional.of(userHoldingStockOnReferenceDate));
            given(userHoldingStockOnReferenceDate.getQuantity()).willReturn(someLongBetween(1L, 10L));

            // When
            ag.act.model.SimpleStringResponse actual = service.updateUserDigitalDocumentStatus(
                digitalDocumentId
            );

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual.getStatus(), is("ok"));
            then(userVerificationHistoryService).should().create(
                userId, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, digitalDocumentUser.getId()
            );
            then(digitalDocumentCertificationService).should().generate(
                digitalDocument,
                digitalDocumentUser
            );
        }
    }

    @Nested
    class WhenUpdateUserDigitalDocumentDelete {
        private LocalDateTime targetEndDate;
        private String pdfPath;
        private String certificationPdfPath;
        private LocalDateTime updatedAt;
        private String postTitle;
        private String stockName;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someString(5);
            targetEndDate = LocalDateTime.now();
            pdfPath = someString(10);
            certificationPdfPath = someString(10);
            updatedAt = LocalDateTime.now();
            postTitle = someString(5);
            stockName = someString(5);
        }

        @Test
        void shouldSuccess() {
            // Given
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId))
                .willReturn(Optional.of(digitalDocumentUser));
            given(digitalDocument.getStockCode()).willReturn(stockCode);
            given(stock.getName()).willReturn(stockName);
            given(digitalDocument.getPost()).willReturn(post);
            given(post.getTitle()).willReturn(postTitle);
            given(stockFacade.findByCode(stockCode)).willReturn(stock);
            given(digitalDocumentUser.getPdfPath()).willReturn(pdfPath);
            given(FilenameUtil.getDigitalDocumentCertificationFilename(any())).willReturn(certificationPdfPath);
            given(digitalDocumentUser.getUpdatedAt()).willReturn(updatedAt);
            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.of(digitalDocument));
            given(digitalDocument.getTargetEndDate()).willReturn(targetEndDate);
            willDoNothing().given(digitalDocumentValidator).validateTargetEndDate(targetEndDate, "삭제");

            mockFileUtil(pdfPath, certificationPdfPath);


            given(digitalDocumentRepository.save(digitalDocument)).willReturn(digitalDocument);
            willDoNothing().given(digitalDocumentUserRepository).delete(digitalDocumentUser);

            // When
            ag.act.model.SimpleStringResponse actual = service.deleteUserDigitalDocument(
                digitalDocumentId
            );

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual.getStatus(), is("ok"));
        }
    }

    @Nested
    class WhenGetUserDigitalDocumentPdf {
        private String pdfPath;
        private InputStream stubInputStream;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someString(5);
            pdfPath = someString(10);
            stubInputStream =
                IOUtils.toInputStream("some test data for my input stream", "UTF-8");
        }

        @Test
        void shouldSuccess() {
            // Given
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId))
                .willReturn(Optional.of(digitalDocumentUser));

            given(digitalDocumentUser.getPdfPath()).willReturn(pdfPath);
            given(s3Service.readObject(pdfPath)).willReturn(stubInputStream);

            // When
            DownloadFile actual = service.getUserDigitalDocumentPdf(
                digitalDocumentId
            );

            // Then
            assertThat(actual, notNullValue());
        }
    }

    @Nested
    class WhenGetUserDigitalDocumentPdfByAdmin {
        private String pdfPath;
        private InputStream stubInputStream;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someString(5);
            pdfPath = someString(10);
            stubInputStream =
                IOUtils.toInputStream("some test data for my input stream", "UTF-8");
        }

        @Test
        void shouldSuccess() {
            // Given
            given(digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId))
                .willReturn(Optional.of(digitalDocumentUser));

            given(digitalDocumentUser.getPdfPath()).willReturn(pdfPath);
            given(s3Service.readObject(pdfPath)).willReturn(stubInputStream);

            // When
            DownloadFile actual = service.getUserDigitalDocumentPdf(digitalDocumentId, userId);

            // Then
            assertThat(actual, notNullValue());
        }
    }

    @Nested
    class DeleteUserDigitalDocument {

        @Mock
        private DigitalDocumentUser digitalDocumentUser;
        private String pdfPath;
        private String certificationPdfPath;
        private Long userId;

        @BeforeEach
        void setUp() {
            pdfPath = someAlphanumericString(10).trim();
            certificationPdfPath = someString(10).trim();
            userId = someLong();
            digitalDocumentUserId = someLong();
            final Long totalIssuedQuantity = someLong();
            final Long stockSum = someLong();
            final double referenceRatio = someLong().doubleValue();
            final LocalDateTime updatedAt = LocalDateTime.now();
            final LocalDateTime userBirthDate = LocalDateTime.now().minusYears(50);
            final String postTitle = someString(5);
            final String stockName = someString(5);

            given(digitalDocument.getStockCode()).willReturn(stockCode);
            given(digitalDocument.getJoinStockSum()).willReturn(stockSum);
            given(stockFacade.findByCode(stockCode)).willReturn(stock);
            given(stock.getTotalIssuedQuantity()).willReturn(totalIssuedQuantity);
            given(stock.getName()).willReturn(stockName);
            given(DecimalFormatUtil.twoDecimalPlaceDouble(stockSum * 100.0 / totalIssuedQuantity))
                .willReturn(referenceRatio);

            given(digitalDocumentUser.getId()).willReturn(digitalDocumentUserId);
            given(digitalDocumentUser.getUserId()).willReturn(userId);
            given(digitalDocumentUser.getPdfPath()).willReturn(pdfPath);
            given(digitalDocumentUser.getDigitalDocumentId()).willReturn(digitalDocumentId);
            given(digitalDocumentUser.getBirthDate()).willReturn(userBirthDate);
            given(digitalDocumentUser.getUpdatedAt()).willReturn(updatedAt);
            given(digitalDocument.getPost()).willReturn(post);
            given(digitalDocument.getId()).willReturn(digitalDocumentId);
            given(post.getTitle()).willReturn(postTitle);

            mockFileUtil(pdfPath, certificationPdfPath);

            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.of(digitalDocument));
            willDoNothing().given(digitalDocumentItemUserAnswerService).deleteByDigitalDocumentUserId(digitalDocumentId, userId);
            willDoNothing().given(digitalDocumentUserRepository).delete(digitalDocumentUser);

            given(s3Service.removeObjectInRetry(pdfPath)).willReturn(true);
            given(s3Service.removeObjectInRetry(certificationPdfPath)).willReturn(true);
        }

        @Nested
        class WhenDigitalDocumentAnswerStatusIsComplete extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(digitalDocumentUser.getDigitalDocumentAnswerStatus()).willReturn(DigitalDocumentAnswerStatus.COMPLETE);
                given(digitalDocumentRepository.save(digitalDocument)).willReturn(digitalDocument);

                service.deleteUserDigitalDocument(digitalDocumentUser);
            }
        }

        @Nested
        class WhenDigitalDocumentAnswerStatusIsSave extends DefaultTestCases {
            @BeforeEach
            void setUp() {

                given(digitalDocumentUser.getDigitalDocumentAnswerStatus()).willReturn(DigitalDocumentAnswerStatus.SAVE);

                service.deleteUserDigitalDocument(digitalDocumentUser);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldCallS3Service() {
                then(s3Service).should().removeObjectInRetry(pdfPath);
                then(s3Service).should().removeObjectInRetry(certificationPdfPath);
            }

            @Test
            void shouldCallDigitalDocumentItemUserAnswerService() {
                then(digitalDocumentItemUserAnswerService).should().deleteByDigitalDocumentUserId(digitalDocumentId, userId);
            }

            @Test
            void shouldCallDigitalDocumentUserRepository() {
                then(digitalDocumentUserRepository).should().deleteById(digitalDocumentUserId);
            }
        }
    }

    private void mockFileUtil(
        String pdfPath,
        String certificationPdfPath
    ) {
        final String digitalDocumentFilename = someString(6);
        final String certificationFilename = someString(6);

        given(FilenameUtil.getDigitalDocumentCertificationFilename(any(), any())).willReturn(certificationFilename);
        given(FilenameUtil.getDigitalDocumentUploadPath(digitalDocumentId, digitalDocumentFilename)).willReturn(pdfPath);
        given(FilenameUtil.getDigitalDocumentUploadPath(digitalDocumentId, certificationFilename)).willReturn(certificationPdfPath);
    }

    @Nested
    class GetUnfinishedDigitalDocumentUsersForCleanup {

        private List<DigitalDocumentUser> expectedDigitalDocumentUsers;
        private List<DigitalDocumentUser> actualDigitalDocumentUsers;
        private List<DigitalDocumentAnswerStatus> statues;
        @Mock
        private DigitalDocumentUser digitalDocumentUser1;
        @Mock
        private DigitalDocumentUser digitalDocumentUser2;
        @Mock
        private DigitalDocumentUser digitalDocumentUser3New;

        @BeforeEach
        void setUp() {
            final LocalDateTime targetUpdatedAt = LocalDateTime.now().minusHours(1);
            final LocalDateTime latestUpdatedAt = LocalDateTime.now().minusMinutes(someIntegerBetween(1, 23));
            expectedDigitalDocumentUsers = List.of(digitalDocumentUser1, digitalDocumentUser2);
            statues = List.of(SAVE);

            given(digitalDocumentUser1.getUpdatedAt()).willReturn(targetUpdatedAt);
            given(digitalDocumentUser2.getUpdatedAt()).willReturn(targetUpdatedAt);
            given(digitalDocumentUser3New.getUpdatedAt()).willReturn(latestUpdatedAt);
            given(digitalDocumentUserRepository.findAllByDigitalDocumentAnswerStatusIn(statues))
                .willReturn(List.of(digitalDocumentUser1, digitalDocumentUser2, digitalDocumentUser3New));

            actualDigitalDocumentUsers = service.getUnfinishedDigitalDocumentUsersForCleanup();
        }

        @Test
        void shouldReturnDigitalDocumentUsers() {
            assertThat(actualDigitalDocumentUsers, is(expectedDigitalDocumentUsers));
        }

        @Test
        void shouldCallDigitalDocumentUserRepository() {
            then(digitalDocumentUserRepository).should().findAllByDigitalDocumentAnswerStatusIn(statues);
        }
    }

    @Nested
    class DeleteAllUnfinishedUserDigitalDocuments {

        @Spy
        @InjectMocks
        private DigitalDocumentUserService spyService;
        @Mock
        private DigitalDocumentUser digitalDocumentUser;

        @BeforeEach
        void setUp() {
            digitalDocumentId = someLong();

            willReturn(List.of(digitalDocumentUser))
                .given(spyService).getDigitalDocumentUsersInSaveStatus(digitalDocumentId);
            willDoNothing()
                .given(spyService).deleteUserDigitalDocument(digitalDocumentUser);

            spyService.deleteAllUnfinishedUserDigitalDocuments(digitalDocumentId);
        }

        @Test
        void shouldGetDigitalDocumentUsersInSaveStatus() {
            then(spyService).should().getDigitalDocumentUsersInSaveStatus(digitalDocumentId);
        }

        @Test
        void shouldCallDeleteUserDigitalDocument() {
            then(spyService).should().deleteUserDigitalDocument(digitalDocumentUser);
        }
    }
}
