package ag.act.validator.document;

import ag.act.entity.DigitalProxy;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DigitalProxyModuSignValidator {

    public DigitalProxy validateAndGet(DigitalProxy digitalProxy) {
        if (digitalProxy == null) {
            log.error("Failed to find digital proxy");
            throw new NotFoundException("의결권위임 정보를 찾을 수 없습니다.");
        }

        return digitalProxy;
    }

    public void validateModuSignDocument(ModuSignDocument document) {
        if (document.getParticipantId() == null) {
            log.error("Failed to get participantId from modusign. document: {}", document);
            throw new InternalServerException("모두싸인을 통해 위임중에 알 수 없는 오류가 발생하였습니다. (participantId)");
        }
        if (document.getId() == null) {
            log.error("Failed to get documentId from modusign. document: {}", document);
            throw new InternalServerException("모두싸인을 통해 위임중에 알 수 없는 오류가 발생하였습니다. (documentId)");
        }
    }

    public void validateBeforeApproval(DigitalProxy digitalProxy) {

        if (DateTimeUtil.isNowBefore(digitalProxy.getTargetStartDate())) {
            throw new BadRequestException("아직 위임 가능 기간이 아닙니다.");
        }

        if (DateTimeUtil.isPast(digitalProxy.getTargetEndDate())) {
            throw new BadRequestException("이미 위임기간이 종료되었습니다.");
        }
    }

    public void validate(ag.act.model.CreateDigitalProxyRequest createDigitalProxyRequest) {

        if (createDigitalProxyRequest == null) {
            return;
        }

        if (DateTimeUtil.isPast(createDigitalProxyRequest.getTargetEndDate())) {
            log.error("Failed to validate targetEndDate of digital proxy which is past. targetEndDate: {}",
                createDigitalProxyRequest.getTargetEndDate());
            throw new BadRequestException("의결권위임 종료일은 현재 시간 이후로 설정해주세요.");
        }
    }
}
