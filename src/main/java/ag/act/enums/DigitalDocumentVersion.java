package ag.act.enums;

import ag.act.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum DigitalDocumentVersion {
    V1,
    V2;

    public static DigitalDocumentVersion fromValue(String version) {
        try {
            return DigitalDocumentVersion.valueOf(version.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("존재하지 않는 전자문서 버전입니다.", e);
        }
    }
}
