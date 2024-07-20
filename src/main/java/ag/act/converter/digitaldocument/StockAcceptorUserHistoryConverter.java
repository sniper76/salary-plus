package ag.act.converter.digitaldocument;

import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import org.springframework.stereotype.Component;

@Component
public class StockAcceptorUserHistoryConverter {

    public User convertToUser(StockAcceptorUserHistory stockAcceptorUserHistory) {
        final User user = new User();
        user.setId(stockAcceptorUserHistory.getUserId());
        user.setName(stockAcceptorUserHistory.getName());
        user.setBirthDate(stockAcceptorUserHistory.getBirthDate());
        user.setHashedPhoneNumber(stockAcceptorUserHistory.getHashedPhoneNumber());
        user.setFirstNumberOfIdentification(stockAcceptorUserHistory.getFirstNumberOfIdentification());
        return user;
    }
}
