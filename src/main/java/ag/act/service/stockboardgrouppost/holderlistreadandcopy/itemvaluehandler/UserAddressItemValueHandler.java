package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;
import ag.act.util.StringContentUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class UserAddressItemValueHandler implements HolderListReadAndCopyItemValueHandler {
    @Override
    public HolderListReadAndCopyItemType getItemType() {
        return HolderListReadAndCopyItemType.LEADER_ADDRESS;
    }

    @Override
    public boolean supports(HolderListReadAndCopyItemType itemType) {
        return getItemType() == itemType;
    }

    @Override
    public HolderListReadAndCopyItemWithValue handle(String stockCode) {
        return HolderListReadAndCopyItemWithValue.of(
            getItemType(),
            getFullAddress().orElse(null)
        );
    }

    private Optional<String> getFullAddress() {
        final User user = ActUserProvider.getNoneNull();

        if (isBlank(user.getAddress()) || isBlank(user.getZipcode())) {
            return Optional.empty();
        }

        return Optional.of(
            StringContentUtil.removeSpecialCharacters(
                "%s %s %s".formatted(
                    user.getZipcode(),
                    user.getAddress(),
                    StringUtils.trimToEmpty(user.getAddressDetail())
                )
            )
        );
    }
}
