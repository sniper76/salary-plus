package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.service.admin.CorporateUserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DigitalDocumentAcceptUserResponseConverter {
    private final DecryptColumnConverter decryptColumnConverter;
    private final CorporateUserService corporateUserService;

    public DigitalDocumentAcceptUserResponse convert(@Nullable User acceptUser) {
        if (acceptUser == null) {
            return null;
        }
        final Optional<String> corporateNo = getCorporateNoByUserId(acceptUser);
        return new DigitalDocumentAcceptUserResponse()
            .id(acceptUser.getId())
            .name(acceptUser.getName())
            .phoneNumber(decryptColumnConverter.convert(acceptUser.getHashedPhoneNumber()))
            .birthDate(DateTimeConverter.convert(acceptUser.getBirthDate()))
            .corporateNo(corporateNo.orElse(null));
    }

    private Optional<String> getCorporateNoByUserId(@NotNull User acceptUser) {
        return Optional.ofNullable(corporateUserService.getNullableCorporateNoByUserId(acceptUser.getId()));
    }
}
