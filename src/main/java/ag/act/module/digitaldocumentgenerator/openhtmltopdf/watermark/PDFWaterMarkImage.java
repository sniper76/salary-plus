package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public record PDFWaterMarkImage(PDImageXObject pdImage, float width, float height){
}
