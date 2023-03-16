package perczynski.kamil.evolution.gameservice.libs;

import java.util.UUID;

public class Uuids {

    public static String nextUuid() {
        return UUID.randomUUID().toString();
    }

}
