package perczynski.kamil.evolution.gameservice.domain;

import lombok.AllArgsConstructor;
import perczynski.kamil.evolution.gameservice.libs.ErrorCode;

@AllArgsConstructor
public enum GameErrorCode implements ErrorCode {
    INSUFFICIENT_FUNDS("You do not have enough funds on your account to place the bet"),
    INVALID_BET_AMOUNT("Bet amount must be in [€1, €10] range"),
    RESOURCE_DOES_NOT_EXIST("Missing resource: %s with id: %s");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
