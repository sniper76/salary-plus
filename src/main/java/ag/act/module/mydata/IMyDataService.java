package ag.act.module.mydata;

public interface IMyDataService {
    int MY_DATA_MAX_RETRY_COUNT = 3;
    int MY_DATA_BACKOFF_DELAY_MS = 1000;
    long TIMEOUT_IN_SECONDS = 5L;
    String MY_DATA_SUCCESS_CODE = "FP-00000";
    String COMMON_ERROR_MESSAGE = "현재 마이데이터 서버와 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.";
    String UNKNOWN_GET_FINPONG_ACCESS_TOKEN_ERROR_MESSAGE = "마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다.";
    String UNKNOWN_WITHDRAW_ERROR_MESSAGE = "마이데이터 연동 취소 요청 중에 알 수 없는 오류가 발생하였습니다.";
}
