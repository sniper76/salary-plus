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
public class MyDataAuthTokenResponseDto {
    private MyDataResultDto result;
    private MyDataAuthDto data;
}
