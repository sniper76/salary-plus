package ag.act.module.solidarity.election.assigner.tiebreaker.filter;

import ag.act.entity.User;
import ag.act.exception.NotFoundException;
import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.TieBreakerDto;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.UserRegistrationDateTieBreakerDto;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Component
public class OldestUserRegistrationDateApplicantVoteFilter implements TieBreakerFilter {
    private final UserService userService;
    private final TieBreakerBaseFilter tieBreakerBaseFilter;

    public List<ApplicantVote> filter(List<ApplicantVote> tieApplicantVoteList) {
        return tieBreakerBaseFilter.filter(tieApplicantVoteList, this::toUserRegistrationDateTieBreakerDto);
    }

    private TieBreakerDto<LocalDateTime> toUserRegistrationDateTieBreakerDto(ApplicantVote applicantVote) {
        return new UserRegistrationDateTieBreakerDto(applicantVote, getUserRegistrationDate(applicantVote));
    }

    private LocalDateTime getUserRegistrationDate(ApplicantVote applicantVote) {
        return getUserByApplicantId(applicantVote.applicantId())
            .getCreatedAt();
    }

    private User getUserByApplicantId(Long applicantId) {
        return userService.findUserByApplicantId(applicantId)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }
}
