package ag.act.validator;

import ag.act.entity.ActEntity;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class DefaultObjectValidator {


    @SuppressWarnings("OptionalAssignedToNull")
    public <T> T validateAndGet(Optional<T> optionalT, String errorMessage) {
        return validateAndGet(optionalT, () -> new NotFoundException(errorMessage));
    }

    @SuppressWarnings("OptionalAssignedToNull")
    public <T> T validateAndGet(Optional<T> optionalT, Supplier<? extends ActRuntimeException> supplier) {
        validateNotNull(optionalT);
        return optionalT.orElseThrow(supplier);
    }

    public void validateStatus(ActEntity actEntity, List<Status> statuses, String errorMessage) {
        validateNotNull(actEntity);
        if (statuses.contains(actEntity.getStatus())) {
            throw new NotFoundException(errorMessage);
        }
    }

    public void validateNotEmpty(Collection<?> collection, String errorMessage) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new InternalServerException(errorMessage);
        }
    }

    private static void validateNotNull(Object value) {
        if (value == null) {
            throw new InternalServerException("데이터 처리중에 알 수 없는 오류가 발생하였습니다. 관리자에게 문의하세요.");
        }
    }

    public void validateNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new InternalServerException(errorMessage);
        }
    }
}
