package ag.act.core.aop;

import ag.act.dto.BatchParameter;
import ag.act.exception.BadRequestException;
import ag.act.exception.BatchExecutionException;
import ag.act.model.BatchRequest;
import ag.act.service.BatchLogService;
import ag.act.util.LogMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchLogAspect {
    private final BatchLogService batchLogService;

    @Around("execution(* ag.act.facade.batch.BatchFacade.*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) {

        final long startTime = System.currentTimeMillis();

        final BatchParameter batchParameter = (BatchParameter) joinPoint.getArgs()[0];
        final String batchName = batchParameter.getBatchName();
        final String batchGroupName = batchParameter.getBatchGroupName();
        final Integer batchPeriod = batchParameter.getBatchPeriod();
        final BatchRequest.PeriodTimeUnitEnum periodTimeUnit = batchParameter.getPeriodTimeUnit();
        final String periodTimeUnitName = periodTimeUnit == BatchRequest.PeriodTimeUnitEnum.HOUR ? "hours" : "minutes";

        log.info("[Batch] {} started", batchName);

        Long batchLogId = batchLogService.logIfCanProceed(batchParameter).orElseThrow(
            () -> new BadRequestException(
                "The batch was already processed - `%s` within `%s` %s.".formatted(batchName, batchPeriod, periodTimeUnitName)
            )
        );

        boolean isFailed = false;
        Object result = null;
        try {
            batchLogService.waitUntilSameGroupBatchIsFinishedExceptCurrentBatch(batchName, batchGroupName, batchLogId);
            result = joinPoint.proceed();
        } catch (Throwable e) {
            isFailed = true;
            batchLogService.logAfterThrowing(batchLogId, LogMessageUtil.getExceptionStackTrace(e));
            throw new BatchExecutionException("[%s] failed due to %s".formatted(batchName, e.getMessage()), e);
        } finally {
            if (!isFailed) {
                batchLogService.logAfterReturning(batchLogId, result);
            }
            log.info("[Batch] {} batch ended - elapsedTime: {} s", batchName, ((System.currentTimeMillis() - startTime) / 1000.0));
        }
        return result;
    }
}
