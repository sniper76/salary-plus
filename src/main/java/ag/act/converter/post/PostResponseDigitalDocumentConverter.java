package ag.act.converter.post;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.digitaldocument.DigitalDocumentAcceptUserResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentDownloadResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentJsonAttachOptionConverter;
import ag.act.converter.digitaldocument.DigitalDocumentStockResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentUserResponseConverter;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.DigitalDocumentStockResponse;
import ag.act.model.DigitalDocumentUserResponse;
import ag.act.model.UserDigitalDocumentResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostResponseDigitalDocumentConverter {
    private final DigitalDocumentStockResponseConverter digitalDocumentStockResponseConverter;
    private final DigitalDocumentUserResponseConverter digitalDocumentUserResponseConverter;
    private final DigitalDocumentAcceptUserResponseConverter digitalDocumentAcceptUserResponseConverter;
    private final DigitalDocumentDownloadResponseConverter digitalDocumentDownloadResponseConverter;
    private final DigitalDocumentJsonAttachOptionConverter digitalDocumentJsonAttachOptionConverter;

    public UserDigitalDocumentResponse convert(
        DigitalDocument digitalDocument,
        @Nullable DigitalDocumentAnswerStatus answerStatus,
        @Nullable ag.act.model.DigitalDocumentStockResponse stockResponse,
        @Nullable ag.act.model.DigitalDocumentUserResponse userResponse,
        @Nullable ag.act.model.DigitalDocumentAcceptUserResponse acceptUserResponse,
        List<ag.act.model.DigitalDocumentItemResponse> items
    ) {
        return convert(
            ConvertParameter.builder()
                .digitalDocument(digitalDocument)
                .answerStatus(answerStatus)
                .stockResponse(stockResponse)
                .userResponse(userResponse)
                .acceptUserResponse(acceptUserResponse)
                .items(items)
                .build()
        );
    }

    public UserDigitalDocumentResponse convert(
        ConvertParameter convertParameter
    ) {
        if (convertParameter.getDigitalDocument() == null) {
            return null;
        }

        DigitalDocument digitalDocument = convertParameter.getDigitalDocument();
        DigitalDocumentAnswerStatus answerStatus = convertParameter.getAnswerStatus();
        DigitalDocumentStockResponse stockResponse = convertParameter.getStockResponse();
        DigitalDocumentAcceptUserResponse acceptUserResponse = convertParameter.getAcceptUserResponse();
        DigitalDocumentUserResponse userResponse = convertParameter.getUserResponse();
        List<DigitalDocumentItemResponse> items = convertParameter.getItems();

        return new UserDigitalDocumentResponse()
            .id(digitalDocument.getId())
            .digitalDocumentType(digitalDocument.getType().name())
            .joinUserCount(digitalDocument.getJoinUserCount())
            .joinStockSum(digitalDocument.getJoinStockSum())
            .shareholdingRatio(getShareholdingRatio(digitalDocument))
            .targetStartDate(DateTimeConverter.convert(digitalDocument.getTargetStartDate()))
            .targetEndDate(DateTimeConverter.convert(digitalDocument.getTargetEndDate()))
            .attachOptions(digitalDocumentJsonAttachOptionConverter.apply(digitalDocument.getJsonAttachOption()))
            .answerStatus(getAnswerStatusName(answerStatus))
            .stock(stockResponse)
            .user(userResponse)
            .acceptUser(acceptUserResponse)
            .items(items)
            .digitalDocumentDownload(getDigitalDocumentDownload(digitalDocument));
    }

    public UserDigitalDocumentResponse convert(
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser,
        Stock stock,
        User user,
        @Nullable User acceptUser
    ) {
        if (digitalDocument == null) {
            return null;
        }

        return convert(
            digitalDocument,
            digitalDocumentUser.getDigitalDocumentAnswerStatus(),
            stock,
            user,
            acceptUser,
            digitalDocumentUser.getStockCount(),
            List.of()
        );
    }

    public UserDigitalDocumentResponse convert(
        DigitalDocument digitalDocument,
        DigitalDocumentAnswerStatus digitalDocumentAnswerStatus,
        Stock stock,
        User user,
        @Nullable User acceptUser,
        Long userStockCount,
        List<ag.act.model.DigitalDocumentItemResponse> digitalDocumentItemResponses
    ) {
        return convert(
            ConvertParameter.builder()
                .digitalDocument(digitalDocument)
                .answerStatus(digitalDocumentAnswerStatus)
                .stockResponse(digitalDocumentStockResponseConverter.convert(
                    stock,
                    digitalDocument,
                    userStockCount
                ))
                .userResponse(digitalDocumentUserResponseConverter.convert(user, digitalDocument.getType().isNeedAddress()))
                .acceptUserResponse(digitalDocumentAcceptUserResponseConverter.convert(acceptUser))
                .items(digitalDocumentItemResponses)
                .build()
        );
    }

    private ag.act.model.DigitalDocumentDownloadResponse getDigitalDocumentDownload(DigitalDocument digitalDocument) {
        return digitalDocument.getLatestDigitalDocumentDownload().map(digitalDocumentDownloadResponseConverter::convert).orElse(null);
    }

    private Float getShareholdingRatio(DigitalDocument digitalDocument) {
        return Optional.ofNullable(digitalDocument.getShareholdingRatio()).map(Double::floatValue).orElse(0f);
    }

    private String getAnswerStatusName(DigitalDocumentAnswerStatus answerStatus) {
        return Optional.ofNullable(answerStatus).map(DigitalDocumentAnswerStatus::name).orElse(null);
    }

    @Builder
    @Getter
    static class ConvertParameter {
        private DigitalDocument digitalDocument;
        private DigitalDocumentAnswerStatus answerStatus;
        private ag.act.model.DigitalDocumentStockResponse stockResponse;
        private ag.act.model.DigitalDocumentUserResponse userResponse;
        private ag.act.model.DigitalDocumentAcceptUserResponse acceptUserResponse;
        private List<ag.act.model.DigitalDocumentItemResponse> items;
    }
}
