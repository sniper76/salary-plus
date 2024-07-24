package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class HolderListReadAndCopyMaskingDataModel extends HolderListReadAndCopyDataModel {
    @Delegate
    private final HolderListReadAndCopyDataModel holderListReadAndCopyDataModel;

    public String getLeaderName() {
        return "*".repeat(holderListReadAndCopyDataModel.getLeaderName().length());
    }

    public String getLeaderAddress() {
        return "*".repeat(holderListReadAndCopyDataModel.getLeaderAddress().length());
    }

    public String getLeaderEmail() {
        return "*".repeat(holderListReadAndCopyDataModel.getLeaderEmail().length());
    }
}
