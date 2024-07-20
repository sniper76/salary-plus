package ag.act.converter.stock;

import ag.act.converter.digitaldocument.DigitalDocumentAcceptUserResponseConverter;
import ag.act.dto.admin.StockDetailsResponseDto;
import ag.act.model.StockDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockDetailsResponseConverter {
    private final SolidarityLeaderResponseConverter solidarityLeaderResponseConverter;
    private final SolidarityLeaderApplicantConverter solidarityLeaderApplicantConverter;
    private final SolidarityResponseConverter solidarityResponseConverter;
    private final DigitalDocumentAcceptUserResponseConverter digitalDocumentAcceptUserResponseConverter;

    public StockDetailsResponse convert(
        StockDetailsResponseDto stockDetailsResponseDto
    ) {
        return new StockDetailsResponse()
            .solidarity(solidarityResponseConverter.convert(stockDetailsResponseDto.solidarity()))
            .solidarityLeader(solidarityLeaderResponseConverter.convert(stockDetailsResponseDto.leader()))
            .acceptUser(digitalDocumentAcceptUserResponseConverter.convert(stockDetailsResponseDto.acceptUser()))
            .solidarityLeaderApplicants(solidarityLeaderApplicantConverter.convertList(stockDetailsResponseDto.applicants()))
            .todayDelta(stockDetailsResponseDto.todayDelta());
    }
}
