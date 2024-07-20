package ag.act.module.user.documentdownload.solidarityleaderconfidentialagreement;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserSolidarityLeaderConfidentialAgreementDocumentDownloadValidator {
    public void validateUser(User user) {
        if (!isSolidarityLeaderConfidentialAgreementDocumentSigned(user)) {
            throw new BadRequestException("회원이 비밀유지서약서를 서명하지 않았습니다.");
        }
    }

    private boolean isSolidarityLeaderConfidentialAgreementDocumentSigned(User user) {
        return Objects.equals(user.getIsSolidarityLeaderConfidentialAgreementSigned(), Boolean.TRUE);
    }

    @SuppressWarnings("OptionalAssignedToNull")
    public void validateFile(User user, @Nullable Optional<InputStream> inputStream) {
        if (inputStream == null || inputStream.isEmpty()) {
            throw new NotFoundException("%s(%s) 회원의 비밀유지서약서를 찾을 수 없습니다.".formatted(user.getName(), user.getId()));
        }
    }
}
