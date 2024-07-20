package ag.act.configuration.initial;

import ag.act.entity.LinkUrl;
import ag.act.enums.LinkType;
import ag.act.model.Status;
import ag.act.repository.LinkUrlRepository;
import ag.act.validator.UrlValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Transactional
public class LinkUrlsLoader implements InitialLoader {
    private final LinkUrlRepository linkUrlRepository;
    private final UrlValidator urlValidator;

    public LinkUrlsLoader(
        LinkUrlRepository linkUrlRepository,
        UrlValidator urlValidator
    ) {
        this.linkUrlRepository = linkUrlRepository;
        this.urlValidator = urlValidator;
    }

    @Override
    public void load() {
        validateUrls();
        loadLinks();
    }

    private void validateUrls() {
        Arrays.stream(LinkType.values())
            .forEach((type) -> urlValidator.validate(type.getInitialUrl()));
    }

    private void loadLinks() {
        Arrays.stream(LinkType.values())
            .forEach(this::saveIfActiveLinkUrlDoesNotExist);
    }

    private void saveIfActiveLinkUrlDoesNotExist(LinkType type) {
        if (existActiveLinkUrlByLinkType(type)) {
            return;
        }

        LinkUrl linkUrl = new LinkUrl();
        linkUrl.setLinkType(type);
        linkUrl.setLinkTitle(type.getLinkTitle());
        linkUrl.setLinkUrl(type.getInitialUrl());
        linkUrl.setStatus(Status.ACTIVE);

        linkUrlRepository.save(linkUrl);
    }

    private boolean existActiveLinkUrlByLinkType(LinkType type) {
        return linkUrlRepository.findByLinkTypeAndStatus(type, ag.act.model.Status.ACTIVE).isPresent();
    }
}
