package ag.act.validator.solidarity;

import ag.act.entity.Solidarity;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SolidarityValidator {
    public void validateUpdateSolidarityToActive(Solidarity solidarity) {
        if (solidarity.getStatus() == Status.ACTIVE) {
            log.error("validateUpdateSolidarityToActive error: solidarityId: {}, status: {}", solidarity.getId(), solidarity.getStatus());
            throw new BadRequestException("이미 활성화된 연대입니다.");
        }
        if (solidarity.getStatus() != Status.INACTIVE_BY_ADMIN) {
            log.error("validateUpdateSolidarityToActive error: solidarityId: {}, status: {}", solidarity.getId(), solidarity.getStatus());
            throw new InternalServerException("연대 상태가 활성화 대기 상태가 아닙니다.");
        }
    }
}
