package ag.act.dto.campaign;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SimpleCampaignPostDto {
    private Long postId;
    private String code;
    private String name;
}
