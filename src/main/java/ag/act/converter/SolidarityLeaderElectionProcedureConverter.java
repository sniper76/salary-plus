package ag.act.converter;

import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.model.SolidarityLeaderElectionProcedureResponse;
import ag.act.model.SolidarityLeaderElectionProceduresDataResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolidarityLeaderElectionProcedureConverter {

    public SolidarityLeaderElectionProceduresDataResponse convert(List<SolidarityLeaderElectionProcedure> procedures) {
        final List<SolidarityLeaderElectionProcedureResponse> data = procedures.stream()
            .map(this::convert)
            .toList();
        return new ag.act.model.SolidarityLeaderElectionProceduresDataResponse()
            .data(data);
    }

    private SolidarityLeaderElectionProcedureResponse convert(SolidarityLeaderElectionProcedure procedure) {
        return new SolidarityLeaderElectionProcedureResponse()
            .name(procedure.name())
            .title(procedure.getTitle())
            .durationDays(procedure.getDurationDays())
            .displayOrder(procedure.getDisplayOrder())
            .description(procedure.getDescription());
    }
}
