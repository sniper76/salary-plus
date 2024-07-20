package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Component;

@SuppressWarnings("AbbreviationAsWordInName")
@Component
public class PDFMergerUtilityFactory {

    public PDFMergerUtility create() {
        return new PDFMergerUtility();
    }
}
