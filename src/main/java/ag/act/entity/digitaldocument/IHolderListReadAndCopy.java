package ag.act.entity.digitaldocument;

public interface IHolderListReadAndCopy extends IDigitalDocument {

    String getLeaderName();

    void setLeaderName(String leaderName);

    String getLeaderAddress();

    void setLeaderAddress(String leaderAddress);

    String getCeoName();

    void setCeoName(String ceoName);

    String getCompanyAddress();

    void setCompanyAddress(String companyAddress);

    String getIrPhoneNumber();

    void setIrPhoneNumber(String irPhoneNumber);

    String getDeadlineDateByLeader();

    void setDeadlineDateByLeader(String deadlineDateByLeader);

    String getReferenceDateByLeader();

    void setReferenceDateByLeader(String referenceDateByLeader);

    String getLeaderEmail();

    void setLeaderEmail(String leaderEmail);
}
