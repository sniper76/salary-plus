package ag.act.validator;

import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.model.CreatePollRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PollValidator {

    public void validate(CreatePollRequest createPollRequest) {
        if (createPollRequest == null) {
            throw new InternalServerException("설문을 생성하는 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 이용해 주세요.");
        }

        if (createPollRequest.getTargetEndDate().isBefore(createPollRequest.getTargetStartDate())) {
            throw new BadRequestException("설문 시작일이 종료일보다 크거나 같습니다.");
        }
    }
}
