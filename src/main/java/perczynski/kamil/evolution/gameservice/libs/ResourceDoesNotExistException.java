package perczynski.kamil.evolution.gameservice.libs;

import lombok.Getter;

@Getter
public class ResourceDoesNotExistException extends RuntimeException {

    private final String resource;
    private final String identifier;

    public ResourceDoesNotExistException(String resource, String identifier) {
        this.resource = resource;
        this.identifier = identifier;
    }

}
