package ag.act.service.admin;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.documenttype.DigitalDocumentTypeServiceResolver;
import ag.act.service.digitaldocument.documenttype.DigitalProxyService;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.validator.document.DigitalDocumentValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentServiceTest {
    @InjectMocks
    private DigitalDocumentService service;
    @Mock
    private DigitalDocumentTypeServiceResolver digitalDocumentTypeServiceResolver;
    @Mock
    private DigitalDocumentValidator digitalDocumentValidator;
    @Mock
    private DigitalDocumentRepository digitalDocumentRepository;
    @Mock
    private StockReferenceDateService stockReferenceDateService;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenUpdateDigitalDocumentForProxy {
        private final Long stockReferenceDateId = someLongBetween(1L, 10L);
        @Mock
        private ag.act.model.UpdatePostRequestDigitalDocument updateRequest;
        @Mock
        private DigitalDocument digitalDocument;
        @Mock
        private StockReferenceDate stockReferenceDate;

        @BeforeEach
        void setUp() {
            final DigitalDocumentType digitalDocumentType = DigitalDocumentType.DIGITAL_PROXY;
            final LocalDateTime startDate = LocalDateTime.now();
            final Instant startDateTime = DateTimeConverter.convert(startDate);
            final Instant endDateTime = DateTimeConverter.convert(startDate.plusDays(3));
            final LocalDate nowDate = LocalDate.now();

            // Given
            given(digitalDocument.getType()).willReturn(digitalDocumentType);
            given(updateRequest.getTargetEndDate()).willReturn(endDateTime);
            given(updateRequest.getTargetStartDate()).willReturn(startDateTime);
            given(digitalDocument.getTargetStartDate()).willReturn(startDate);
            given(updateRequest.getStockReferenceDateId()).willReturn(stockReferenceDateId);
            given(stockReferenceDateService.findById(stockReferenceDateId))
                .willReturn(Optional.ofNullable(stockReferenceDate));
            given(stockReferenceDate.getReferenceDate()).willReturn(nowDate);
        }

        @Test
        void shouldBeSuccess() {

            // When
            DigitalDocument actual = service.updateDigitalDocument(digitalDocument, updateRequest);

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual.getTitle(), is(digitalDocument.getTitle()));
        }
    }



    @Nested
    class WhenPreviewDigitalDocumentForProxy {
        @Mock
        private DownloadFile previewPdfFile;
        @Mock
        private ag.act.model.PreviewDigitalDocumentRequest previewRequest;
        @Mock
        private DigitalProxyService digitalProxyService;

        @BeforeEach
        void setUp() {
            final String typeString = "DIGITAL_PROXY";
            DigitalDocumentType digitalDocumentType = DigitalDocumentType.DIGITAL_PROXY;

            // Given
            given(previewRequest.getType()).willReturn(typeString);
            given(digitalDocumentTypeServiceResolver.getService(digitalDocumentType))
                .willReturn(digitalProxyService);
            given(digitalProxyService.preview(previewRequest)).willReturn(previewPdfFile);
        }

        @Test
        void shouldBeSuccess() {

            // When
            DownloadFile actual = service.previewDigitalDocument(previewRequest);

            // Then
            assertThat(actual, notNullValue());
        }
    }
}