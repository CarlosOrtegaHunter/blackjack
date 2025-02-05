package blackjack.model.dto;

import blackjack.model.enums.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDTO {
    private int id;
    private String name;
    private int totalPoints;
    private PlayerStatus status;
}
