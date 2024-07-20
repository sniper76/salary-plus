package ag.act.module.solidarity.election.assigner.tiebreaker.filter;

import ag.act.entity.User;
import ag.act.exception.NotFoundException;
import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.NumberOfPostsTieBreakerDto;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.TieBreakerDto;
import ag.act.service.post.PostService;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Component
public class MostNumberOfPostsApplicantVoteFilter implements TieBreakerFilter {
    private final UserService userService;
    private final PostService postService;
    private final TieBreakerBaseFilter tieBreakerBaseFilter;

    public List<ApplicantVote> filter(List<ApplicantVote> tieApplicantVoteList) {
        return tieBreakerBaseFilter.filter(tieApplicantVoteList, this::toNumberOfPostsTieBreakerDto);
    }

    private TieBreakerDto<Long> toNumberOfPostsTieBreakerDto(ApplicantVote applicantVote) {
        return new NumberOfPostsTieBreakerDto(applicantVote, getNumberOfPosts(applicantVote));
    }

    private long getNumberOfPosts(ApplicantVote applicantVote) {
        final User user = getUser(applicantVote.applicantId());

        return postService.getActivePostsCount(user.getId());
    }

    private User getUser(Long applicantId) {
        return userService.findUserByApplicantId(applicantId)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }
}
