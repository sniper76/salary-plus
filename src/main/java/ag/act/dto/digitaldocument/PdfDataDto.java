package ag.act.dto.digitaldocument;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PdfDataDto {
    private String path;
    private byte[] pdfBytes;
    private byte[] pdfMaskingBytes;
    private Long originalPageCount;
    private Long attachmentPageCount;
}
