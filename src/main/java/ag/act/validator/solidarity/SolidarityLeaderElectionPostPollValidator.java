package ag.act.validator.solidarity;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.service.solidarity.election.SolidarityLeaderElectionPollItemMappingService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SolidarityLeaderElectionPostPollValidator {
    private final SolidarityLeaderElectionPollItemMappingService solidarityLeaderElectionPollItemMappingService;

    public void validateElectionPollItems(
        SolidarityLeaderElection solidarityLeaderElection,
        List<CreatePollAnswerItemRequest> pollAnswerItemRequests
    ) {
        validateVotePeriod(solidarityLeaderElection);

        final Map<Long, List<SolidarityLeaderElectionPollItemMapping>> mappingListMapByApplicantId
            = getMappingListMapByApplicantId(solidarityLeaderElection, pollAnswerItemRequests);

        validateSingleVotePerCandidate(mappingListMapByApplicantId);
        validateOnlySingleApprovalVote(mappingListMapByApplicantId);
    }

    private Map<Long, List<SolidarityLeaderElectionPollItemMapping>> getMappingListMapByApplicantId(
        SolidarityLeaderElection solidarityLeaderElection,
        List<CreatePollAnswerItemRequest> pollAnswerItemRequests
    ) {
        final List<Long> pollItemIds = getPollItemIds(pollAnswerItemRequests);

        final List<SolidarityLeaderElectionPollItemMapping> pollItemMappingListOfAnswerItemRequest
            = getSolidarityLeaderElectionPollItemMappings(solidarityLeaderElection, pollItemIds);

        if (pollItemIds.size() != pollItemMappingListOfAnswerItemRequest.size()) {
            throw new BadRequestException("주주대표 선출 투표 항목과 요청 항목이 일치하지 않습니다.");
        }

        return pollItemMappingListOfAnswerItemRequest
            .stream()
            .collect(Collectors.groupingBy(SolidarityLeaderElectionPollItemMapping::getSolidarityLeaderApplicantId));
    }

    @NotNull
    private List<Long> getPollItemIds(List<CreatePollAnswerItemRequest> pollAnswerItemRequests) {
        return pollAnswerItemRequests
            .stream()
            .map(CreatePollAnswerItemRequest::getPollItemId)
            .toList();
    }

    private List<SolidarityLeaderElectionPollItemMapping> getSolidarityLeaderElectionPollItemMappings(
        SolidarityLeaderElection solidarityLeaderElection, List<Long> pollItemIds
    ) {
        return solidarityLeaderElectionPollItemMappingService.getSolidarityLeaderElectionPollItemMappingList(
            solidarityLeaderElection.getId(), pollItemIds
        );
    }

    private void validateVotePeriod(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.getElectionStatus().isFinishedStatus()) {
            throw new BadRequestException("주주대표 선출이 이미 마감 되었습니다.");
        }
    }

    private void validateSingleVotePerCandidate(
        Map<Long, List<SolidarityLeaderElectionPollItemMapping>> mappingListMapByApplicantId
    ) {
        mappingListMapByApplicantId
            .forEach((applicantId, mappingList) -> {
                if (isMoreThanOne(mappingList.size())) {
                    throw new BadRequestException("주주대표 선출 후보자에 찬성/반대를 동시에 투표할 수 없습니다.");
                }
            });
    }

    private void validateOnlySingleApprovalVote(Map<Long, List<SolidarityLeaderElectionPollItemMapping>> mappingListMapByApplicantId) {
        final AtomicInteger count = new AtomicInteger();
        mappingListMapByApplicantId
            .forEach((applicantId, mappingList) -> {
                if (hasApproval(mappingList)) {
                    count.getAndIncrement();
                }
            });

        if (isMoreThanOne(count.get())) {
            throw new BadRequestException("주주대표 선출 후보자에 찬성을 2개 이상 투표할 수 없습니다.");
        }
    }

    private boolean hasApproval(List<SolidarityLeaderElectionPollItemMapping> mappingList) {
        return mappingList
            .stream()
            .map(SolidarityLeaderElectionPollItemMapping::getElectionAnswerType)
            .anyMatch(SolidarityLeaderElectionAnswerType::isApproval);
    }

    private boolean isMoreThanOne(int count) {
        return count > 1;
    }
}
