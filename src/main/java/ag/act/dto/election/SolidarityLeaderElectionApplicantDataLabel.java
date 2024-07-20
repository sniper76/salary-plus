package ag.act.dto.election;

import ag.act.model.SolidarityLeaderElectionApplicantDataLabelResponse;

public record SolidarityLeaderElectionApplicantDataLabel(
    String requiredStockQuantityRatio,
    Long stockQuantity,
    String label,
    String unit,
    String valueText,
    String color
) {

    public SolidarityLeaderElectionApplicantDataLabel(
        Long stockQuantity,
        String label,
        String unit,
        String valueText,
        String color
    ) {
        this(null, stockQuantity, label, unit, valueText, color);
    }

    public SolidarityLeaderElectionApplicantDataLabelResponse toResponse() {
        return new SolidarityLeaderElectionApplicantDataLabelResponse()
            .requiredStockQuantityRatio(requiredStockQuantityRatio)
            .stockQuantity(stockQuantity)
            .label(label)
            .unit(unit)
            .valueText(valueText)
            .color(color);
    }
}
