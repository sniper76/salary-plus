package ag.act.service.digitaldocument;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.InternalServerException;
import ag.act.facade.stock.StockFacade;
import ag.act.module.digitaldocumentgenerator.document.DigitalDocumentGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DigitalDocumentUserProcessService {
    private final UserHoldingStockService userHoldingStockService;
    private final DigitalDocumentGenerator digitalDocumentGenerator;
    private final DigitalDocumentUserRepository digitalDocumentUserRepository;
    private final StockFacade stockFacade;
    private final DigitalDocumentNumberService digitalDocumentNumberService;

    public DigitalDocumentUser createDigitalDocumentUserWithGeneratedPdf(
        DigitalDocument digitalDocument,
        User user,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        DigitalDocumentUser digitalDocumentUser = saveDigitalDocumentUser(
            digitalDocument, user, getPurchasePrice(digitalDocument, user)
        );

        final PdfDataDto pdfDataDto = generatePdf(
            digitalDocument, signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, digitalDocumentUser
        );

        updateDigitalDocumentUser(
            digitalDocumentUser,
            pdfDataDto
        );

        return digitalDocumentUser;
    }

    public DigitalDocumentUser createCompletedDigitalDocumentUser(DigitalDocument digitalDocument, User user) {
        final DigitalDocumentUser digitalDocumentUser = saveDigitalDocumentUser(digitalDocument, user, getPurchasePrice(digitalDocument, user));
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.COMPLETE);
        return digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    private long getPurchasePrice(DigitalDocument digitalDocument, User user) {
        if (digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY) {
            //의결권위잉은 과거 기준일이여서 매입금액을 알수가 없다
            return 0L;
        }
        return Optional.ofNullable(
            userHoldingStockService.getUserHoldingStock(user.getId(), digitalDocument.getStockCode()).getPurchasePrice()
        ).orElse(0L);
    }

    private PdfDataDto generatePdf(
        DigitalDocument digitalDocument,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf,
        DigitalDocumentUser digitalDocumentUser
    ) {
        try {
            return digitalDocumentGenerator.generate(
                getGenerateDigitalDocumentDto(signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf),
                digitalDocument,
                digitalDocumentUser
            );
        } catch (Exception e) {
            log.error("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다. {}", e.getMessage(), e);
            throw new InternalServerException("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private GenerateDigitalDocumentDto getGenerateDigitalDocumentDto(
        MultipartFile signatureImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImageList,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        return GenerateDigitalDocumentDto
            .builder()
            .signatureImage(signatureImage)
            .attachingFilesDto(
                getAttachingFilesDto(idCardImage, bankAccountImageList, hectoEncryptedBankAccountPdf)
                    .orElse(null)
            )
            .build();
    }

    private Optional<AttachingFilesDto> getAttachingFilesDto(
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImageList,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        if (idCardImage == null && bankAccountImageList == null && hectoEncryptedBankAccountPdf == null) {
            return Optional.empty();
        }

        return Optional.of(
            AttachingFilesDto.builder()
                .idCardImage(idCardImage)
                .bankAccountImages(bankAccountImageList)
                .hectoEncryptedBankAccountPdf(hectoEncryptedBankAccountPdf)
                .build()
        );
    }

    private void updateDigitalDocumentUser(DigitalDocumentUser digitalDocumentUser, PdfDataDto pdfDataDto) {
        digitalDocumentUser.setPdfPath(pdfDataDto.getPath());
        digitalDocumentUser.setOriginalPageCount(pdfDataDto.getOriginalPageCount());
        digitalDocumentUser.setAttachmentPageCount(pdfDataDto.getAttachmentPageCount());
        digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    private DigitalDocumentUser saveDigitalDocumentUser(
        DigitalDocument digitalDocument, User user, Long purchasePrice
    ) {
        Stock stock = stockFacade.findByCode(digitalDocument.getStockCode());

        DigitalDocumentUser digitalDocumentUser = new DigitalDocumentUser();
        digitalDocumentUser.setUserId(user.getId());
        digitalDocumentUser.setDigitalDocumentId(digitalDocument.getId());
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.SAVE);
        digitalDocumentUser.setStockCode(stock.getCode());
        digitalDocumentUser.setStockName(stock.getName());
        digitalDocumentUser.setName(user.getName());
        digitalDocumentUser.setHashedPhoneNumber(user.getHashedPhoneNumber());
        digitalDocumentUser.setBirthDate(user.getBirthDate());
        digitalDocumentUser.setGender(user.getGender());
        digitalDocumentUser.setFirstNumberOfIdentification(user.getFirstNumberOfIdentification());
        digitalDocumentUser.setAddress(user.getAddress());
        digitalDocumentUser.setAddressDetail(user.getAddressDetail());
        digitalDocumentUser.setZipcode(user.getZipcode());
        digitalDocumentUser.setDigitalDocument(digitalDocument);
        digitalDocumentUser.setPurchasePrice(purchasePrice);
        digitalDocumentUser.setStockCount(0L);
        digitalDocumentUser.setLoanPrice(0L);

        final long lastIssuedNumber = getLastIssuedNumber(digitalDocument);

        digitalDocumentUser.setIssuedNumber(lastIssuedNumber);

        if (digitalDocument.getType() != DigitalDocumentType.DIGITAL_PROXY) {
            digitalDocumentUser.setStockReferenceDate(KoreanDateTimeUtil.getTodayLocalDate());
        } else {
            // 모두 레퍼런스 데이터를 user에 얺어준다.
            digitalDocumentUser.setStockReferenceDate(digitalDocument.getStockReferenceDate());
        }

        return digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    private long getLastIssuedNumber(DigitalDocument digitalDocument) {
        return digitalDocumentNumberService.increaseAndGetLastIssuedNumber(digitalDocument.getId());
    }
}
