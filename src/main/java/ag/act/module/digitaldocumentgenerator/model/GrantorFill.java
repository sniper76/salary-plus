package ag.act.module.digitaldocumentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrantorFill {
    private String name;
    private String birthDate;
    private Integer firstNumberOfIdentification;
    private DateFill signingDate;
    private String holdingStockQuantity;

    public static GrantorFill createPreview() {
        final GrantorFill grantorFill = new GrantorFill();
        grantorFill.setHoldingStockQuantity("100");
        grantorFill.setName("(위임인 이름)");
        grantorFill.setBirthDate("700101");
        grantorFill.setFirstNumberOfIdentification(1);
        grantorFill.setSigningDate(DateFill.createPreview());

        return grantorFill;
    }
}
