package ag.act.validator;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.repository.SolidarityLeaderApplicantRepository;
import ag.act.service.solidarity.SolidarityService;
import ag.act.validator.solidarity.ApplySolidarityValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class ApplySolidarityValidatorTest {
    @InjectMocks
    private ApplySolidarityValidator validator;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityLeaderApplicantRepository solidarityLeaderApplicantRepository;
    @Mock
    private Solidarity solidarity;
    @Mock
    private User user;
    private List<MockedStatic<?>> statics;
    private String stockCode;
    private int version;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        stockCode = someStockCode();
        final Long userId = someLong();
        final Long solidarityId = someLong();

        given(user.getId()).willReturn(userId);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(solidarityService.getSolidarityByStockCode(stockCode)).willReturn(solidarity);
        given(solidarity.getId()).willReturn(solidarityId);

        version = someInteger();
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenValidate {
        @Test
        void shouldSuccess() {
            // When
            validator.validate(stockCode, version);

            // Then
            then(solidarityService).should().getSolidarityByStockCode(stockCode);
            then(solidarity).should().getSolidarityLeader();
            then(solidarityLeaderApplicantRepository).should().existsBySolidarityIdAndUserIdAndVersion(
                solidarity.getId(), user.getId(), version
            );
        }

        @Nested
        class AndSolidarityLeaderAlreadyExists {

            @Test
            void shouldThrowBadRequestException() {
                // Given
                SolidarityLeader solidarityLeader = mock(SolidarityLeader.class);
                given(solidarity.getSolidarityLeader()).willReturn(solidarityLeader);

                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validate(stockCode, version)
                );

                // Then
                assertThat(exception.getMessage(), is("해당 연대에 이미 주주대표가 존재합니다."));
                then(solidarityService).should().getSolidarityByStockCode(stockCode);
                then(solidarity).should().getSolidarityLeader();
                then(solidarityLeaderApplicantRepository).shouldHaveNoInteractions();
            }

        }

        @Nested
        class AndUserAlreadyHasBeenApplied {

            @Test
            void shouldThrowBadRequestException() {
                // Given
                given(solidarityLeaderApplicantRepository.existsBySolidarityIdAndUserIdAndVersion(
                    solidarity.getId(), user.getId(), version
                )).willReturn(true);

                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validate(stockCode, version)
                );

                // Then
                assertThat(exception.getMessage(), is("이미 주주대표 지원이 완료되었습니다."));
                then(solidarityService).should().getSolidarityByStockCode(stockCode);
                then(solidarity).should().getSolidarityLeader();
                then(solidarityLeaderApplicantRepository).should().existsBySolidarityIdAndUserIdAndVersion(
                    solidarity.getId(), user.getId(), version
                );
            }
        }
    }
}
