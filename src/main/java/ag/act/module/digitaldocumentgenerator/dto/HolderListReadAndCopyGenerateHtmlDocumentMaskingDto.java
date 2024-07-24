package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
public class HolderListReadAndCopyGenerateHtmlDocumentMaskingDto extends HolderListReadAndCopyGenerateHtmlDocumentDto {
    @Delegate
    private final HolderListReadAndCopyGenerateHtmlDocumentDto holderListReadAndCopyGenerateHtmlDocumentDto;
    private final HolderListReadAndCopyDataModel holderListReadAndCopyDataModel;

    public HolderListReadAndCopyGenerateHtmlDocumentMaskingDto(
        HolderListReadAndCopyGenerateHtmlDocumentDto holderListReadAndCopyGenerateHtmlDocumentDto
    ) {
        this.holderListReadAndCopyGenerateHtmlDocumentDto = holderListReadAndCopyGenerateHtmlDocumentDto;
        this.holderListReadAndCopyDataModel = new HolderListReadAndCopyMaskingDataModel(
            holderListReadAndCopyGenerateHtmlDocumentDto.getHolderListReadAndCopyDataModel()
        );
    }
}
