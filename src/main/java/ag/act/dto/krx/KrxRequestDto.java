package ag.act.dto.krx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KrxRequestDto {
    private String basDd;
    private List<KrxRequestParamDto> krxRequestParamDtoList;
}
