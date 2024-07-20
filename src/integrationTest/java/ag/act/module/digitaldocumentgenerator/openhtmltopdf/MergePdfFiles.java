package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import ag.act.exception.InternalServerException;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MergePdfFiles {

    public static void main(String[] args) throws IOException {
        final String sourceDirectoryPath = "/Users/yanggun7201/Downloads/pdfFiles";
        final String destinationDirectoryPath = "/Users/yanggun7201/Downloads";

        FileUtils.writeByteArrayToFile(
            new File(destinationDirectoryPath + "/merged.pdf"),
            merge(readAllFiles(new File(sourceDirectoryPath)))
        );
        System.out.println("Pdf file is successfully merged and created.");
    }

    private static List<byte[]> readAllFiles(File rootDirectory) throws IOException {
        List<byte[]> sources = new ArrayList<>();

        for (File file : Objects.requireNonNull(rootDirectory.listFiles())) {
            sources.add(FileUtils.readFileToByteArray(file));
        }
        return sources;
    }

    private static byte[] merge(List<byte[]> sources) {
        final PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        final ByteArrayOutputStream destinationStream = new ByteArrayOutputStream();
        pdfMergerUtility.setDestinationStream(destinationStream);

        sources.forEach(source -> pdfMergerUtility.addSource(new ByteArrayInputStream(source)));

        try {
            pdfMergerUtility.mergeDocuments(null);
            return destinationStream.toByteArray();
        } catch (Exception e) {
            throw new InternalServerException("Failed to merge PDFs", e);
        }
    }
}
