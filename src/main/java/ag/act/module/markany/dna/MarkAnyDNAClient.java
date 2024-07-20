package ag.act.module.markany.dna;

import ag.act.exception.ActRuntimeException;
import ag.act.exception.MarkAnyDNAException;
import com.markany.dna.MaDNAClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@SuppressWarnings({"LocalVariableName", "SameParameterValue", "SpellCheckingInspection", "checkstyle:MemberName"})
@Slf4j
@Component
@RequiredArgsConstructor
public class MarkAnyDNAClient {
    private static final int PI_FPS_SERVER_PORT = 18200;
    private static final int PI_2_D_CELL_COUNT = 280;
    private static final int PI_2_D_CELL_ROW = 65;
    private static final int PI_2_D_POS_X = 130;
    private static final int PI_2_D_POS_Y = 60;
    private static final String PSTR_FPS_SERVER_IP = "127.0.0.1";
    private static final String PSTR_USER_PASSWORD = "0";
    private static final String PSTR_PDF_CREATOR = "MarkAny";
    private static final int PI_FILE_ENC_FLAG = 0;
    private static final int PI_AUTO_PRINT = 0;
    private static final int PI_USE_MIX_DIMENSION = 0; //1;
    private static final int PI_USE_RELEASE_PDF = 0; // 0: network, 1: local. (비동기, network 시 결과파일 수령불가)
    private static final String PSTR_RCV_RESULT_URL = "";
    private static final String STR_JSON_RECEIVE_DATA = "";
    private static final boolean ASYNC_MODE = false; // true:비동기, false:동기
    private static final boolean VISIBLE_2D_WATERMARK = true; // true:visible 2D in PDF, false:invisible 2D in PDF
    private static final int POST_SEND_METHOD = 1; // 0 : GET, 1: POST
    private static final int DNA_OPTION = 1; // 1: DNA, 2: DNA+FPS, 3: FPS, 4:DNA Verify

    private final MarkAnyConfig markAnyConfig;

    public byte[] makeDna(byte[] inputBytes) {
        if (!markAnyConfig.isEnabled()) {
            return inputBytes;
        }

        final String watermarkImagePath = markAnyConfig.getWatermark(); // 2D Watermark Image Path
        final String serverIps = markAnyConfig.getHosts(); // MarkAny 서버모듈 설치 IP
        final int serverPort = markAnyConfig.getPort(); // MarkAny 서버모듈 PORT
        final MaDNAClient clMaDNAClient = MaDNAClient.getInstance();

        try {
            final String strWMData = makeWMParam(clMaDNAClient, watermarkImagePath);
            final String strGuideData = makeGuideStringParam(clMaDNAClient);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            String dnaResult = clMaDNAClient.MaMakeDNAv3(
                serverIps,
                serverPort,
                inputBytes,
                inputBytes.length,
                byteArrayOutputStream,
                DNA_OPTION,
                PSTR_FPS_SERVER_IP,
                PI_FPS_SERVER_PORT,
                PI_2_D_CELL_COUNT,
                PI_2_D_CELL_ROW,
                PI_2_D_POS_X,
                PI_2_D_POS_Y,
                PSTR_USER_PASSWORD,
                PSTR_PDF_CREATOR,
                PI_FILE_ENC_FLAG,
                PI_AUTO_PRINT,
                PI_USE_MIX_DIMENSION,
                PI_USE_RELEASE_PDF,
                ASYNC_MODE,
                POST_SEND_METHOD,
                PSTR_RCV_RESULT_URL,
                STR_JSON_RECEIVE_DATA,
                "addinfo",
                VISIBLE_2D_WATERMARK,
                strWMData,
                strGuideData
            );

            validateResultAndLogging(dnaResult, serverIps, serverPort);

            return byteArrayOutputStream.toByteArray();
        } catch (UnsatisfiedLinkError e) {
            log.error("[MarkAny] An error while binding method on [{}][{}]", serverIps, serverPort, e);
            throw new MarkAnyDNAException("위변조 방지 처리중에 알 수 없는 오류가 발생하였습니다. on[%s][%s]".formatted(serverIps, serverPort), e);
        } catch (ActRuntimeException e) {
            log.error("[MarkAny] An error occurred while creating the file: {} on [{}][{}]", e.getMessage(), serverIps, serverPort, e);
            throw e;
        }
    }

    private void validateResultAndLogging(String dnaResult, String serverIps, int serverPort) {
        final String strRetCode = getResultCode(dnaResult);
        final int iRet = Integer.parseInt(strRetCode);

        if (iRet != 0) {
            log.error("[MarkAny] failed = [{}] on [{}][{}]", iRet, serverIps, serverPort);
            throw new MarkAnyDNAException("위변조 방지 처리중에 알 수 없는 오류가 발생하였습니다.(%s) on[%s][%s]".formatted(iRet, serverIps, serverPort));
        }

        log.debug("[MarkAny] issueNo = [{}] is finisehd on [{}][{}]", strRetCode, serverIps, serverPort);
    }

    private String makeGuideStringParam(MaDNAClient clMaDNAClient) {
        int iPosXGuide1 = 15;
        int iPosYGuide1 = 283;
        final String strGuide1 = "※전자문서의 진위 확인은 연구원 웹사이트(cs.ktc.re.kr), 스마트폰 검증 앱(MaSmartDetector)을 통하여 확인할 수 있습니다.";

        return "1009" + clMaDNAClient.MakeGuideStringParam(iPosXGuide1, iPosYGuide1, strGuide1);
    }

    private String makeWMParam(MaDNAClient clMaDNAClient, String strImagePath) {
        int iPosX = 45;
        int iPosY = 60;
        int iWidth = 68;
        int iHeight = 34;
        float fOpacity = 1.0f;

        return "1" + clMaDNAClient.MakeWMParam(iPosX, iPosY, iWidth, iHeight, fOpacity, strImagePath);
    }

    private String getResultCode(String dnaResult) {
        final boolean isNegative = dnaResult.startsWith("-");

        return dnaResult.substring(0, isNegative ? 6 : 5);
    }
}
