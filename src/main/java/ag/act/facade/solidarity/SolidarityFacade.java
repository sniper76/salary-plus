package ag.act.facade.solidarity;

import ag.act.converter.stock.SolidarityResponseConverter;
import ag.act.entity.Solidarity;
import ag.act.model.SolidarityDataResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.validator.solidarity.SolidarityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SolidarityFacade {
    private final SolidarityService solidarityService;
    private final SolidarityResponseConverter solidarityResponseConverter;
    private final SolidarityValidator solidarityValidator;

    public SolidarityDataResponse updateSolidarityToActive(Long solidarityId) {
        Solidarity solidarity = solidarityService.getSolidarity(solidarityId);
        solidarityValidator.validateUpdateSolidarityToActive(solidarity);

        solidarity.setStatus(ag.act.model.Status.ACTIVE);

        return new SolidarityDataResponse()
            .data(solidarityResponseConverter.convert(
                solidarityService.saveSolidarity(solidarity)
            ));
    }
}
