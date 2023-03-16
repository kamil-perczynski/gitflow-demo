package perczynski.kamil.evolution.gameservice.libs;

import org.mockito.stubbing.Answer;

public class MockAnswers {

    public static Answer<Object> withFirstParameter() {
        return a -> a.getArgument(0);
    }

}
