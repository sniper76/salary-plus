package ag.act.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserDto {
    private Long id;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    private ag.act.model.Gender gender;
}
