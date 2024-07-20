package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.model.DigitalDocumentUserResponse;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentUserResponseConverter {
    private final DecryptColumnConverter decryptColumnConverter;

    public DigitalDocumentUserResponseConverter(DecryptColumnConverter decryptColumnConverter) {
        this.decryptColumnConverter = decryptColumnConverter;
    }

    public DigitalDocumentUserResponse convert(User currentUser, boolean isSetAddress) {
        DigitalDocumentUserResponse response = new DigitalDocumentUserResponse()
            .id(currentUser.getId())
            .name(currentUser.getName())
            .gender(currentUser.getGender().getValue())
            .birthDate(DateTimeConverter.convert(currentUser.getBirthDate()))
            .phoneNumber(decryptColumnConverter.convert(currentUser.getHashedPhoneNumber()));
        if (isSetAddress) {
            response
                .zipcode(currentUser.getZipcode())
                .address(currentUser.getAddress())
                .addressDetail(currentUser.getAddressDetail());
        }
        return response;
    }
}
