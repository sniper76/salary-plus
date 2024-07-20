package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.exception.NotFoundException;
import ag.act.module.solidarity.election.SolidarityLeaderElectionSlackNotifier;
import ag.act.repository.SolidarityLeaderApplicantRepository;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.notifier.SolidarityLeaderApplicantChangeNotifier;
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
import java.util.Optional;

import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityLeaderApplicantServiceTest {
    @InjectMocks
    private SolidarityLeaderApplicantService service;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityLeaderApplicantRepository solidarityLeaderApplicantRepository;
    @Mock
    private Solidarity solidarity;
    @Mock
    private SolidarityLeaderApplicant applicant;
    @Mock
    private ApplySolidarityValidator applySolidarityValidator;
    @Mock
    private SolidarityLeaderApplicantChangeNotifier solidarityLeaderApplicantChangeNotifier;
    @Mock
    private SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier;
    @Mock
    private User user;
    private Long solidarityId;
    private Long userId;
    private List<MockedStatic<?>> statics;
    private String stockCode;
    private int version;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        stockCode = someStockCode();
        version = 1;

        willDoNothing().given(solidarityLeaderElectionSlackNotifier).notifyIfApplicantComplete(anyLong(), any(User.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    private void givenGetUserSolidarityLeaderApplicantWillReturnApplicant() {
        userId = someLong();
        solidarityId = someLong();
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(solidarityService.getSolidarityByStockCode(stockCode))
            .willReturn(solidarity);
        given(solidarity.getId()).willReturn(solidarityId);
        given(user.getId()).willReturn(userId);
        given(solidarityLeaderApplicantRepository.findBySolidarityIdAndUserIdAndVersion(solidarityId, userId, version))
            .willReturn(Optional.of(applicant));
    }

    @Nested
    class WhenGetUserSolidarityLeaderApplicant {
        @BeforeEach
        void setUp() {
            givenGetUserSolidarityLeaderApplicantWillReturnApplicant();
        }

        @Test
        void shouldGetUserSolidarityLeaderApplicant() {
            // When
            Optional<SolidarityLeaderApplicant> actual = service.findSolidarityLeaderApplicant(stockCode);

            // Then
            assertThat(actual.isPresent(), is(true));
            assertThat(actual.get(), is(applicant));
        }
    }

    @Nested
    class WhenIsUserAppliedSolidarity {
        @Nested
        class AndUserAppliedSolidarity {
            @Test
            void shouldReturnFalseWhenIsUserAppliedSolidarity() {
                // Given
                givenGetUserSolidarityLeaderApplicantWillReturnApplicant();

                // When
                boolean actual = service.isUserAppliedSolidarity(stockCode);

                // Then
                assertThat(actual, is(true));
            }
        }

        @Nested
        class AndUserNotAppliedSolidarity {
            @BeforeEach
            void setUp() {
                userId = someLong();
                solidarityId = someLong();
                given(ActUserProvider.getNoneNull()).willReturn(user);
                given(solidarityService.getSolidarityByStockCode(stockCode))
                    .willReturn(solidarity);
                given(solidarity.getId()).willReturn(solidarityId);
                given(user.getId()).willReturn(userId);
                given(solidarityLeaderApplicantRepository.findBySolidarityIdAndUserIdAndVersion(solidarityId, userId, version))
                    .willReturn(Optional.empty());
            }

            @Test
            void shouldReturnFalseWhenIsUserAppliedSolidarity() {
                // When
                boolean actual = service.isUserAppliedSolidarity(stockCode);

                // Then
                assertThat(actual, is(false));
            }

        }

    }

    @Nested
    class WhenApplyForLeader {

        @Mock
        private Stock stock;
        private String stockName;
        private String userName;
        private int version;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            stockName = someString(10);
            userName = someString(15);
            version = someInteger();

            given(user.getId()).willReturn(userId);
            given(user.getName()).willReturn(userName);
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(solidarityService.getSolidarityByStockCode(stockCode)).willReturn(solidarity);
            given(solidarity.getStock()).willReturn(stock);
            given(stock.getName()).willReturn(stockName);

            willDoNothing().given(applySolidarityValidator).validate(stockCode, version);

            willDoNothing().given(solidarityLeaderApplicantChangeNotifier).notifyAppliedLeader(stockName, userName);

            service.applyForLeader(stockCode);
        }

        @Test
        void shouldSaveSolidarityApplicant() {
            then(solidarityLeaderApplicantRepository)
                .should()
                .save(any(SolidarityLeaderApplicant.class));
        }

        @Test
        void shouldSendSlackMessage() {
            then(solidarityLeaderApplicantChangeNotifier)
                .should()
                .notifyAppliedLeader(stockName, userName);
        }
    }

    @Nested
    class WhenApplyForLeaderVersion2 {

        @Mock
        private Stock stock;
        @Mock
        private SolidarityLeaderElectionApplyDto applyDto;
        private String stockName;
        private String userName;
        private int version;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            stockName = someString(10);
            userName = someString(15);
            version = someInteger();

            given(user.getId()).willReturn(userId);
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(solidarityService.getSolidarityByStockCode(applyDto.getStockCode())).willReturn(solidarity);
            given(stock.getName()).willReturn(stockName);

            given(applyDto.getReasonsForApply()).willReturn(someSolidarityLeaderElectionApplicationItem());
            given(applyDto.getKnowledgeOfCompanyManagement()).willReturn(someSolidarityLeaderElectionApplicationItem());
            given(applyDto.getCommentsForStockHolder()).willReturn(someSolidarityLeaderElectionApplicationItem());
            given(applyDto.getGoals()).willReturn(someSolidarityLeaderElectionApplicationItem());
            given(applyDto.getApplyStatus()).willReturn(someEnum(SolidarityLeaderElectionApplyStatus.class));

            willDoNothing().given(applySolidarityValidator).validate(stockCode, version);

            service.createSolidarityLeaderApplicant(applyDto);
        }

        @Test
        void shouldSaveSolidarityApplicant() {
            then(solidarityLeaderApplicantRepository)
                .should()
                .save(any(SolidarityLeaderApplicant.class));
        }
    }

    @Nested
    class WhenCancelLeaderApplication {

        @BeforeEach
        void setUp() {
            givenGetUserSolidarityLeaderApplicantWillReturnApplicant();
        }

        @Nested
        class AndSuccessfullyDelete {

            @Test
            void shouldDeleteSolidarityLeaderApplicant() {
                // When
                service.cancelLeaderApplication(stockCode);

                // Then
                then(solidarityLeaderApplicantRepository).should().delete(applicant);
            }
        }

        @Nested
        class AndNotFoundExistingApplicant {

            @Test
            void shouldThrowNotFoundException() {

                // Given
                given(solidarityLeaderApplicantRepository.findBySolidarityIdAndUserIdAndVersion(solidarityId, userId, version))
                    .willReturn(Optional.empty());

                // When
                final NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> service.cancelLeaderApplication(stockCode)
                );

                // Then
                assertThat(exception.getMessage(), is("해당 종목에 대한 지원 내역이 없습니다."));
                then(solidarityLeaderApplicantRepository).should(never()).delete(applicant);
            }
        }
    }
}