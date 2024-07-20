package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.exception.NotFoundException;
import ag.act.exception.UnauthorizedException;
import ag.act.repository.SolidarityLeaderRepository;
import ag.act.repository.SolidarityRepository;
import ag.act.service.admin.CorporateUserService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.solidarity.election.notifier.SolidarityLeaderChangeNotifier;
import ag.act.service.user.UserService;
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

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityLeaderServiceTest {
    @InjectMocks
    private SolidarityLeaderService service;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private UserService userService;
    @Mock
    private SolidarityLeaderChangeNotifier solidarityLeaderChangeNotifier;
    @Mock
    private SolidarityLeaderRepository solidarityLeaderRepository;
    @Mock
    private SolidarityRepository solidarityRepository;
    @Mock
    private Solidarity solidarity;
    @Mock
    private SolidarityLeader solidarityLeader;
    @Mock
    private CorporateUserService corporateUserService;
    private List<MockedStatic<?>> statics;
    private String stockCode;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        stockCode = someStockCode();
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenGetLeader {
        @BeforeEach
        void setUp() {
            given(solidarityService.findSolidarity(stockCode))
                .willReturn(Optional.of(solidarity));
        }

        @Test
        void shouldGetLeader() {
            // Given
            given(solidarity.getSolidarityLeader())
                .willReturn(solidarityLeader);

            // When
            Optional<SolidarityLeader> actual = service.findLeader(stockCode);

            // Then
            assertThat(actual.isPresent(), is(true));
            then(solidarityService).should().findSolidarity(stockCode);
        }

        @Test
        void shouldReturnNull() {
            // Given
            given(solidarity.getSolidarityLeader())
                .willReturn(null);

            // When
            Optional<SolidarityLeader> actual = service.findLeader(stockCode);

            // Then
            assertThat(actual.isEmpty(), is(true));
            then(solidarityService).should().findSolidarity(stockCode);
        }
    }

    @Nested
    class UpdateSolidarityLeaderMessage {
        private Long solidarityId;
        @Mock
        private ag.act.model.UpdateSolidarityLeaderMessageRequest request;
        @Mock
        private SolidarityLeader solidarityLeader;
        @Mock
        private Stock stock;
        @Mock
        private Solidarity solidarity;
        private String message;
        @Mock
        private User user;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            final String stockName = someStockCode();
            solidarityId = someLong();
            message = " " + someString(10) + " ";
            final Long userId = someLong();
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(solidarity.getId()).willReturn(solidarityId);
            given(solidarity.getStock()).willReturn(stock);
            given(stock.getName()).willReturn(stockName);
            given(solidarity.getStockCode()).willReturn(stockCode);
            given(request.getMessage()).willReturn(message);
            given(solidarityLeader.getSolidarity()).willReturn(solidarity);
            given(solidarityLeaderRepository.findBySolidarityId(solidarityId)).willReturn(Optional.of(solidarityLeader));
            given(solidarityLeaderRepository.save(solidarityLeader)).willReturn(solidarityLeader);
            given(solidarityRepository.findById(solidarityId)).willReturn(Optional.of(solidarity));
            given(solidarityLeader.getUserId()).willReturn(userId);
            given(userService.getUserForSolidarityLeader(stockCode)).willReturn(user);

        }

        @Nested
        class Success {
            @BeforeEach
            void setUp() {
                service.updateSolidarityLeaderMessage(solidarityId, request);
            }

            @Test
            void shouldFindSolidarityLeader() {
                then(solidarityLeaderRepository).should().findBySolidarityId(solidarityId);
            }

            @Test
            void shouldSaveSolidarityLeader() {
                then(solidarityLeaderRepository).should().save(solidarityLeader);
            }

            @Test
            void shouldUpdateMessage() {
                then(solidarityLeader).should().setMessage(message.trim());
            }

            @Test
            void shouldGetLeader() {
                then(userService).should().getUserForSolidarityLeader(solidarity.getStockCode());
            }

            @Test
            void shouldNotify() {
                then(solidarityLeaderChangeNotifier).should().notifyUpdatedLeaderMessage(
                    solidarity.getStock().getName(), user.getName(), message);
            }
        }

        @Nested
        class WhenUserIsNotMatched {
            @BeforeEach
            void setUp() {
                given(user.getId()).willReturn(someLong());
            }

            @Test
            void shouldThrowException() {
                assertException(
                    UnauthorizedException.class,
                    () -> service.updateSolidarityLeaderMessage(solidarityId, request),
                    "주주대표가 아니면 변경할 수 없습니다."
                );
            }
        }

        @Nested
        class WhenLeaderNotFound {
            @BeforeEach
            void setUp() {
                given(solidarityLeaderRepository.findBySolidarityId(solidarityId)).willReturn(Optional.empty());
            }

            @Test
            void shouldThrowException() {
                assertException(
                    NotFoundException.class,
                    () -> service.updateSolidarityLeaderMessage(solidarityId, request),
                    "주주대표를 찾을 수 없습니다."
                );
            }
        }
    }
}
