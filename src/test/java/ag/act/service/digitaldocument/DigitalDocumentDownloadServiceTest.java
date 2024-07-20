package ag.act.service.digitaldocument;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.digitaldocument.DigitalDocumentZipFileRequestConverter;
import ag.act.core.infra.LambdaEnvironment;
import ag.act.dto.DigitalDocumentZipFileRequest;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.repository.DigitalDocumentDownloadRepository;
import ag.act.service.aws.LambdaService;
import ag.act.service.aws.S3Service;
import ag.act.service.download.csv.DigitalDocumentCsvDownloadService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockService;
import ag.act.util.ObjectMapperUtil;
import ag.act.util.UUIDUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("unused")
@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentDownloadServiceTest {

    private List<MockedStatic<?>> statics;
    @InjectMocks
    private DigitalDocumentDownloadService service;
    @Mock
    private DigitalDocumentDownloadRepository digitalDocumentDownloadRepository;
    @Mock
    private LambdaService lambdaService;
    @Mock
    private LambdaEnvironment lambdaEnvironment;
    @Mock
    private ObjectMapperUtil objectMapperUtil;
    @Mock
    private DigitalDocumentZipFileRequestConverter digitalDocumentZipFileRequestConverter;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private DigitalDocumentUserService digitalDocumentUserService;
    @Mock
    private StockService stockService;
    @Mock
    private PostService postService;
    @Mock
    private S3Service s3Service;
    @Mock
    private DigitalDocumentCsvDownloadService digitalDocumentCsvDownloadService;
    @Mock
    private User user;
    @Mock
    private InputStream inputStream;
    private String zipFilesLambdaName;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        zipFilesLambdaName = someString(5);

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(someLong());
        given(s3Service.readObject(anyString())).willReturn(inputStream);
        given(lambdaEnvironment.getZipFilesLambdaName()).willReturn(zipFilesLambdaName);

    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class CreateDigitalDocumentZipFile {
        private Long digitalDocumentId;
        @Mock
        private DigitalDocumentDownload digitalDocumentDownload;

        @BeforeEach
        void setUp() {
            digitalDocumentId = someLong();

            // Given
            given(digitalDocumentDownloadRepository.findAllByDigitalDocumentId(digitalDocumentId))
                .willReturn(List.of());
            given(digitalDocumentDownloadRepository.save(any(DigitalDocumentDownload.class)))
                .willReturn(digitalDocumentDownload);
        }

        @Nested
        class WhenNotFoundExistingDigitalDocumentDownloads {

            @BeforeEach
            void setUp() {
                service.createDigitalDocumentDownload(digitalDocumentId);
            }

            @Test
            void shouldCreateDigitalDocumentZipFile() {
                then(digitalDocumentDownloadRepository).should().save(any(DigitalDocumentDownload.class));
            }

            @Test
            void shouldCallFindAllByDigitalDocumentId() {
                then(digitalDocumentDownloadRepository).should().findAllByDigitalDocumentId(digitalDocumentId);
            }
        }

        @Nested
        class WhenAlreadyHaveDigitalDocumentDownloads {

            @Mock
            private DigitalDocumentDownload existingDigitalDocumentDownload;

            @BeforeEach
            void setUp() {
                // Given
                given(digitalDocumentDownloadRepository.findAllByDigitalDocumentId(digitalDocumentId))
                    .willReturn(List.of(existingDigitalDocumentDownload));

                service.createDigitalDocumentDownload(digitalDocumentId);
            }

            @Test
            void shouldSetIsLatestFalse() {
                then(existingDigitalDocumentDownload).should().setIsLatest(false);
            }

            @Test
            void shouldCreateDigitalDocumentZipFile() {
                then(digitalDocumentDownloadRepository).should().save(any(DigitalDocumentDownload.class));
            }

            @Test
            void shouldCallFindAllByDigitalDocumentId() {
                then(digitalDocumentDownloadRepository).should().findAllByDigitalDocumentId(digitalDocumentId);
            }
        }
    }

    @Nested
    class InvokeZipFilesLambda {

        @Mock
        private DigitalDocument digitalDocument;
        @Mock
        private Stock stock;
        @Mock
        private Post post;
        @Mock
        private DigitalDocumentZipFileRequest digitalDocumentZipFileRequest;
        private String requestBody;
        private Long digitalDocumentId;
        private Long digitalDocumentDownloadId;
        private String stockCode;
        private Long postId;
        private String password;

        @BeforeEach
        void setUp() {
            final String stockName = someString(10);
            final String postTitle = someString(15);
            stockCode = someStockCode();
            postId = someLong();
            digitalDocumentId = someLong();
            digitalDocumentDownloadId = someLong();
            requestBody = someString(5);

            given(digitalDocumentService.getDigitalDocument(digitalDocumentId)).willReturn(digitalDocument);
            given(digitalDocument.getStockCode()).willReturn(stockCode);
            given(digitalDocument.getPostId()).willReturn(postId);
            given(objectMapperUtil.toRequestBody(digitalDocumentZipFileRequest)).willReturn(requestBody);

            willDoNothing().given(lambdaService).invokeLambdaAsync(zipFilesLambdaName, requestBody);
            given(stockService.findByCode(stockCode)).willReturn(Optional.of(stock));
            given(postService.findById(digitalDocument.getPostId())).willReturn(Optional.of(post));
            given(stock.getName()).willReturn(stockName);
            given(post.getTitle()).willReturn(postTitle);
        }

        @Nested
        class WhenInvokeZipFilesLambdaSuccess {

            @Nested
            class AndIsSecured extends DefaultTestCases {

                @BeforeEach
                void setUp() {
                    password = stockCode;

                    given(digitalDocumentZipFileRequestConverter.convert(
                        eq(digitalDocumentId), eq(digitalDocumentDownloadId), eq(password), anyString()
                    )).willReturn(digitalDocumentZipFileRequest);

                    service.invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownloadId, Boolean.TRUE);
                }
            }

            @Nested
            class AndIsNotSecured extends DefaultTestCases {

                @BeforeEach
                void setUp() {
                    password = null;

                    given(digitalDocumentZipFileRequestConverter.convert(
                        eq(digitalDocumentId), eq(digitalDocumentDownloadId), eq(password), anyString()
                    )).willReturn(digitalDocumentZipFileRequest);

                    service.invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownloadId, Boolean.FALSE);
                }
            }

            @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
            class DefaultTestCases {

                @Test
                void shouldInvokeLambdaAsync() {
                    then(lambdaService).should().invokeLambdaAsync(zipFilesLambdaName, requestBody);
                }

                @Test
                void shouldConvertDigitalDocumentZipFileRequest() {
                    then(digitalDocumentZipFileRequestConverter).should().convert(
                        eq(digitalDocumentId), eq(digitalDocumentDownloadId), eq(password), anyString()
                    );
                }

                @Test
                void shouldCallToRequestBody() {
                    then(objectMapperUtil).should().toRequestBody(any(DigitalDocumentZipFileRequest.class));
                }

                @Test
                void shouldCallGetStock() {
                    then(stockService).should().findByCode(stockCode);
                }

                @Test
                void shouldCallGetPost() {
                    then(postService).should().findById(postId);
                }
            }
        }

        @Nested
        class WhenNotFoundDigitalDocument {

            @BeforeEach
            void setUp() {
                willThrow(RuntimeException.class).given(digitalDocumentService).getDigitalDocument(digitalDocumentId);

                try {
                    service.invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownloadId, someBoolean());
                    Assertions.fail("should throw exception");
                } catch (RuntimeException e) {
                    // ignore
                } catch (Exception e) {
                    Assertions.fail(e.getMessage());
                }
            }

            @Test
            void shouldNotInvokeLambdaAsync() {
                then(lambdaService).shouldHaveNoInteractions();
            }

            @Test
            void shouldNotCallToRequestBody() {
                then(objectMapperUtil).shouldHaveNoInteractions();
            }

            @Test
            void shouldNotCallGetPrivateBucketName() {
                then(digitalDocumentZipFileRequestConverter).shouldHaveNoInteractions();
            }

            @Test
            void shouldNotCallGetStockService() {
                then(stockService).shouldHaveNoInteractions();
            }

            @Test
            void shouldNotCallGetPostService() {
                then(postService).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    class UpdateDigitalDocumentZipFileInProgress {
        @Mock
        private DigitalDocumentDownload digitalDocumentDownload;
        private Long digitalDocumentDownloadId;
        private DigitalDocumentDownload actual;

        @BeforeEach
        void setUp() {
            digitalDocumentDownloadId = someLong();
            given(digitalDocumentDownloadRepository.findById(digitalDocumentDownloadId))
                .willReturn(Optional.of(digitalDocumentDownload));
            given(digitalDocumentDownloadRepository.save(digitalDocumentDownload))
                .willReturn(digitalDocumentDownload);

            actual = service.updateDigitalDocumentZipFileInProgress(digitalDocumentDownloadId);
        }

        @Test
        void shouldReturnDigitalDocumentDownload() {
            assertThat(actual, is(digitalDocumentDownload));
        }

        @Test
        void shouldCallFindById() {
            then(digitalDocumentDownloadRepository).should().findById(digitalDocumentDownloadId);
        }

        @Test
        void shouldCallSave() {
            then(digitalDocumentDownloadRepository).should().save(digitalDocumentDownload);
        }

        @Test
        void shouldSetZipFileStatusInProgress() {
            then(digitalDocumentDownload).should().setZipFileStatus(ZipFileStatus.IN_PROGRESS);
        }
    }

    @Nested
    class CompleteDigitalDocumentDownloadZipFile {

        private List<MockedStatic<?>> statics;
        @Mock
        private DigitalDocumentDownload digitalDocumentDownload;
        @Mock
        private UUID uuid;
        private Long digitalDocumentDownloadId;
        private String zipFilePath;
        private DigitalDocumentDownload actual;
        private String uuidString;

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(UUIDUtil.class));
            digitalDocumentDownloadId = someLong();
            zipFilePath = someString(10);
            uuidString = someString(20);

            given(UUIDUtil.randomUUID()).willReturn(uuid);
            given(uuid.toString()).willReturn(uuidString);
            given(digitalDocumentDownloadRepository.findById(digitalDocumentDownloadId))
                .willReturn(Optional.of(digitalDocumentDownload));
            given(digitalDocumentDownloadRepository.save(digitalDocumentDownload))
                .willReturn(digitalDocumentDownload);

            actual = service.completeDigitalDocumentDownloadZipFile(digitalDocumentDownloadId, zipFilePath);
        }

        @Test
        void shouldReturnDigitalDocumentDownload() {
            assertThat(actual, is(digitalDocumentDownload));
        }

        @Test
        void shouldCallFindById() {
            then(digitalDocumentDownloadRepository).should().findById(digitalDocumentDownloadId);
        }

        @Test
        void shouldCallSave() {
            then(digitalDocumentDownloadRepository).should().save(digitalDocumentDownload);
        }

        @Test
        void shouldSetZipFileStatusInProgress() {
            then(digitalDocumentDownload).should().setZipFileStatus(ZipFileStatus.COMPLETE);
        }

        @Test
        void shouldSetZipFilePath() {
            then(digitalDocumentDownload).should().setZipFilePath(zipFilePath);
        }

        @Test
        void shouldSetZipFileKey() {
            then(digitalDocumentDownload).should().setZipFileKey(uuidString);
        }
    }

    @Nested
    class IsDigitalDocumentFinished {

        private Long digitalDocumentId;

        @BeforeEach
        void setUp() {
            digitalDocumentId = someLong();
        }

        @Nested
        class WhenFinished {
            @Test
            void shouldReturnTrue() {
                // Given
                given(digitalDocumentService.isFinished(digitalDocumentId)).willReturn(true);

                // When
                boolean actual = service.isDigitalDocumentFinished(digitalDocumentId);

                // Then
                assertThat(actual, is(true));
            }
        }

        @Nested
        class WhenNotFinished {
            @Test
            void shouldReturnFalse() {
                // Given
                given(digitalDocumentService.isFinished(digitalDocumentId)).willReturn(false);

                // When
                boolean actual = service.isDigitalDocumentFinished(digitalDocumentId);

                // Then
                assertThat(actual, is(false));
            }
        }
    }

    @Nested
    class CleanupAllUnfinishedUserDigitalDocuments {
        @BeforeEach
        void setUp() {
            Long digitalDocumentId = someLong();

            willDoNothing().given(digitalDocumentUserService).deleteAllUnfinishedUserDigitalDocuments(digitalDocumentId);

            service.cleanupAllUnfinishedUserDigitalDocuments(digitalDocumentId);
        }

        @Test
        void shouldCallDeleteAllUnfinishedUserDigitalDocuments() {
            then(digitalDocumentUserService).should().deleteAllUnfinishedUserDigitalDocuments(any(Long.class));
        }
    }
}
