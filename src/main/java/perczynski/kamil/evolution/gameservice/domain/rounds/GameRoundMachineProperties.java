package perczynski.kamil.evolution.gameservice.domain.rounds;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@ConstructorBinding
@ConfigurationProperties("game-round")
public record GameRoundMachineProperties(
        @NotNull
        Integer smallWinMultiplier,
        @NotNull
        Integer mediumWinMultiplier,
        @NotNull
        Integer bigWinMultiplier
) {
}
