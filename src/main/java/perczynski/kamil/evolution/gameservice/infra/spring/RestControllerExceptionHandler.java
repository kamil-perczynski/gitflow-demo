package perczynski.kamil.evolution.gameservice.infra.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import perczynski.kamil.evolution.gameservice.domain.GameErrorCode;
import perczynski.kamil.evolution.gameservice.libs.ErrorCodeException;
import perczynski.kamil.evolution.gameservice.libs.ResourceDoesNotExistException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(ErrorCodeException.class)
    public ResponseEntity<Object> handleException(ErrorCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        Map.of(
                                "code", ex.getCode(),
                                "message", ex.getCode().getMessage()
                        )
                );
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    public ResponseEntity<Object> handleException(ResourceDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "code", GameErrorCode.RESOURCE_DOES_NOT_EXIST,
                                "resource", ex.getResource(),
                                "identifier", ex.getIdentifier(),
                                "message", String.format(GameErrorCode.RESOURCE_DOES_NOT_EXIST.getMessage(), ex.getResource(), ex.getIdentifier())
                        )
                );
    }

}
