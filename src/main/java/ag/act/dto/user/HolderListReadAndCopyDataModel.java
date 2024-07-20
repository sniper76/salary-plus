package ag.act.dto.user;


import ag.act.service.digitaldocument.HolderListReadAndCopyListWrapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HolderListReadAndCopyDataModel {
    private String companyName;
    private String leaderName;
    private String leaderAddress;
    private String ceoName;
    private String companyAddress;
    private String irPhoneNumber;
    private String deadlineDateByLeader1;
    private String deadlineDateByLeader2;
    private String referenceDateByLeader;
    private String leaderEmail;

    public HolderListReadAndCopyDataModel(HolderListReadAndCopyListWrapper holderListReadAndCopyListWrapper) {
        leaderName = holderListReadAndCopyListWrapper.getLeaderName();
        companyName = holderListReadAndCopyListWrapper.getCompanyName();
        leaderAddress = holderListReadAndCopyListWrapper.getLeaderAddress();
        ceoName = holderListReadAndCopyListWrapper.getCeoName();
        companyAddress = holderListReadAndCopyListWrapper.getCompanyAddress();
        irPhoneNumber = holderListReadAndCopyListWrapper.getIrPhoneNumber();
        deadlineDateByLeader1 = holderListReadAndCopyListWrapper.getDeadlineDateByLeader1();
        deadlineDateByLeader2 = holderListReadAndCopyListWrapper.getDeadlineDateByLeader2();
        referenceDateByLeader = holderListReadAndCopyListWrapper.getReferenceDateByLeader();
        leaderEmail = holderListReadAndCopyListWrapper.getLeaderEmail();
    }
}
