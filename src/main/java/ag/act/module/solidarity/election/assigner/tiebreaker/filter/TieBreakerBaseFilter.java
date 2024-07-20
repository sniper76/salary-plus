package ag.act.module.solidarity.election.assigner.tiebreaker.filter;

import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.TieBreakerDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Transactional
@Component
public class TieBreakerBaseFilter {
    public <T> List<ApplicantVote> filter(List<ApplicantVote> tieApplicantVoteList, Function<ApplicantVote, TieBreakerDto<T>> toTieBreakerDto) {
        final List<TieBreakerDto<T>> eligibleTieBreakerDtoList = tieApplicantVoteList.stream()
            .map(toTieBreakerDto)
            .sorted()
            .toList();

        return getHighestApplicantVotes(
            eligibleTieBreakerDtoList,
            getHighestValue(eligibleTieBreakerDtoList)
        );
    }

    private <T> T getHighestValue(List<TieBreakerDto<T>> eligibleTieBreakerList) {
        return eligibleTieBreakerList.get(0).getValue();
    }

    private <T> List<ApplicantVote> getHighestApplicantVotes(
        List<TieBreakerDto<T>> eligibleTieBreakerDtoList,
        T highestValue
    ) {

        return eligibleTieBreakerDtoList.stream()
            .filter(tieBreakerData -> tieBreakerData.equals(highestValue))
            .map(TieBreakerDto::getApplicantVote)
            .toList();
    }
}
