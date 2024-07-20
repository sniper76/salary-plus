package ag.act.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDummyStockDto {
    private String code;
    private String name;
    private Long quantity;
    private LocalDate referenceDate;
    private LocalDateTime registerDate;
}
