package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.IHolderListReadAndCopyGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.HolderListReadAndCopyFill;
import org.springframework.stereotype.Component;

@Component
public class HolderListReadAndCopyFillConverter extends BaseDigitalDocumentFillConverter {

    @Override
    public DigitalDocumentFill convert(IGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        return convertToFill((IHolderListReadAndCopyGenerateHtmlDocumentDto) generateHtmlDocumentDto);
    }

    private DigitalDocumentFill convertToFill(IHolderListReadAndCopyGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        final HolderListReadAndCopyDataModel dataModel = generateHtmlDocumentDto.getHolderListReadAndCopyDataModel();

        final HolderListReadAndCopyFill holderListReadAndCopyFill =
            (HolderListReadAndCopyFill) getBaseDigitalDocumentFill(
                generateHtmlDocumentDto
        );

        holderListReadAndCopyFill.setCompanyName(dataModel.getCompanyName());
        holderListReadAndCopyFill.setLeaderName(dataModel.getLeaderName());
        holderListReadAndCopyFill.setLeaderAddress(dataModel.getLeaderAddress());
        holderListReadAndCopyFill.setCeoName(dataModel.getCeoName());
        holderListReadAndCopyFill.setCompanyAddress(dataModel.getCompanyAddress());
        holderListReadAndCopyFill.setIrPhoneNumber(dataModel.getIrPhoneNumber());
        holderListReadAndCopyFill.setDeadlineDateByLeader1(dataModel.getDeadlineDateByLeader1());
        holderListReadAndCopyFill.setDeadlineDateByLeader2(dataModel.getDeadlineDateByLeader2());
        holderListReadAndCopyFill.setReferenceDateByLeader(dataModel.getReferenceDateByLeader());
        holderListReadAndCopyFill.setLeaderEmail(dataModel.getLeaderEmail());

        return holderListReadAndCopyFill;
    }
}
