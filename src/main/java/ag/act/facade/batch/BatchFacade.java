package ag.act.facade.batch;

import ag.act.dto.BatchParameter;
import ag.act.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestScope
@Transactional
public class BatchFacade {

    private final List<IBatchExecutor> batchExecutors;

    public String execute(BatchParameter batchParameter) {
        return batchExecutors.stream()
            .filter(batchExecutor -> batchExecutor.supports(batchParameter))
            .findFirst()
            .map(batchExecutor -> batchExecutor.execute(batchParameter))
            .orElseThrow(() -> new NotFoundException("Batch not found: " + batchParameter.getBatchName()));
    }
}
