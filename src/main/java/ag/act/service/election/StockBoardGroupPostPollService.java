package ag.act.service.election;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.election.PollAnswerData;
import ag.act.dto.election.SolidarityLeaderElectionApplicantDataLabel;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.module.solidarity.election.label.EarlyFinishedConditionDataLabelGenerator;
import ag.act.module.solidarity.election.label.ResolutionConditionDataLabelGenerator;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.poll.PollItemService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionPollItemMappingService;
import ag.act.util.NumberUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockBoardGroupPostPollService {
    private final PollAnswerService pollAnswerService;
    private final SolidarityLeaderElectionPollItemMappingService solidarityLeaderElectionPollItemMappingService;
    private final PollItemService pollItemService;
    private final ResolutionConditionDataLabelGenerator resolutionConditionDataLabelGenerator;
    private final EarlyFinishedConditionDataLabelGenerator earlyFinishedConditionDataLabelGenerator;

    public Map<Long, ApplicantPollAnswerData> getElectionPollItemWithAnswers(
        SolidarityLeaderElection solidarityLeaderElection,
        Long postId, Long pollId,
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants
    ) {
        final List<PollAnswer> totalPollAnswerList = pollAnswerService.getPollAnswersByPostId(postId);
        final long totalStockQuantity = calculateTotalStockQuantity(totalPollAnswerList, solidarityLeaderApplicants);

        return getPollAnswerListToResultMap(
            solidarityLeaderElection,
            getApplicantListMap(solidarityLeaderElection.getId(), pollId),
            getPollAnswerListByPollItemIdMap(totalPollAnswerList),
            solidarityLeaderApplicants,
            totalStockQuantity
        );
    }

    private long calculateTotalStockQuantity(
        List<PollAnswer> totalPollAnswerList,
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants
    ) {
        return solidarityLeaderApplicants.isEmpty()
            ? 0
            : getTotalStockQuantity(totalPollAnswerList, solidarityLeaderApplicants.size());
    }

    private Map<Long, List<PollAnswer>> getPollAnswerListByPollItemIdMap(List<PollAnswer> pollAnswers) {
        return getPollAnswerListMap(pollAnswers);
    }

    private Map<Long, List<SolidarityLeaderElectionPollItemMapping>> getApplicantListMap(Long solidarityLeaderElectionId, Long pollId) {
        return getSolidarityLeaderApplicantListMap(
            getSolidarityLeaderElectionPollItemMappings(solidarityLeaderElectionId, pollId)
        );
    }

    @NotNull
    private Map<Long, ApplicantPollAnswerData> getPollAnswerListToResultMap(
        SolidarityLeaderElection solidarityLeaderElection,
        Map<Long, List<SolidarityLeaderElectionPollItemMapping>> applicantListMap,
        Map<Long, List<PollAnswer>> pollAnswerListByPollItemIdMap,
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants,
        long totalStockQuantity
    ) {
        Map<Long, ApplicantPollAnswerData> result = new LinkedHashMap<>();
        applicantListMap.forEach(
            (applicantId, solidarityLeaderElectionPollItemMapping) -> {
                long totalApprovalStockQuantity = pollAnswerService.sumApprovalPollAnswersByPostId(
                    solidarityLeaderElection.getPostId(),
                    applicantId
                );

                result.put(
                    applicantId,
                    getApplicantPollAnswerData(
                        solidarityLeaderElection,
                        pollAnswerListByPollItemIdMap,
                        solidarityLeaderApplicants,
                        totalStockQuantity,
                        totalApprovalStockQuantity,
                        applicantId,
                        solidarityLeaderElectionPollItemMapping
                    )
                );
            }
        );

        return result;
    }

    private ApplicantPollAnswerData getApplicantPollAnswerData(
        SolidarityLeaderElection solidarityLeaderElection,
        Map<Long, List<PollAnswer>> pollAnswerListByPollItemIdMap,
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants,
        long totalStockQuantity,
        long totalApprovalStockQuantity,
        Long applicantId,
        List<SolidarityLeaderElectionPollItemMapping> leaderElectionPollItemMappings
    ) {
        return new ApplicantPollAnswerData(
            applicantId,
            solidarityLeaderElection.getStockCode(),
            getNickname(solidarityLeaderApplicants, applicantId),
            totalStockQuantity,
            getResolutionConditionDataLabel(solidarityLeaderElection, totalApprovalStockQuantity),
            getEarlyFinishedConditionDataLabel(solidarityLeaderElection, totalApprovalStockQuantity),
            toPollAnswerDataList(pollAnswerListByPollItemIdMap, leaderElectionPollItemMappings, totalStockQuantity)
        );
    }

    private SolidarityLeaderElectionApplicantDataLabel getEarlyFinishedConditionDataLabel(
        SolidarityLeaderElection solidarityLeaderElection,
        long totalVoteStockQuantity
    ) {
        return earlyFinishedConditionDataLabelGenerator.generate(solidarityLeaderElection.getTotalStockQuantity(), totalVoteStockQuantity);
    }

    private SolidarityLeaderElectionApplicantDataLabel getResolutionConditionDataLabel(
        SolidarityLeaderElection solidarityLeaderElection,
        long totalVoteStockQuantity
    ) {
        return resolutionConditionDataLabelGenerator.generate(solidarityLeaderElection.getTotalStockQuantity(), totalVoteStockQuantity);
    }

    private String getNickname(
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicantDtoList, Long applicantId
    ) {
        return solidarityLeaderApplicantDtoList
            .stream()
            .filter(it -> Objects.equals(it.solidarityApplicantId(), applicantId))
            .map(SolidarityLeaderApplicantDto::nickname)
            .findFirst()
            .orElse(null);
    }

    private List<PollAnswerData> toPollAnswerDataList(
        Map<Long, List<PollAnswer>> pollAnswerListByPollItemIdMap,
        List<SolidarityLeaderElectionPollItemMapping> leaderElectionPollItemMappings,
        long totalStockQuantity
    ) {
        final Long userId = getCurrentUserId();

        return leaderElectionPollItemMappings.stream()
            .map(leaderElectionPollItemMapping -> {
                Long pollItemId = leaderElectionPollItemMapping.getPollItemId();
                List<PollAnswer> pollAnswers = pollAnswerListByPollItemIdMap.getOrDefault(pollItemId, new ArrayList<>());
                final long totalVoteStockQuantity = getTotalVoteStockQuantity(pollAnswers);
                return new PollAnswerData(
                    pollItemId,
                    pollAnswers.size(),
                    totalVoteStockQuantity,
                    NumberUtil.getPercentage(totalVoteStockQuantity, totalStockQuantity),
                    leaderElectionPollItemMapping.getElectionAnswerType(),
                    getIsVoted(pollAnswers, userId)
                );
            })
            .toList();
    }

    private Long getCurrentUserId() {
        return ActUserProvider.get()
            .map(User::getId)
            .orElseGet(ActUserProvider::getSystemUserId);
    }

    private List<SolidarityLeaderElectionPollItemMapping> getSolidarityLeaderElectionPollItemMappings(
        Long solidarityLeaderElectionId, Long pollId
    ) {
        final List<Long> allPollItemIds = pollItemService.getPollItems(pollId)
            .stream()
            .map(PollItem::getId)
            .toList();

        return solidarityLeaderElectionPollItemMappingService.getSolidarityLeaderElectionPollItemMappingList(
            solidarityLeaderElectionId,
            allPollItemIds
        );
    }

    private boolean getIsVoted(List<PollAnswer> pollAnswers, Long userId) {
        return pollAnswers
            .parallelStream()
            .anyMatch(pollAnswer -> pollAnswer.getUserId().equals(userId));
    }

    private long getTotalVoteStockQuantity(List<PollAnswer> pollAnswers) {
        return pollAnswerService.getTotalVoteStockQuantity(pollAnswers);
    }

    private long getTotalStockQuantity(List<PollAnswer> pollAnswers, int applicantCount) {
        return getTotalVoteStockQuantity(pollAnswers) / applicantCount;
    }

    private Map<Long, List<SolidarityLeaderElectionPollItemMapping>> getSolidarityLeaderApplicantListMap(
        List<SolidarityLeaderElectionPollItemMapping> leaderElectionPollItemMappingList
    ) {
        return leaderElectionPollItemMappingList
            .stream()
            .collect(
                Collectors.groupingBy(
                    SolidarityLeaderElectionPollItemMapping::getSolidarityLeaderApplicantId,
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            );
    }

    private Map<Long, List<PollAnswer>> getPollAnswerListMap(
        List<PollAnswer> pollAnswers
    ) {
        return pollAnswers
            .parallelStream()
            .collect(Collectors.groupingBy(PollAnswer::getPollItemId));
    }
}
