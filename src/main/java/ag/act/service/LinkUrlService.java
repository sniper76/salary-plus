package ag.act.service;

import ag.act.converter.LinkUrlConverter;
import ag.act.entity.LinkUrl;
import ag.act.enums.LinkType;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import ag.act.repository.LinkUrlRepository;
import ag.act.validator.UrlValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LinkUrlService {
    private final LinkUrlRepository linkUrlRepository;
    private final LinkUrlConverter linkUrlConverter;
    private final UrlValidator urlValidator;

    public LinkUrlService(
        LinkUrlRepository linkUrlRepository,
        LinkUrlConverter linkUrlConverter,
        UrlValidator urlValidator
    ) {
        this.linkUrlRepository = linkUrlRepository;
        this.linkUrlConverter = linkUrlConverter;
        this.urlValidator = urlValidator;
    }

    public ag.act.model.HomeLinkResponse findByLinkType(LinkType linkType) {
        return linkUrlConverter.convert(getActiveLinkUrl(linkType));
    }

    public ag.act.model.HomeLinkResponse updateByLinkType(LinkType linkType, String linkUrl) {
        urlValidator.validate(linkUrl);

        LinkUrl linkUrlToUpdate = getActiveLinkUrl(linkType);
        linkUrlToUpdate.setLinkUrl(linkUrl.trim());

        return linkUrlConverter.convert(linkUrlToUpdate);
    }

    private LinkUrl getActiveLinkUrl(LinkType linkType) {
        return linkUrlRepository
            .findByLinkTypeAndStatus(linkType, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundException("해당 링크 타입이 존재하지 않습니다."));
    }
}
