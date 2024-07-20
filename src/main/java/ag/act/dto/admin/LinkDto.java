package ag.act.dto.admin;

import ag.act.enums.AppLinkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkDto {
    private String linkTitle;
    private Long postId;
    private AppLinkType linkType;
}
