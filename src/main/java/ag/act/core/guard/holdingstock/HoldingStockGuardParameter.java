package ag.act.core.guard.holdingstock;

public record HoldingStockGuardParameter(
    String stockCode,
    Long postId,
    Long digitalDocumentId
) {
}
