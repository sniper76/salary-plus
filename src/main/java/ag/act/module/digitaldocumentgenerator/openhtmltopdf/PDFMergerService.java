package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import ag.act.exception.InternalServerException;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@SuppressWarnings("AbbreviationAsWordInName")
@Service
public class PDFMergerService {
    private final PDFMergerUtilityFactory pdfMergerUtilityFactory;

    public PDFMergerService(PDFMergerUtilityFactory pdfMergerUtilityFactory) {
        this.pdfMergerUtilityFactory = pdfMergerUtilityFactory;
    }

    public byte[] mergePdfSources(List<byte[]> sources) {
        PDFMergerUtility pdfMergerUtility = pdfMergerUtilityFactory.create();
        ByteArrayOutputStream destinationStream = new ByteArrayOutputStream();
        pdfMergerUtility.setDestinationStream(destinationStream);

        for (byte[] source : sources) {
            pdfMergerUtility.addSource(new ByteArrayInputStream(source));
        }

        try {
            pdfMergerUtility.mergeDocuments(null);
            return destinationStream.toByteArray();
        } catch (Exception e) {
            throw new InternalServerException("Failed to merge PDFs", e);
        }
    }
}
