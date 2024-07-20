package ag.act.converter.admin;

import ag.act.dto.admin.UserDummyStockDto;
import ag.act.model.UserDummyStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDummyStockResponseConverter {

    public UserDummyStockResponse convert(UserDummyStockDto userDummyStockDto) {
        return new UserDummyStockResponse()
            .code(userDummyStockDto.getCode())
            .name(userDummyStockDto.getName())
            .quantity(userDummyStockDto.getQuantity())
            .referenceDate(userDummyStockDto.getReferenceDate())
            .registerDate(userDummyStockDto.getRegisterDate().toLocalDate());
    }
}
