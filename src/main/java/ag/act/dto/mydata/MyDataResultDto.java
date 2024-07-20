package ag.act.dto.mydata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("checkstyle:MemberName")
public class MyDataResultDto {
    private String code;
    private String message;
    private String extraMessage;
    private String txid;
    private String timestamp;
}
