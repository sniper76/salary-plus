package ag.act.validator.document;

import ag.act.dto.IDigitalDocumentFillRequest;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.JsonAttachOption;
import ag.act.util.DateTimeUtil;
import ag.act.validator.ImageMediaTypeValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class DigitalDocumentValidator {
    private final ImageMediaTypeValidator imageMediaTypeValidator;
    private final OtherDocumentContentValidator otherDocumentContentValidator;

    public DigitalDocumentValidator(
        ImageMediaTypeValidator imageMediaTypeValidator,
        OtherDocumentContentValidator otherDocumentContentValidator
    ) {
        this.imageMediaTypeValidator = imageMediaTypeValidator;
        this.otherDocumentContentValidator = otherDocumentContentValidator;
    }

    public void validateCommon(CreateDigitalDocumentRequest createRequest) {
        if (DigitalDocumentType.DIGITAL_PROXY.name().equals(createRequest.getType())
            && createRequest.getStockReferenceDateId() == null) {
            throw new BadRequestException("기준일 정보 아이디를 확인하세요.");
        }
        if (createRequest.getTargetEndDate() == null || createRequest.getTargetStartDate() == null) {
            throw new BadRequestException("시작일 종료일을 확인하세요.");
        }
        validateTargetStartDateIsBeforeTargetEndDate(createRequest.getTargetStartDate(), createRequest.getTargetEndDate());
        validateTargetEndDateIsPast(createRequest.getTargetEndDate());
        validateAttachOptions(createRequest.getAttachOptions());
    }

    private void validateAttachOptions(JsonAttachOption attachOptions) {
        if (attachOptions == null) {
            throw new BadRequestException("전자문서 옵셔널 정보를 확인하세요.");
        }

        validateAttachOption(attachOptions.getSignImage(), "signImage");
        validateAttachOption(attachOptions.getIdCardImage(), "idCardImage");
        validateAttachOption(attachOptions.getBankAccountImage(), "bankAccountImage");
        validateAttachOption(attachOptions.getHectoEncryptedBankAccountPdf(), "hectoEncryptedBankAccountPdf");
    }

    private void validateAttachOption(String attachOption, String attachOptionName) {
        if (attachOption == null) {
            throw new BadRequestException("전자문서 옵셔널 정보 %s 를 입력하세요.".formatted(attachOptionName));
        }
        validateAttachOptionType(attachOption);
    }

    private void validateAttachOptionType(String attachOption) {
        try {
            AttachOptionType.fromValue(attachOption);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void validateTargetEndDateIsPast(Instant targetEndDate) {
        if (DateTimeUtil.isPast(targetEndDate)) {
            throw new BadRequestException("종료일은 현재 시간 이후로 설정해주세요.");
        }
    }

    public void validateTargetStartDateIsBeforeToday(LocalDateTime targetStartDate) {
        if (DateTimeUtil.isBeforeToday(targetStartDate)) {
            throw new BadRequestException("기준일은 시작일 이전에 변경 가능합니다.");
        }
    }

    public void validateTargetStartDateIsBeforeTargetEndDate(Instant targetStartDate, Instant targetEndDate) {
        if (targetEndDate.isBefore(targetStartDate)) {
            throw new BadRequestException("종료일을 시작일 이전으로 등록 불가능합니다.");
        }
    }

    public void validateJointOwnershipDocument(IDigitalDocumentFillRequest request) {
        if (StringUtils.isBlank(request.getContent())) {
            throw new BadRequestException("공동보유확인서 내용을 확인하세요.");
        }
        if (StringUtils.isBlank(request.getCompanyRegistrationNumber())) {
            throw new BadRequestException("법인등록번호를 확인하세요.");
        }
    }

    public void validateOtherDocument(IDigitalDocumentFillRequest request) {
        if (StringUtils.isBlank(request.getTitle())) {
            throw new BadRequestException("문서 제목을 확인하세요.");
        }
        if (StringUtils.isBlank(request.getContent())) {
            throw new BadRequestException("문서 내용을 확인하세요.");
        }
        otherDocumentContentValidator.validate(request.getContent());
    }

    public void validateAnswerItemCount(int itemCount, int answerCount) {
        if (answerCount != itemCount) {
            throw new BadRequestException("선택된 찬성/반대/기권 정보 갯수가 일치하지 않습니다.");
        }
    }

    public void validateRequestAnswerItemCount(int itemCount, int answerCount) {
        if (answerCount != itemCount) {
            throw new BadRequestException("선택된 찬성/반대/기권 정보 갯수와 전자문서 의안 갯수가 일치하지 않습니다.");
        }
    }

    public void validateDigitalDocumentUserFiles(
        DigitalDocument digitalDocument,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        validateDigitalDocumentUserFiles(
            digitalDocument,
            signImage,
            idCardImage,
            bankAccountImages,
            hectoEncryptedBankAccountPdf,
            null
        );
    }

    public void validateDigitalDocumentUserFiles(
        DigitalDocument digitalDocument,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf,
        String answerData
    ) {
        digitalDocument.getType().validate(answerData);
        // TODO 이걸 사실 디비에 저장해야 한다. 여기서 처리하면 안된다. DigitalDocument의 JsonAttachOption 에 정보를 저장해야 한다.
        digitalDocument.getType().validate(bankAccountImages, hectoEncryptedBankAccountPdf);
        JsonAttachOption jsonAttachOption = digitalDocument.getJsonAttachOption();
        validateUserAttachOption(signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, jsonAttachOption);

        List<MultipartFile> images = new ArrayList<>();
        if (signImage != null) {
            images.add(signImage);
        }
        if (idCardImage != null) {
            images.add(idCardImage);
        }
        if (!CollectionUtils.isEmpty(bankAccountImages)) {
            images.addAll(bankAccountImages);
        }
        validateImageMediaTypes(images);
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private void validateUserAttachOption(
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf,
        JsonAttachOption jsonAttachOption
    ) {
        if (jsonAttachOption == null) {
            return;
        }

        AttachOptionType.fromValue(jsonAttachOption.getSignImage())
            .validate(Arrays.asList(signImage), "서명 이미지");

        AttachOptionType.fromValue(jsonAttachOption.getIdCardImage())
            .validate(Arrays.asList(idCardImage), "신분증 이미지");

        AttachOptionType.fromValue(jsonAttachOption.getBankAccountImage())
            .validate(bankAccountImages, "잔고증명서");

        AttachOptionType.fromValue(jsonAttachOption.getHectoEncryptedBankAccountPdf())
            .validate(Arrays.asList(hectoEncryptedBankAccountPdf), "잔고증명서");
    }

    private void validateImageMediaTypes(List<MultipartFile> images) {
        for (MultipartFile file : images) {
            imageMediaTypeValidator.validate(file);
        }
    }

    public void validateTargetEndDate(LocalDateTime targetEndDate, String text) {
        if (DateTimeUtil.isNowAfter(targetEndDate)) {
            throw new BadRequestException("이미 위임기간이 종료되어 %s하실 수 없습니다.".formatted(text));
        }
    }

    public void validateAcceptor(DigitalDocumentType digitalDocumentType, User acceptorUser, Long acceptUserId) {
        if (digitalDocumentType == DigitalDocumentType.ETC_DOCUMENT) {
            return;
        }

        if (acceptorUser == null) {
            throw new BadRequestException("전자문서의 %s 정보가 없습니다.".formatted(digitalDocumentType.getAcceptUserDisplayName()));
        }

        if (!Objects.equals(acceptorUser.getId(), acceptUserId)) {
            throw new BadRequestException("전자문서의 %s 정보가 일치하지 않습니다.".formatted(digitalDocumentType.getAcceptUserDisplayName()));
        }
    }

    public void validateDigitalProxy(IDigitalDocumentFillRequest request) {
        if (StringUtils.isBlank(request.getShareholderMeetingType())) {
            throw new BadRequestException("주총구분을 확인하세요.");
        }

        if (StringUtils.isBlank(request.getShareholderMeetingName())) {
            throw new BadRequestException("주총명을 확인하세요.");
        }

        if (request.getShareholderMeetingDate() == null) {
            throw new BadRequestException("주총일자를 확인하세요.");
        }

        if (StringUtils.isBlank(request.getDesignatedAgentNames())) {
            throw new BadRequestException("수임인지정대리인을 확인하세요.");
        }
    }
}
