package ag.act.service.solidarity;

import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.repository.solidarity.election.SolidarityLeaderElectionRepository;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionCreateService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.quality.Strictness.LENIENT;

@MockitoSettings(strictness = LENIENT)
class SolidarityLeaderElectionServiceTest {

    @InjectMocks
    private SolidarityLeaderElectionService solidarityLeaderElectionService;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private SolidarityLeaderElectionRepository solidarityLeaderElectionRepository;
    @Mock
    private SolidarityLeaderElectionCreateService solidarityLeaderElectionCreateService;

    @Mock
    private SolidarityLeaderElection election;

    @Nested
    class WhenApplySolidarityLeader {

        @Nested
        class WhenElectionNotExist {

            private String stockCode;
            @Mock
            private SolidarityLeaderElectionApplyDto solidarityLeaderElectionApplyDto;

            @BeforeEach
            void setUp() {
                stockCode = someStockCode();

                given(solidarityLeaderElectionRepository
                    .findByStockCodeAndElectionStatusIn(stockCode, SolidarityLeaderElectionStatus.getActiveStatus()))
                    .willReturn(Optional.empty());
            }

            @Nested
            class WhenSave {

                @BeforeEach
                void setUp() {
                    given(solidarityLeaderElectionApplyDto.getApplyStatus()).willReturn(SolidarityLeaderElectionApplyStatus.SAVE);
                    given(solidarityLeaderElectionApplyDto.getStockCode()).willReturn(stockCode);
                    given(solidarityLeaderElectionCreateService.createPendingSolidarityLeaderElection(stockCode)).willReturn(election);
                    given(election.isPendingElection()).willReturn(true);

                    solidarityLeaderElectionService.createSolidarityLeaderApplicant(solidarityLeaderElectionApplyDto);
                }

                @Test
                void shouldCallSaveElectionSave() {
                    then(solidarityLeaderElectionCreateService).should().createPendingSolidarityLeaderElection(stockCode);
                }

            }

            @Nested
            class WhenComplete {

                @BeforeEach
                void setUp() {
                    given(solidarityLeaderElectionApplyDto.getApplyStatus()).willReturn(SolidarityLeaderElectionApplyStatus.COMPLETE);
                    given(solidarityLeaderElectionApplyDto.getStockCode()).willReturn(stockCode);
                    given(solidarityLeaderElectionCreateService.createPendingSolidarityLeaderElection(stockCode)).willReturn(election);
                    given(solidarityLeaderElectionCreateService.startSolidarityLeaderElection(election)).willReturn(election);
                    given(election.isPendingElection()).willReturn(true);
                    given(election.isCandidateRegistrationPeriod()).willReturn(true);

                    solidarityLeaderElectionService.createSolidarityLeaderApplicant(solidarityLeaderElectionApplyDto);
                }

                @Test
                void shouldCallCreatePendingElection() {
                    then(solidarityLeaderElectionCreateService).should().createPendingSolidarityLeaderElection(stockCode);
                }

                @Test
                void shouldCallStartElection() {
                    then(solidarityLeaderElectionCreateService).should().startSolidarityLeaderElection(election);
                }
            }
        }
    }
}
