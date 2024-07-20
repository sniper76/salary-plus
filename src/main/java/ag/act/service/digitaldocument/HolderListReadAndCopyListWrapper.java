package ag.act.service.digitaldocument;

import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.exception.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HolderListReadAndCopyListWrapper {
    private final Map<HolderListReadAndCopyItemType, String> holderListReadAndCopyMap;

    public HolderListReadAndCopyListWrapper(List<HolderListReadAndCopy> holderListReadAndCopyList) {
        holderListReadAndCopyMap = getHolderListReadAndCopyMap(holderListReadAndCopyList);
    }

    public String getLeaderName() {
        return getItemValue(HolderListReadAndCopyItemType.LEADER_NAME);
    }

    public String getCompanyName() {
        return getItemValue(HolderListReadAndCopyItemType.COMPANY_NAME);
    }

    public String getLeaderAddress() {
        return getItemValue(HolderListReadAndCopyItemType.LEADER_ADDRESS);
    }

    public String getCeoName() {
        return getItemValue(HolderListReadAndCopyItemType.CEO_NAME);
    }

    public String getCompanyAddress() {
        return getItemValue(HolderListReadAndCopyItemType.COMPANY_ADDRESS);
    }

    public String getIrPhoneNumber() {
        return getItemValue(HolderListReadAndCopyItemType.IR_PHONE_NUMBER);
    }

    public String getDeadlineDateByLeader1() {
        return getItemValue(HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER1);
    }

    public String getDeadlineDateByLeader2() {
        return getItemValue(HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER2);
    }

    public String getReferenceDateByLeader() {
        return getItemValue(HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER);
    }

    public String getLeaderEmail() {
        return getItemValue(HolderListReadAndCopyItemType.LEADER_EMAIL);
    }

    private String getItemValue(HolderListReadAndCopyItemType itemType) {
        return Optional.ofNullable(holderListReadAndCopyMap.get(itemType))
            .orElseThrow(() -> new BadRequestException("주주명부 열람/등사 등록시 필수 항목(%s)이 존재하지 않습니다.".formatted(itemType.getTitle())));
    }

    private Map<HolderListReadAndCopyItemType, String> getHolderListReadAndCopyMap(
        List<HolderListReadAndCopy> holderListReadAndCopyList
    ) {

        final Map<HolderListReadAndCopyItemType, String> requestMap = new HashMap<>();

        holderListReadAndCopyList.forEach(
            holderListReadAndCopy ->
                requestMap.put(
                    holderListReadAndCopy.getItemType(),
                    holderListReadAndCopy.getItemValue()
                )
        );

        return requestMap;
    }
}
