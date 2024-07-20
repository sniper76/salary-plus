package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.User;
import ag.act.module.digitaldocumentgenerator.model.AcceptorFill;
import ag.act.service.admin.CorporateUserService;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserHistoryService;
import ag.act.util.DateTimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcceptorFillConverter {
    private final CorporateUserService corporateUserService;
    private final StockAcceptorUserHistoryService stockAcceptorUserHistoryService;

    public AcceptorFill convert(String stockCode, Long acceptUserId) {
        final User acceptor = stockAcceptorUserHistoryService.getUserByStockAcceptorUserHistory(stockCode, acceptUserId);
        final String corporateNo = corporateUserService.getNullableCorporateNoByUserId(acceptor.getId());

        final AcceptorFill acceptorFill = new AcceptorFill();

        acceptorFill.setName(acceptor.getName());
        acceptorFill.setBirthDate(DateTimeFormatUtil.yyMMdd().format(acceptor.getBirthDate()));
        acceptorFill.setFirstNumberOfIdentification(acceptor.getFirstNumberOfIdentification());
        acceptorFill.setCorporateNo(corporateNo);

        return acceptorFill;
    }
}
