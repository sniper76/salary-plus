package ag.act.util;

import ag.act.enums.FileType;
import ag.act.module.digitaldocumentgenerator.dto.DigitalDocumentFilenameDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FilenameUtil {

    public static final String YYMMDD = "yyMMdd";

    public static String getHolderListReadAndCopyFilename(String stockName, String suffix, String extension) {
        final String filename = refineFileName(
            "%s_%s_%s".formatted(
                stockName,
                suffix,
                "주주명부열람등사_청구"
            )
        );

        return filename + "." + extension;
    }

    public static String getFilenameWithDate(String prefix, String extension) {
        final String filename = refineFileName(
            "%s_%s".formatted(
                prefix,
                DateTimeUtil.getFormattedCurrentTimeInKorean(YYMMDD)
            )
        );

        return filename + "." + extension;
    }

    public static String getFilenameWithDate(String prefix, String extension, LocalDate date) {
        final String filename = refineFileName(
            "%s_%s".formatted(
                prefix,
                DateTimeUtil.formatLocalDate(date, YYMMDD)
            )
        );

        return filename + "." + extension;
    }

    public static String getFilename(String prefix, String name, String extension) {
        final String filename = refineFileName(
            "%s_%s_%s".formatted(
                prefix,
                name,
                DateTimeUtil.getFormattedCurrentTimeInKorean(YYMMDD)
            )
        );

        return filename + "." + extension;
    }

    // ex) 이화전기공업_2023제1차임시주주총회위임장_홍길동(980813)_230807.pdf
    public static String getDigitalDocumentFilename(DigitalDocumentFilenameDto dto) {
        final String filename = refineFileName(
            "%s_%s_%s(%s)_%s".formatted(
                dto.getStockName(),
                dto.getPostTitle(),
                dto.getUserName(),
                DateTimeFormatUtil.yyMMdd().format((dto.getUserBirthDate())),
                DateTimeUtil.getFormattedCurrentTimeInKorean(YYMMDD)
            )
        );

        return filename + ".pdf";
    }

    // ex) 이화전기공업_2023제1차임시주주총회위임장_홍길동(980813)_전자문서인증서_230807.pdf
    public static String getDigitalDocumentCertificationFilename(DigitalDocumentFilenameDto dto) {
        String currentTimeInKorean = DateTimeUtil.getFormattedCurrentTimeInKorean(YYMMDD);
        return getDigitalDocumentCertificationFilename(dto, currentTimeInKorean);
    }

    private static String getDigitalDocumentCertificationFilename(DigitalDocumentFilenameDto dto, String currentTimeInKorean) {
        final String filename = refineFileName(
            "%s_%s_%s(%s)_전자문서인증서_%s".formatted(
                dto.getStockName(),
                dto.getPostTitle(),
                dto.getUserName(),
                DateTimeFormatUtil.yyMMdd().format((dto.getUserBirthDate())),
                currentTimeInKorean
            )
        );
        return filename + ".pdf";
    }

    public static String getDigitalDocumentCertificationFilename(
        DigitalDocumentFilenameDto dto,
        LocalDateTime certificationCreatedAt
    ) {
        String certificationDocumentCreatedAtStr = DateTimeUtil.formatLocalDateTime(certificationCreatedAt, YYMMDD);
        return getDigitalDocumentCertificationFilename(dto, certificationDocumentCreatedAtStr);
    }

    public static String refineFileName(String fileName) {
        return fileName
            .replaceAll("\\s+", "")
            .replaceAll("[^a-zA-Z0-9가-힣_()]+", "")
            .replaceAll("^_+|_+$", "");
    }

    public static String getDigitalDocumentUploadPath(Long digitalDocumentId, String originalFilename) {
        return "%s/source/%s".formatted(
            getDigitalDocumentIdPath(digitalDocumentId), originalFilename
        );
    }

    public static String getDigitalDocumentIdPath(Long digitalDocumentId) {
        return "%s/%s".formatted(
            FileType.DIGITAL_DOCUMENT.getPathPrefix(), digitalDocumentId
        );
    }

    public static String getSolidarityLeaderConfidentialAgreementDocumentPath(Long userId, String userName) {
        return "%s/%s/비밀유지서약서(%s).pdf".formatted(
            FileType.SOLIDARITY_LEADER_CONFIDENTIAL_AGREEMENT_SIGN.getPathPrefix(),
            userId,
            userName
        );
    }
}
