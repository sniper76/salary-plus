package ag.act.service.digitaldocument;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.BadRequestException;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.documenttype.DigitalDocumentTypeServiceResolver;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostUpdateService;
import ag.act.validator.document.DigitalDocumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

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
    @Mock
    private StockBoardGroupPostUpdateService stockBoardGroupPostUpdateService;


    @Nested
    class GetDigitalDocumentNotNull {

        private Long digitalDocumentId;

        @BeforeEach
        void setUp() {
            digitalDocumentId = someLong();
            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.empty());
        }

        @Test
        void shouldThrowBadRequestException() {

            final BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.getDigitalDocument(digitalDocumentId)
            );

            assertThat(exception.getMessage(), is("전자문서 정보가 없습니다."));
        }
    }

    @Nested
    class IsFinished {

        private Long digitalDocumentId;
        @Mock
        private DigitalDocument digitalDocument;

        @BeforeEach
        void setUp() {
            digitalDocumentId = someLong();
            given(digitalDocumentRepository.findById(digitalDocumentId))
                .willReturn(Optional.of(digitalDocument));
        }

        @Nested
        class WhenTheDigitalDocumentIsFinished {

            @BeforeEach
            void setUp() {
                given(digitalDocument.getTargetEndDate())
                    .willReturn(LocalDateTime.now().minusMinutes(someIntegerBetween(1, 10)));
            }

            @Test
            void shouldReturnTrue() {

                final boolean actual = service.isFinished(digitalDocumentId);

                assertThat(actual, is(true));

            }
        }

        @Nested
        class WhenTheDigitalDocumentIsNotFinished {

            @BeforeEach
            void setUp() {
                given(digitalDocument.getTargetEndDate())
                    .willReturn(LocalDateTime.now().plusMinutes(1));
            }

            @Test
            void shouldReturnTrue() {

                final boolean actual = service.isFinished(digitalDocumentId);

                assertThat(actual, is(false));

            }
        }
    }
}