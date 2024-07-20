package ag.act.entity.digitaldocument;

public interface IJointOwnershipDocument extends IDigitalDocument {

    Long getAcceptUserId();

    void setAcceptUserId(Long acceptUserId);

    String getContent();

    void setContent(String content);

    String getCompanyRegistrationNumber();

    void setCompanyRegistrationNumber(String companyRegistrationNumber);
}
