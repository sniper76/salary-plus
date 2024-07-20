package ag.act.service.user;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserWithdrawalRequestValidatorTest {

    @InjectMocks
    private UserWithdrawalRequestValidator validator;
    @Mock
    private SolidarityLeaderService solidarityLeaderService;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        given(user.getId()).willReturn(someLong());
        given(solidarityLeaderService.isLeader(anyLong())).willReturn(Boolean.FALSE);
        given(digitalDocumentService.existsProcessingByAcceptor(anyLong())).willReturn(Boolean.FALSE);
        given(solidarityLeaderApplicantService.isUserAppliedForActiveSolidarityLeaderElection(anyLong()))
            .willReturn(Boolean.FALSE);
    }

    @Nested
    class WhenUserIsLeader {
        @BeforeEach
        void setUp() {
            given(solidarityLeaderService.isLeader(anyLong()))
                .willReturn(Boolean.TRUE);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> validator.validate(user),
                "주주대표는 탈퇴할 수 없습니다. 관리자에게 문의해주세요."
            );
        }
    }

    @Nested
    class WhenUserHasProcessingDigitalDocument {
        @BeforeEach
        void setUp() {
            given(digitalDocumentService.existsProcessingByAcceptor(anyLong()))
                .willReturn(Boolean.TRUE);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> validator.validate(user),
                "수임인으로 진행중인 전자문서가 존재하여 탈퇴할 수 없습니다. 관리자에게 문의해주세요."
            );
        }
    }

    @Nested
    class WhenUserAppliedForActiveSolidarityLeaderElection {
        @BeforeEach
        void setUp() {
            given(solidarityLeaderApplicantService.isUserAppliedForActiveSolidarityLeaderElection(anyLong()))
                .willReturn(Boolean.TRUE);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> validator.validate(user),
                "주주대표 선출 진행 중에는 회원탈퇴를 할 수 없습니다."
            );
        }
    }
}
