package ag.act.module.modusign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuSignRequest {
    private String templateId;
    private String templateName;
    private String templateRole;
    private String name;
    private String email;
    private String phoneNumber;
}
