package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.converter.Converter;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.datetimeprovider.DigitalDocumentFillCurrentDateTimeProvider;
import ag.act.module.digitaldocumentgenerator.model.DateFill;
import ag.act.module.digitaldocumentgenerator.model.GrantorFill;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.DateTimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

@Component
@RequiredArgsConstructor
public class GrantorFillConverter implements Converter<DigitalDocumentUser, GrantorFill> {
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final UserHoldingStockService userHoldingStockService;
    private final DigitalDocumentFillCurrentDateTimeProvider digitalDocumentFillCurrentDateTimeProvider;

    @Override
    public GrantorFill apply(DigitalDocumentUser digitalDocumentUser) {
        return convert(digitalDocumentUser);
    }

    private GrantorFill convert(DigitalDocumentUser digitalDocumentUser) {
        final GrantorFill grantorFill = new GrantorFill();

        grantorFill.setName(digitalDocumentUser.getName());
        grantorFill.setBirthDate(DateTimeFormatUtil.yyMMdd().format((digitalDocumentUser.getBirthDate())));
        grantorFill.setFirstNumberOfIdentification(digitalDocumentUser.getFirstNumberOfIdentification());
        grantorFill.setSigningDate(new DateFill(digitalDocumentFillCurrentDateTimeProvider.getKoreanDateTime()));

        final NumberFormat formatter = NumberFormat.getInstance();
        grantorFill.setHoldingStockQuantity(
            formatter.format(getGrantorHoldingStockQuantity(digitalDocumentUser))
        );

        return grantorFill;
    }

    private Long getGrantorHoldingStockQuantity(DigitalDocumentUser digitalDocumentUser) {
        final DigitalDocument digitalDocument = digitalDocumentUser.getDigitalDocument();

        if (digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY) {
            return userHoldingStockOnReferenceDateService.getUserHoldingStockOnReferenceDate(
                    digitalDocumentUser.getUserId(), digitalDocument.getStockCode(), digitalDocument.getStockReferenceDate()
                )
                .map(UserHoldingStockOnReferenceDate::getQuantity)
                .orElse(0L);
        }
        return userHoldingStockService.getUserHoldingStock(digitalDocumentUser.getUserId(), digitalDocument.getStockCode())
            .getQuantity();
    }
}
