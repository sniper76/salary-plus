package ag.act.module.markany.dna;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkAnyService {
    private final MarkAnyDNAClient markAnyDNAClient;
    private final MarkAnyNotifier markAnyNotifier;

    public byte[] makeDna(MarkAnyDigitalDocument markAnyDigitalDocument) {

        final byte[] pdfBytes = markAnyDigitalDocument.pdfBytes();

        try {
            return markAnyDNAClient.makeDna(pdfBytes);
        } catch (Exception ex) {
            markAnyNotifier.notify(markAnyDigitalDocument, ex);
            log.warn("Failed to convert to MarkAny DNA: ", ex);
        }

        return pdfBytes;
    }

    public byte[] makeDna(MarkAnyConfidentialAgreement markAnyConfidentialAgreement) {

        final byte[] pdfBytes = markAnyConfidentialAgreement.pdfBytes();

        try {
            return markAnyDNAClient.makeDna(pdfBytes);
        } catch (Exception ex) {
            markAnyNotifier.notify(markAnyConfidentialAgreement, ex);
            log.warn("Failed to convert to MarkAny DNA: ", ex);
        }

        return pdfBytes;
    }
}
