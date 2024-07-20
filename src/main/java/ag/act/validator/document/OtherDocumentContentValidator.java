package ag.act.validator.document;

import ag.act.exception.BadRequestException;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import com.openhtmltopdf.util.XRRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OtherDocumentContentValidator {
    private final PDFRenderService pdfRenderService;

    public OtherDocumentContentValidator(
        PDFRenderService pdfRenderService
    ) {
        this.pdfRenderService = pdfRenderService;
    }

    public void validate(String content) {
        try {
            validateParseHtml(content);
        } catch (XRRuntimeException e) {
            throw new BadRequestException("문서 내용 형식이 올바르지 않습니다. (%s)".formatted(
                getDetailMessage(e)
            ));
        } catch (Exception e) {
            log.error("Error occurred while parsing the other document content :: {}", e.getMessage(), e);
            throw new BadRequestException("문서 내용을 변환하는 중 에러가 발생하였습니다. 개발자에게 문의해 주세요.", e);
        }
    }

    private String getDetailMessage(XRRuntimeException exception) {
        return exception.getMessage().contains("root") ? "html 태그 형식을 확인하세요. 잘못된 예시: </br>, <br>" : exception.getMessage();
    }

    private void validateParseHtml(String content) {
        pdfRenderService.renderPdf(content);
    }
}
