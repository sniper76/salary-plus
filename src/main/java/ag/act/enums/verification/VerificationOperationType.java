package ag.act.enums.verification;

import lombok.Getter;

@Getter
public enum VerificationOperationType {
    REGISTER("등록"),
    VERIFICATION("인증"),
    SIGNATURE_SAVE("전자문서서명완료"),
    SIGNATURE("전자문서제출");

    private final String value;

    VerificationOperationType(String value) {
        this.value = value;
    }
}
