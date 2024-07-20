package ag.act.facade.digitaldocument.holderlistreadandcopy;

import ag.act.enums.BoardCategory;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.JsonAttachOption;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.stock.StockService;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class HolderListReadAndCopyPostRequestGenerator {
    private static final JsonAttachOption JSON_ATTACH_OPTION = new JsonAttachOption()
        .signImage(AttachOptionType.REQUIRED.name())
        .idCardImage(AttachOptionType.REQUIRED.name())
        .bankAccountImage(AttachOptionType.OPTIONAL.name())
        .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name());

    private final ObjectMapperUtil objectMapperUtil;
    private final StockService stockService;

    public CreatePostRequest generate(
        String stockCode,
        String request
    ) {
        final CreateDigitalDocumentRequest createDigitalDocumentRequest = generateCreateDigitalDocumentRequest(stockCode, request);

        return new CreatePostRequest()
            .boardCategory(BoardCategory.HOLDER_LIST_READ_AND_COPY.name())
            .digitalDocument(createDigitalDocumentRequest)
            .title(createDigitalDocumentRequest.getTitle());
    }

    private CreateDigitalDocumentRequest generateCreateDigitalDocumentRequest(String stockCode, String request) {
        final SimpleStock stock = stockService.getSimpleStock(stockCode);
        final InstantPeriod instantPeriod = getInstantPeriod();
        final String title = getTitle(stock.getName());

        return readRequest(request)
            .title(title)
            .companyName(stock.getName())
            .type(DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT.name())
            .targetStartDate(instantPeriod.targetStartDate())
            .targetEndDate(instantPeriod.targetEndDate())
            .attachOptions(getAttachOptions());
    }

    private InstantPeriod getInstantPeriod() {
        final Instant currentTime = Instant.now();
        return new InstantPeriod(currentTime, currentTime.plusSeconds(60));
    }

    private JsonAttachOption getAttachOptions() {
        return JSON_ATTACH_OPTION;
    }

    private CreateDigitalDocumentRequest readRequest(String request) {
        try {
            return objectMapperUtil.readValue(request, CreateDigitalDocumentRequest.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("주주명부 열람/등사 등록 데이터를 확인하세요.", e);
        }
    }

    private String getTitle(String stockName) {
        return "%s 주주명부 열람/등사 청구 공문".formatted(stockName);
    }

    record InstantPeriod(Instant targetStartDate, Instant targetEndDate) {
    }
}
