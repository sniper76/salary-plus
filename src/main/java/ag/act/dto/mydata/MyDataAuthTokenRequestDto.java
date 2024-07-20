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
public class MyDataAuthTokenRequestDto {
    private String clientId;
    private String clientSecret;
    private String txId;
    private String userId;
    private String userEmail;
    private String ci;
    private String realName;
    private String phoneNum;
    private String gender;
    private String birthday;
    private String accessToken;
}
