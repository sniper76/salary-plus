package ag.act.dto.poll;

public record PollResultSummaryDto(
    int voteTotalCount,
    long voteSumStockQuantity
) {
}
