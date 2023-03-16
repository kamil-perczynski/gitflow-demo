package perczynski.kamil.evolution.gameservice.libs;

import lombok.Getter;

@Getter
public class ErrorCodeException extends RuntimeException {

    private final ErrorCode code;

    public ErrorCodeException(ErrorCode code) {
        super("Encountered an error with the code: " + code);
        this.code = code;
    }

    public ErrorCodeException(ErrorCode code, Throwable cause) {
        super("Encountered an error with the code: " + code, cause);
        this.code = code;
    }

}
