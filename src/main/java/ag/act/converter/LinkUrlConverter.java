package ag.act.converter;

import ag.act.entity.LinkUrl;
import org.springframework.stereotype.Component;

@Component
public class LinkUrlConverter {
    public ag.act.model.HomeLinkResponse convert(LinkUrl linkUrl) {
        return new ag.act.model.HomeLinkResponse()
            .id(linkUrl.getId())
            .linkTitle(linkUrl.getLinkTitle())
            .linkType(linkUrl.getLinkType().name())
            .linkUrl(linkUrl.getLinkUrl())
            .status(linkUrl.getStatus())
            .createdAt(DateTimeConverter.convert(linkUrl.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(linkUrl.getUpdatedAt()));
    }
}
