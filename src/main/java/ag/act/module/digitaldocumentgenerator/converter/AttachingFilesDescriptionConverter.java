package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import org.springframework.stereotype.Component;

@Component
public class AttachingFilesDescriptionConverter {
    public String convert(AttachingFilesDto attachingFilesDto) {
        if (attachingFilesDto == null) {
            return null;
        }

        if (isIdCardImageAttached(attachingFilesDto) && isBankAccountFileAttached(attachingFilesDto)) {
            return "신분증 사본, 잔고증명서/주식보유명세서 등 보유주식 증빙자료";
        } else if (isIdCardImageAttached(attachingFilesDto)) {
            return "신분증 사본";
        } else if (isBankAccountFileAttached(attachingFilesDto)) {
            return "잔고증명서/주식보유명세서 등 보유주식 증빙자료";
        }

        return null;
    }

    private boolean isIdCardImageAttached(AttachingFilesDto attachingFilesDto) {
        return attachingFilesDto.getIdCardImage() != null;
    }

    private boolean isBankAccountFileAttached(AttachingFilesDto attachingFilesDto) {
        return (attachingFilesDto.getBankAccountImages() != null
            || attachingFilesDto.getHectoEncryptedBankAccountPdf() != null);
    }
}
