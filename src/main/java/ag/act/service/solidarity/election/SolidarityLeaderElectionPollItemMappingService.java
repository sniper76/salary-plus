package ag.act.service.solidarity.election;

import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.entity.PollItem;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.repository.SolidarityLeaderElectionPollItemMappingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionPollItemMappingService {
    private final SolidarityLeaderElectionPollItemMappingRepository solidarityLeaderElectionPollItemMappingRepository;

    public void createPollItemMappings(
        Long solidarityLeaderElectionId, List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants, List<PollItem> savedPollItemList
    ) {
        for (int index = 0; index < savedPollItemList.size(); index++) {
            SolidarityLeaderApplicantDto solidarityLeaderApplicant = solidarityLeaderApplicants.get(index / 2);
            PollItem pollItem = savedPollItemList.get(index);

            SolidarityLeaderElectionPollItemMapping mapping = new SolidarityLeaderElectionPollItemMapping();
            mapping.setSolidarityLeaderElectionId(solidarityLeaderElectionId);
            mapping.setSolidarityLeaderApplicantId(solidarityLeaderApplicant.solidarityApplicantId());
            mapping.setPollItemId(pollItem.getId());
            mapping.setElectionAnswerType(SolidarityLeaderElectionAnswerType.fromText(pollItem.getText()));

            solidarityLeaderElectionPollItemMappingRepository.save(mapping);
        }
    }

    public List<SolidarityLeaderElectionPollItemMapping> getSolidarityLeaderElectionPollItemMappingList(
        Long solidarityLeaderElectionId, List<Long> pollItemIds
    ) {
        return solidarityLeaderElectionPollItemMappingRepository.findAllBySolidarityLeaderElectionIdAndPollItemIdIn(
            solidarityLeaderElectionId, pollItemIds
        );
    }
}
