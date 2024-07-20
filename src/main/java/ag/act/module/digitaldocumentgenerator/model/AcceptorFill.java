package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AcceptorFill {
    private String name;
    private String birthDate;
    private Integer firstNumberOfIdentification;
    private String birthDateOrCorporateNo;
    private String birthDateOrCorporateNoForDigitalProxy;
    private String birthDateOrCorporateNoForJointOwnership;
    private String corporateNo;

    public static AcceptorFill createPreview(String corporateNo) {
        final AcceptorFill acceptorFill = new AcceptorFill();

        acceptorFill.setName(corporateNo == null ? "(수임인 이름)" : "(수임인 법인명)");
        acceptorFill.setBirthDate("701230");
        acceptorFill.setFirstNumberOfIdentification(1);
        acceptorFill.setCorporateNo(corporateNo);

        return acceptorFill;
    }
}
