package ag.act.converter;

import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.model.UserStockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserStockResponseConverter implements Converter<JsonMyDataStock, ag.act.model.UserStockResponse> {

    @Override
    public ag.act.model.UserStockResponse apply(JsonMyDataStock myDataStock) {
        ag.act.model.UserStockResponse userStockResponse = new UserStockResponse();
        userStockResponse.setCode(myDataStock.getCode());
        userStockResponse.setName(myDataStock.getName());
        userStockResponse.setQuantity(myDataStock.getQuantity());
        userStockResponse.setRegisterDate(myDataStock.getRegisterDate());
        userStockResponse.setReferenceDate(myDataStock.getReferenceDate());

        return userStockResponse;
    }
}
