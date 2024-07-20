package ag.act.service.image;

import ag.act.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ImageExtractor {

    private static final String FILE_TYPE_PNG = "png";
    private static final int PDF_PAGE_INDEX = 0;
    private static final float DPI = 300;

    public byte[] extractPdfFirstPageImage(byte[] pdfBytes) {
        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(PDF_PAGE_INDEX, DPI, ImageType.RGB);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bim, FILE_TYPE_PNG, os);

            return os.toByteArray();
        } catch (Exception e) {
            throw new InternalServerException("pdf 파일에서 첫 페이지의 image 생성중 오류가 발생하였습니다.", e);
        }
    }
}
