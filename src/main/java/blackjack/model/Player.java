package blackjack.model;

import blackjack.model.enums.PlayerStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("player")
public class Player {

    @Id
    private Integer id;

    @Column("player_name")
    private String name;

    @Column("total_points")
    private int totalPoints;

    @Transient
    @Builder.Default
    private PlayerStatus status = null;
}
