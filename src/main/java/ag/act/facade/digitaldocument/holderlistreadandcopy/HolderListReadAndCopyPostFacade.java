package ag.act.facade.digitaldocument.holderlistreadandcopy;

import ag.act.converter.HolderListReadAndCopyDigitalDocumentResponseConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.converter.post.createpostrequestdto.CreatePostRequestDtoConverter;
import ag.act.dto.HolderListReadAndCopyDigitalDocumentResponseData;
import ag.act.dto.PostDetailsParamDto;
import ag.act.dto.digitaldocument.HolderListReadAndCopyDocumentDto;
import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.DigitalDocumentUserProcessService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyUploadService;
import ag.act.validator.document.DigitalDocumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolderListReadAndCopyPostFacade {
    private final PostUpdateResponseConverter postUpdateResponseConverter;
    private final HolderListReadAndCopyUploadService holderListReadAndCopyUploadService;
    private final CreatePostRequestDtoConverter createPostRequestDtoConverter;
    private final StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final SolidarityLeaderService solidarityLeaderService;
    private final DigitalDocumentUserProcessService digitalDocumentUserProcessService;
    private final FileContentFromPdfCreator fileContentFromPdfCreator;
    private final DigitalDocumentValidator digitalDocumentValidator;
    private final HolderListReadAndCopyDigitalDocumentResponseConverter holderListReadAndCopyDigitalDocumentResponseConverter;

    public PostDetailsDataResponse createPostWithHolderListReadAndCopyDocument(
        HolderListReadAndCopyDocumentDto dto
    ) {
        final PostDetailsParamDto postDetailsParamDto = createPost(dto);
        final Long postId = postDetailsParamDto.getPost().getId();
        final Long digitalDocumentId = postDetailsParamDto.getPost().getDigitalDocument().getId();

        final DigitalDocument digitalDocument = getDigitalDocument(digitalDocumentId);
        final DigitalDocumentUser digitalDocumentUser = createDigitalDocumentUser(digitalDocument, dto);

        final PdfDataDto pdfDataDto = generateAndUploadPdf(dto, digitalDocument, digitalDocumentUser);
        updateDigitalDocumentUserPdfPath(digitalDocumentUser, pdfDataDto.getPath());
        final FileContent firstFileContent = createFileContentFromPdf(pdfDataDto, digitalDocument, postId);

        postDetailsParamDto.setFileContents(List.of(firstFileContent));

        HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocumentResponse =
            holderListReadAndCopyDigitalDocumentResponseConverter.convert(
            new HolderListReadAndCopyDigitalDocumentResponseData(
                digitalDocumentId,
                digitalDocumentUser.getPdfPath(),
                digitalDocumentUser.getUserId()
            )
        );
        postDetailsParamDto.setHolderListReadAndCopyDigitalDocumentResponse(holderListReadAndCopyDigitalDocumentResponse);

        return new PostDetailsDataResponse()
            .data(postUpdateResponseConverter.convert(postDetailsParamDto));
    }

    private void updateDigitalDocumentUserPdfPath(DigitalDocumentUser digitalDocumentUser, String pdfPath) {
        digitalDocumentUser.setPdfPath(pdfPath);
        digitalDocumentUserService.save(digitalDocumentUser);
    }

    private PostDetailsParamDto createPost(HolderListReadAndCopyDocumentDto dto) {
        return stockBoardGroupPostCreateService.createBoardGroupPostAndGetPostDetailsParamDto(
            createPostRequestDtoConverter.convert(
                dto.getStockCode(),
                dto.getBoardGroupName(),
                dto.getCreatePostRequest()
            )
        );
    }

    private FileContent createFileContentFromPdf(PdfDataDto pdfDataDto, DigitalDocument digitalDocument, Long postId) {
        return fileContentFromPdfCreator.create(pdfDataDto, digitalDocument, postId);
    }

    private PdfDataDto generateAndUploadPdf(
        HolderListReadAndCopyDocumentDto dto, DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser
    ) {
        return holderListReadAndCopyUploadService.generatePdf(
            digitalDocument,
            digitalDocumentUser,
            dto.getSignImage(),
            dto.getIdCardImage(),
            dto.getBankAccountImages(),
            dto.getHectoEncryptedBankAccountPdf()
        );
    }

    private DigitalDocument getDigitalDocument(Long digitalDocumentId) {
        return digitalDocumentService.getDigitalDocument(digitalDocumentId);
    }

    private DigitalDocumentUser createDigitalDocumentUser(DigitalDocument digitalDocument, HolderListReadAndCopyDocumentDto dto) {
        final User leaderUser = solidarityLeaderService.getLeaderUser(dto.getStockCode());

        validateAttachments(digitalDocument, dto);

        return digitalDocumentUserProcessService.createCompletedDigitalDocumentUser(digitalDocument, leaderUser);
    }

    private void validateAttachments(DigitalDocument digitalDocument, HolderListReadAndCopyDocumentDto dto) {
        digitalDocumentValidator.validateDigitalDocumentUserFiles(
            digitalDocument,
            dto.getSignImage(),
            dto.getIdCardImage(),
            dto.getBankAccountImages(),
            dto.getHectoEncryptedBankAccountPdf()
        );
    }
}
