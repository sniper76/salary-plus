package ag.act.module.markany.dna;

public record MarkAnyDigitalDocument(Long documentId, Long userId, Long issuedNumber, byte[] pdfBytes) {
}
