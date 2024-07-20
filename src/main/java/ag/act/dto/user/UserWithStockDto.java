package ag.act.dto.user;

import ag.act.entity.User;

public record UserWithStockDto(User user, long holdingStockCount) {
    //
}
