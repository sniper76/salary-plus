package ag.act.converter.digitaldocument;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.model.DigitalDocumentUserDetailsResponse;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentUserDetailsResponseConverter implements Converter<DigitalDocumentUser, DigitalDocumentUserDetailsResponse> {
    private final DecryptColumnConverter decryptColumnConverter;

    public DigitalDocumentUserDetailsResponseConverter(DecryptColumnConverter decryptColumnConverter) {
        this.decryptColumnConverter = decryptColumnConverter;
    }

    public DigitalDocumentUserDetailsResponse convert(DigitalDocumentUser digitalDocumentUser) {
        return new DigitalDocumentUserDetailsResponse()
            .id(digitalDocumentUser.getId())
            .userId(digitalDocumentUser.getUserId())
            .digitalDocumentId(digitalDocumentUser.getDigitalDocumentId())
            .name(digitalDocumentUser.getName())
            .gender(digitalDocumentUser.getGender().getValue())
            .birthDate(DateTimeConverter.convert(digitalDocumentUser.getBirthDate()))
            .zipcode(digitalDocumentUser.getZipcode())
            .address(digitalDocumentUser.getAddress())
            .addressDetail(digitalDocumentUser.getAddressDetail())
            .issuedNumber(digitalDocumentUser.getIssuedNumber())
            .phoneNumber(decryptColumnConverter.convert(digitalDocumentUser.getHashedPhoneNumber()));
    }

    @Override
    public DigitalDocumentUserDetailsResponse apply(DigitalDocumentUser digitalDocumentUser) {
        return this.convert(digitalDocumentUser);
    }
}
