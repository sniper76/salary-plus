package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.constants.DigitalDocumentTemplateNames;
import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.exception.InternalServerException;
import ag.act.model.HolderListReadAndCopyFormResponse;
import ag.act.module.digitaldocumentgenerator.freemarker.ActFreeMarkerConfiguration;
import ag.act.util.HtmlContentUtil;
import ag.act.util.XHTMLFormatUtil;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HolderListReadAndCopyFormService implements DigitalDocumentTemplateNames {
    private final ActFreeMarkerConfiguration digitalDocumentFreeMarkerConfiguration;
    private final HolderListReadAndCopyItemValueHandlerResolver holderListReadAndCopyItemValueHandlerResolver;

    public HolderListReadAndCopyFormResponse getHolderListReadAndCopyForm(String stockCode, String boardGroupName) {
        final List<HolderListReadAndCopyItemWithValue> itemWithValues = HolderListReadAndCopyItemType.getSortedList()
            .stream()
            .map(holderListReadAndCopyItemValueHandlerResolver::resolve)
            .map(holderListReadAndCopyItemValueHandler -> holderListReadAndCopyItemValueHandler.handle(stockCode))
            .toList();

        int index = 0;
        final HolderListReadAndCopyDataModel dataModel = new HolderListReadAndCopyDataModel();
        dataModel.setLeaderName(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setCompanyName(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setLeaderAddress(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setCeoName(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setCompanyAddress(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setIrPhoneNumber(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setDeadlineDateByLeader1(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setDeadlineDateByLeader2(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setReferenceDateByLeader(convertToButtonTag(itemWithValues.get(index++)));
        dataModel.setLeaderEmail(convertToButtonTag(itemWithValues.get(index)));

        return new HolderListReadAndCopyFormResponse()
            .content(HtmlContentUtil.unescape(fillAndGetHtmlBodyString(dataModel)));
    }

    @SuppressWarnings("LineLength")
    private String convertToButtonTag(HolderListReadAndCopyItemWithValue itemWithValue) {
        HolderListReadAndCopyItemType itemType = itemWithValue.itemType();
        List<String> values = itemWithValue.values();

        return """
            <button id='%s' item-values='[%s]' item-type='%s' data-type='%s' data-max-length='%s' hint-message='%s' description='%s' min-datetime-constraint='%s' max-datetime-constraint='%s'>
            %s
            </button>
            """
            .formatted(
                itemType.name(),
                getFormattedValues(values),
                itemType.getHolderListReadAndCopyItemAnswerType(),
                itemType.getDataType(),
                itemType.getDataMaxLength(),
                itemType.getMessage(),
                itemType.getDescription(),
                itemType.getMinDatetimeConstraint(),
                itemType.getMaxDatetimeConstraint(),
                itemType.getTitle()
            );
    }

    private String getFormattedValues(List<String> values) {
        return values.stream()
            .filter(StringUtils::isNotBlank)
            .map("\"%s\""::formatted)
            .collect(Collectors.joining(","));
    }

    private String fillAndGetHtmlBodyString(Object dataModel) {
        final StringWriter out = fillTemplate(dataModel, getTemplate());

        return XHTMLFormatUtil.getHtmlById(out.toString());
    }

    private StringWriter fillTemplate(Object dataModel, Template template) {
        final StringWriter out = new StringWriter();
        try {
            template.process(dataModel, out);
        } catch (Exception e) {
            throw new InternalServerException("Failed to fill template", e);
        }
        return out;
    }

    private Template getTemplate() {
        try {
            return digitalDocumentFreeMarkerConfiguration.getTemplate(HOLDER_LIST_READ_AND_COPY_TEMPLATE);
        } catch (Exception e) {
            throw new InternalServerException(String.format("Failed to get template name %s", HOLDER_LIST_READ_AND_COPY_TEMPLATE), e);
        }
    }
}
