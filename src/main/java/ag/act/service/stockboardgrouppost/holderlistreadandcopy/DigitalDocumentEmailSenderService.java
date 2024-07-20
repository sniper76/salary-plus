package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.exception.BadRequestException;
import ag.act.model.SendEmailRequest;
import ag.act.module.email.EmailSender;
import ag.act.module.email.RawEmailSender;
import ag.act.module.email.builder.InputStreamDataSource;
import ag.act.module.email.builder.RawEmailRequest;
import ag.act.service.aws.S3Service;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.service.digitaldocument.HolderListReadAndCopyListWrapper;
import ag.act.service.digitaldocument.HolderListReadAndCopyService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static ag.act.module.email.template.EmailTemplateNames.HOLDER_LIST_READ_AND_COPY_EMAIL_TEMPLATE_NAME;

@Service
@RequiredArgsConstructor
@Transactional
public class DigitalDocumentEmailSenderService {
    private final S3Service s3Service;
    private final RawEmailSender rawEmailSender;
    private final HolderListReadAndCopyService holderListReadAndCopyService;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final DecryptColumnConverter decryptColumnConverter;
    private final SolidarityLeaderService solidarityLeaderService;
    private final DigitalDocumentEmailSenderNotifier digitalDocumentEmailSenderNotifier;

    @Value("${aws.ses.holder-list-read-and-copy.sender-email}")
    private String senderEmail;

    public void sendEmailForDigitalDocument(Long digitalDocumentId, SendEmailRequest sendEmailRequest) {
        final User user = ActUserProvider.getNoneNull();
        validateIsSolidarityLeader(user);
        final HolderListReadAndCopyListWrapper holderListReadAndCopyListWrapper =
            validateDigitalDocumentAndGetHolderListReadAndCopies(digitalDocumentId);

        final RawEmailRequest rawEmailRequest = new RawEmailRequest(
            EmailSender.SENDER_NAME,
            senderEmail,
            List.of(user.getEmail()),
            List.of(sendEmailRequest.getRecipientEmail()),
            List.of(user.getEmail()),
            getSubject(holderListReadAndCopyListWrapper),
            null,
            HOLDER_LIST_READ_AND_COPY_EMAIL_TEMPLATE_NAME,
            getTemplateData(holderListReadAndCopyListWrapper, user),
            List.of(getInputStreamDataSource(digitalDocumentId, user))
        );

        try {
            rawEmailSender.sendEmail(rawEmailRequest);
        } catch (Exception e) {
            digitalDocumentEmailSenderNotifier.notify(digitalDocumentId);
            throw e;
        }
    }

    private void validateIsSolidarityLeader(User user) {
        if (!solidarityLeaderService.isLeader(user.getId())) {
            throw new BadRequestException("주주대표만 가능한 기능입니다.");
        }
    }

    private HolderListReadAndCopyListWrapper validateDigitalDocumentAndGetHolderListReadAndCopies(Long digitalDocumentId) {
        if (digitalDocumentService.findDigitalDocument(digitalDocumentId).isEmpty()) {
            throw new BadRequestException("주주명부 열람/등사 정보가 존재하지 않습니다.");
        }
        return holderListReadAndCopyService.getHolderListReadAndCopyListWrapper(digitalDocumentId);
    }

    @NotNull
    private Map<String, Object> getTemplateData(HolderListReadAndCopyListWrapper holderListReadAndCopyListWrapper, User user) {
        return Map.of(
            "leaderName", holderListReadAndCopyListWrapper.getLeaderName(),
            "leaderPhone", decryptColumnConverter.convert(user.getHashedPhoneNumber()),
            "leaderEmail", holderListReadAndCopyListWrapper.getLeaderEmail()
        );
    }

    private String getSubject(HolderListReadAndCopyListWrapper holderListReadAndCopyListWrapper) {
        return "%s %s 주주명부 열람/등사 청구".formatted(
            holderListReadAndCopyListWrapper.getCompanyName(),
            holderListReadAndCopyListWrapper.getReferenceDateByLeader()
        );
    }

    @NotNull
    private InputStreamDataSource getInputStreamDataSource(Long digitalDocumentId, User user) {
        final DigitalDocumentUser digitalDocumentUser = digitalDocumentUserService
            .getDigitalDocumentUserNoneNull(digitalDocumentId, user.getId());
        final String pdfPath = digitalDocumentUser.getPdfPath();
        final String filename = FilenameUtils.getName(pdfPath);
        final InputStream inputStream = s3Service.readObject(pdfPath);
        return new InputStreamDataSource(filename, inputStream);
    }
}
