package ag.act.api.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Board;
import ag.act.entity.CorporateUser;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.UserVerificationHistory;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.facade.FileFacade;
import ag.act.service.digitaldocument.certification.DigitalDocumentCertificationService;
import ag.act.util.DateTimeFormatUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static ag.act.enums.verification.VerificationOperationType.REGISTER;
import static ag.act.enums.verification.VerificationOperationType.SIGNATURE_SAVE;
import static ag.act.enums.verification.VerificationOperationType.VERIFICATION;
import static ag.act.enums.verification.VerificationType.PIN;
import static ag.act.enums.verification.VerificationType.SIGNATURE;
import static ag.act.enums.verification.VerificationType.SMS;
import static ag.act.itutil.PdfTestHelper.assertPdfTextEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class DigitalDocumentCertificationIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String STOCK_NAME = "삼성전자";
    private static final String GRANTOR_NAME = "위임인";
    private static final LocalDateTime GRANTOR_BIRTHDATE = LocalDate.of(1990, 12, 25).atStartOfDay();
    private static final String ACCEPTOR_NAME = "수임인";
    private static final LocalDateTime ACCEPTOR_BIRTHDATE = LocalDate.of(1989, 1, 25).atStartOfDay();
    private static final LocalDateTime SMS_REGISTER = LocalDate.of(2023, 1, 1).atStartOfDay();
    private static final LocalDateTime PIN_REGISTER = LocalDate.of(2023, 1, 2).atStartOfDay();
    private static final LocalDateTime PIN_VERIFICATION = LocalDate.of(2023, 1, 3).atStartOfDay();
    private static final LocalDateTime SIGNATURE_SIGNATURE_SAVE = LocalDate.of(2023, 1, 4).atStartOfDay();
    private static final LocalDateTime SIGNATURE_SIGNATURE = LocalDate.of(2023, 1, 5).atStartOfDay();
    private static final LocalDateTime DIGITAL_DOC_USER_CREATED_AT = LocalDate.of(2023, 1, 6).atStartOfDay();
    private static final LocalDateTime SHAREHOLDER_MEETING = LocalDate.of(2023, 3, 1).atStartOfDay();
    private static final Long DIGITAL_DOC_USER_ISSUED_NO = 1L;
    private static final String DIGITAL_PROXY_DOC_NO = "ACT-A-1-%s-%s"
        .formatted(DateTimeFormatUtil.yyMMdd().format(DIGITAL_DOC_USER_CREATED_AT), DIGITAL_DOC_USER_ISSUED_NO);

    @Autowired
    private DigitalDocumentCertificationService digitalDocumentCertificationService;
    @MockBean
    private FileFacade fileFacade;
    @Value("classpath:/digitaldocument/certification/digital-proxy-certification.pdf")
    private File expectedPdf;
    @Value("classpath:/digitaldocument/certification/digital-proxy-certification-corporate-user.pdf")
    private File expectedPdfForCorporateUser;
    private User acceptor;
    private User grantor;
    @Captor
    private ArgumentCaptor<byte[]> pdfCaptor;
    private List<MockedStatic<?>> statics;
    private DigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        acceptor = setNameAndBirthdate(itUtil.createAcceptorUser(), ACCEPTOR_NAME, ACCEPTOR_BIRTHDATE);
        grantor = setNameAndBirthdate(itUtil.createUser(), GRANTOR_NAME, GRANTOR_BIRTHDATE);
        statics = List.of(mockStatic(ActUserProvider.class));

        given(ActUserProvider.getNoneNull()).willReturn(grantor);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    private void mockDigitalDocument(DigitalDocumentType digitalDocumentType) {
        stockCode = someStockCode();
        Stock stock = itUtil.createStock(stockCode, STOCK_NAME);
        Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        Post post = itUtil.createPost(board, acceptor.getId());

        itUtil.createStockAcceptorUser(stock.getCode(), acceptor);

        digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptor, SHAREHOLDER_MEETING, digitalDocumentType
        );
        digitalDocumentUser = mockDigitalDocumentUser(digitalDocument, grantor, stock);
        loadDigitalDocument(digitalDocument, digitalDocumentUser);
    }

    private DigitalDocumentUser mockDigitalDocumentUser(DigitalDocument digitalDocument, User user, Stock stock) {
        digitalDocumentUser = itUtil.createDigitalDocumentUser(
            digitalDocument, user, stock, someAlphanumericString(10), DigitalDocumentAnswerStatus.SAVE
        );
        digitalDocumentUser.setOriginalPageCount(0L);
        digitalDocumentUser.setAttachmentPageCount(0L);
        digitalDocumentUser.setCreatedAt(DIGITAL_DOC_USER_CREATED_AT);
        digitalDocumentUser.setIssuedNumber(DIGITAL_DOC_USER_ISSUED_NO);
        return itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }

    private void loadDigitalDocument(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        digitalDocumentUser.setDigitalDocument(digitalDocument);
    }

    private void mockUserHoldingStock() {
        final Long quantity = someLong();
        final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(digitalDocument.getStockCode(), grantor);
        userHoldingStock.setQuantity(quantity);
        itUtil.updateUserHoldingStock(userHoldingStock);
        final UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate = itUtil.createUserHoldingStockOnReferenceDate(
            digitalDocument.getStockCode(), grantor.getId(), digitalDocumentUser.getStockReferenceDate()
        );
        userHoldingStockOnReferenceDate.setQuantity(quantity);
        itUtil.updateUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDate);
    }

    @SuppressWarnings("LineLength")
    private void mockUserVerificationHistories() {
        mockUserVerificationHistoryWithCreatedAt(
            grantor, SMS, REGISTER, SMS_REGISTER
        );
        mockUserVerificationHistoryWithCreatedAt(
            grantor, PIN, REGISTER, PIN_REGISTER
        );
        mockUserVerificationHistoryWithCreatedAt(
            grantor, PIN, VERIFICATION, PIN_VERIFICATION
        );
        mockUserVerificationHistoryWithCreatedAt(
            grantor, SIGNATURE, SIGNATURE_SAVE, SIGNATURE_SIGNATURE_SAVE, digitalDocumentUser.getId()
        );
        mockUserVerificationHistoryWithCreatedAt(
            grantor, SIGNATURE, VerificationOperationType.SIGNATURE, SIGNATURE_SIGNATURE, digitalDocumentUser.getId()
        );
    }

    private User setNameAndBirthdate(
        User user,
        String name,
        LocalDateTime birthDate
    ) {
        user.setName(name);
        user.setBirthDate(birthDate);

        return itUtil.updateUser(user);
    }

    private void mockUserVerificationHistoryWithCreatedAt(
        User user,
        VerificationType verificationType,
        VerificationOperationType operationType,
        LocalDateTime createdAt
    ) {
        mockUserVerificationHistoryWithCreatedAt(
            user, verificationType, operationType, createdAt, null
        );
    }

    private void mockUserVerificationHistoryWithCreatedAt(
        User user,
        VerificationType verificationType,
        VerificationOperationType operationType,
        LocalDateTime createdAt,
        Long digitalDocumentUserId
    ) {
        UserVerificationHistory userVerificationHistory = itUtil.createUserVerificationHistory(
            user, verificationType, operationType, digitalDocumentUserId
        );
        userVerificationHistory.setCreatedAt(createdAt);
        itUtil.updateUserVerificationHistory(userVerificationHistory);
    }

    @Nested
    class WhenDigitalProxy {

        @Nested
        class WhenAcceptorNormalUser {
            @BeforeEach
            void setUp() {
                mockDigitalDocument(DigitalDocumentType.DIGITAL_PROXY);
                mockUserHoldingStock();
                mockUserVerificationHistories();
                given(digitalDocumentNumberGenerator.generate(
                    DigitalDocumentType.DIGITAL_PROXY, digitalDocument.getId(), digitalDocumentUser.getIssuedNumber())
                ).willReturn(DIGITAL_PROXY_DOC_NO);
            }

            @Test
            void shouldGeneratePdf() {
                // When
                digitalDocumentCertificationService.generate(
                    digitalDocument, digitalDocumentUser
                );

                // Then
                then(fileFacade).should().uploadDigitalDocumentCertification(
                    pdfCaptor.capture(), eq(digitalDocument), eq(digitalDocumentUser.getUserId())
                );

                // PDF 내용이 바뀌면 아래 주석 풀고 테스트 한 번 실행해 생성된 원본 pdf 저장
                //PdfTestHelper.generatePdfFile(pdfCaptor.getValue(), "digital-proxy-certification.pdf");
                assertPdfTextEquals(pdfCaptor.getValue(), expectedPdf);
            }
        }

        @Nested
        class WhenAcceptorCorporateUser {

            @BeforeEach
            void setUp() {
                mockDigitalDocument(DigitalDocumentType.DIGITAL_PROXY);
                mockUserHoldingStock();
                mockUserVerificationHistories();
                final String corporateNo = "123456-1234567";
                final String corporateName = "테스트법인";
                final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, corporateName);
                corporateUser.setUserId(acceptor.getId());
                itUtil.updateCorporateUser(corporateUser);

                acceptor.setName(corporateName);
                itUtil.updateUser(acceptor);

                final StockAcceptorUserHistory stockAcceptorUserHistory = itUtil.findStockAcceptorUserHistory(stockCode, acceptor.getId())
                    .orElseThrow();
                stockAcceptorUserHistory.setName(corporateName);
                itUtil.updateStockAcceptorUserHistory(stockAcceptorUserHistory);

                given(digitalDocumentNumberGenerator.generate(
                    DigitalDocumentType.DIGITAL_PROXY,
                    digitalDocument.getId(),
                    digitalDocumentUser.getIssuedNumber()
                )).willReturn(DIGITAL_PROXY_DOC_NO);
            }

            @Test
            void shouldGeneratePdf() {
                // When
                digitalDocumentCertificationService.generate(
                    digitalDocument, digitalDocumentUser
                );

                // Then
                then(fileFacade).should().uploadDigitalDocumentCertification(
                    pdfCaptor.capture(), eq(digitalDocument), eq(digitalDocumentUser.getUserId())
                );

                // PDF 내용이 바뀌면 아래 주석 풀고 테스트 한 번 실행해 생성된 원본 pdf 저장
                //PdfTestHelper.generatePdfFile(pdfCaptor.getValue(), "digital-proxy-certification-corporate-user.pdf");
                assertPdfTextEquals(pdfCaptor.getValue(), expectedPdfForCorporateUser);
            }
        }
    }

    @Nested
    class WhenJointOwnership {
        @BeforeEach
        void setUp() {
            mockDigitalDocument(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
        }

        @Test
        void shouldDoNothing() {
            // When
            digitalDocumentCertificationService.generate(
                digitalDocument, digitalDocumentUser
            );

            // Then
            then(fileFacade).should(never()).uploadDigitalDocumentCertification(any(), any(), any());
        }
    }

    @Nested
    class WhenEtc {
        @BeforeEach
        void setUp() {
            mockDigitalDocument(DigitalDocumentType.ETC_DOCUMENT);
        }

        @Test
        void shouldDoNothing() {
            // When
            digitalDocumentCertificationService.generate(
                digitalDocument, digitalDocumentUser
            );

            // Then
            then(fileFacade).should(never()).uploadDigitalDocumentCertification(any(), any(), any());
        }
    }
}
