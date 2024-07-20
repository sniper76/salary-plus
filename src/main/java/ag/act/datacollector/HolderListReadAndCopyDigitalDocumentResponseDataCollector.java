package ag.act.datacollector;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.HolderListReadAndCopyDigitalDocumentResponseData;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.exception.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HolderListReadAndCopyDigitalDocumentResponseDataCollector {

    public Optional<HolderListReadAndCopyDigitalDocumentResponseData> collect(DigitalDocument digitalDocument) {
        return findHolderListReadAndCopyDigitalDocumentUser(digitalDocument).map(digitalDocumentUser ->
            new HolderListReadAndCopyDigitalDocumentResponseData(
                digitalDocumentUser.getDigitalDocumentId(),
                getFileNameWithExtension(digitalDocumentUser),
                digitalDocumentUser.getUserId()
            )
        );
    }

    private Optional<DigitalDocumentUser> findHolderListReadAndCopyDigitalDocumentUser(DigitalDocument digitalDocument) {
        return digitalDocument.getDigitalDocumentUserList()
            .stream()
            .findFirst()
            .filter(digitalDocumentUser -> isVisible(digitalDocumentUser, ActUserProvider.getNoneNull()));
    }

    private String getFileNameWithExtension(DigitalDocumentUser digitalDocumentUser) {
        return FilenameUtils.getName(getPdfPath(digitalDocumentUser));
    }

    private String getPdfPath(DigitalDocumentUser digitalDocumentUser) {
        return Optional.ofNullable(digitalDocumentUser.getPdfPath())
            .orElseThrow(() -> new BadRequestException("주주명부 열람/등사 청구 파일의 경로를 알 수 없습니다. 고객센터에 문의해주세요."));
    }

    private boolean isVisible(DigitalDocumentUser digitalDocumentUser, User user) {
        final boolean isAdminInCms = RequestContextHolder.isCmsApi() && user.isAdmin();
        final boolean isDigitalDocumentUser = digitalDocumentUser.getUserId().equals(user.getId());

        return isAdminInCms || isDigitalDocumentUser;
    }
}
