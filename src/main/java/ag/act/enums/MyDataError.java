package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum MyDataError {
    FP_01101("FP-01101", "사용권한 정보가 존재하지 않습니다. 관리자에게 문의하세요.", "사용권한 정보가 존재하지 않습니다."),
    FP_01102("FP-01102", "사용권한 정보가 올바르지 않습니다. 관리자에게 문의하세요.", "사용권한 정보가 올바르지 않습니다."),
    FP_01200("FP-01200", "회원 정보 저장중 오류가 발생하였습니다. 관리자에게 문의하세요.", "회원 정보 저장중 오류가 발생하였습니다."),
    FP_01201("FP-01201", "회원 고유값이 없습니다.", "회원 고유값이 없습니다."),
    FP_01202("FP-01202", "회원 이름이 없습니다.", "회원 이름이 없습니다."),
    FP_01203("FP-01203", "회원 전화번호가 없습니다.", "회원 전화번호가 없습니다."),
    FP_01204("FP-01204", "회원 식별 연계정보가 없습니다.", "회원 식별 연계정보가 없습니다."),
    FP_01205("FP-01205", "회원 성별 정보가 없습니다.", "회원 성별 정보가 없습니다."),
    FP_01206("FP-01206", "회원 생년월일 정보가 없습니다.", "회원 생년월일 정보가 없습니다."),
    FP_01207("FP-01207", "회원 이메일이 없습니다.", "회원 이메일이 없습니다."),
    FP_01208("FP-01208", "마이데이터 미연동 고객입니다.", "마이데이터 미연동 고객입니다."),
    FP_05016("FP-05016", "만19세 미만은 가입할수 없습니다.", "만19세 미만은 가입할수 없습니다."),
    FP_50000("FP-50000", Constants.DEFAULT_MESSAGE, "마이데이터 전송 중 오류가 발생하였습니다."),
    FP_50001("FP-50001", Constants.DEFAULT_MESSAGE, "요청중 오류가 발생하였습니다."),
    FP_05023("FP-05023", "회원 USER ID값이 올바르지 않습니다. 관리자에게 문의하세요.", "회원 USER ID값이 올바르지 않습니다. 관리자에게 문의하세요."),
    FP_04500("FP-04500", "존재하지 않는 고객사입니다.", "마이데이터 전송 중 오류가 발생하였습니다."),
    FP_99999("FP-99999", Constants.DEFAULT_MESSAGE, "서버 처리 중 오류가 발생했습니다. 관리자에게 문의하세요."),
    UNKNOWN("UNKNOWN", Constants.DEFAULT_MESSAGE, "UNKNOWN");

    private final String code;
    private final String message;
    private final String errorMessage;

    MyDataError(String code, String message, String errorMessage) {
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MyDataError fromValue(String value) {
        for (MyDataError b : MyDataError.values()) {
            if (b.getCode().equals(value)) {
                return b;
            }
        }

        log.error("Unknown MyDataError code: {}", value);
        return UNKNOWN;
    }

    private static class Constants {
        private static final String DEFAULT_MESSAGE = "연동 중에 알 수 없는 오류가 발생하였습니다. 관리자에게 문의하세요.";
    }
}
