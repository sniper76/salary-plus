package ag.act.facade.batch;

import ag.act.dto.BatchParameter;

public interface IBatchExecutor extends IBatch {

    boolean supports(BatchParameter batchParameter);

    String execute(BatchParameter batchParameter);
}
