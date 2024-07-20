package ag.act.enums;

import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import lombok.Getter;

import java.util.List;

@Getter
public enum ProgressRecordFillType {
    SMS_VERIFICATION(
        "%s님이 휴대폰 본인 인증을 완료하였습니다.",
        VerificationOperationType.REGISTER,
        VerificationType.SMS,
        1
    ),
    PIN_REGISTER(
        "%s님이 PIN번호 설정을 완료하였습니다.",
        VerificationOperationType.REGISTER,
        VerificationType.PIN,
        2
    ),
    PIN_VERIFICATION(
        "%s님이 PIN번호를 통해 본인 인증을 완료하였습니다.",
        VerificationOperationType.VERIFICATION,
        VerificationType.PIN,
        3
    ),
    DOCUMENT_CREATION(
        "%s님이 전자문서에 서명을 완료하였습니다.",
        VerificationOperationType.SIGNATURE_SAVE,
        VerificationType.SIGNATURE,
        4
    ),
    DOCUMENT_SUBMISSION(
        "%s님이 전자문서를 제출하였습니다.",
        VerificationOperationType.SIGNATURE,
        VerificationType.SIGNATURE,
        5
    );

    private final String format;
    private final VerificationOperationType operationType;
    private final VerificationType verificationType;
    private final int order;

    ProgressRecordFillType(
        String format,
        VerificationOperationType operationType,
        VerificationType verificationType,
        int order
    ) {
        this.format = format;
        this.operationType = operationType;
        this.verificationType = verificationType;
        this.order = order;
    }

    public boolean isDocumentSpecific() {
        return  List.of(DOCUMENT_CREATION, DOCUMENT_SUBMISSION).contains(this);
    }
}
