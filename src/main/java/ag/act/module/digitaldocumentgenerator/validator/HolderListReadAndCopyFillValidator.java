package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.IHolderListReadAndCopyGenerateHtmlDocumentDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class HolderListReadAndCopyFillValidator extends BaseDigitalDocumentFillValidator {

    @Override
    public void validateTypeSpecific(IGenerateHtmlDocumentDto dto) {
        validate((IHolderListReadAndCopyGenerateHtmlDocumentDto) dto);
    }

    private void validate(IHolderListReadAndCopyGenerateHtmlDocumentDto dto) {
        final HolderListReadAndCopyDataModel dataModel = dto.getHolderListReadAndCopyDataModel();
        if (dataModel == null) {
            throw new InternalServerException("주주명부 열람/등사 데이터가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getLeaderName())) {
            throw new InternalServerException("발신인 이름이 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getCompanyName())) {
            throw new InternalServerException("발행회사 이름이 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getLeaderAddress())) {
            throw new InternalServerException("발신인 주소가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getCeoName())) {
            throw new InternalServerException("발행회사 대표 이름이 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getCompanyAddress())) {
            throw new InternalServerException("발행회사 주소가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getIrPhoneNumber())) {
            throw new InternalServerException("발행회사 IR담당자 전화번호가 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getDeadlineDateByLeader1())) {
            throw new InternalServerException("발신인 정하는 기한의 연/월/일/시가 존재하지 않습니다. : 1");
        }

        if (StringUtils.isBlank(dataModel.getDeadlineDateByLeader2())) {
            throw new InternalServerException("발신인 정하는 기한의 연/월/일/시가 존재하지 않습니다. : 2");
        }

        if (StringUtils.isBlank(dataModel.getReferenceDateByLeader())) {
            throw new InternalServerException("발신인이 정하는 주주명부 기준일이 존재하지 않습니다.");
        }

        if (StringUtils.isBlank(dataModel.getLeaderEmail())) {
            throw new InternalServerException("발신인 메일주소가 존재하지 않습니다.");
        }
    }
}
