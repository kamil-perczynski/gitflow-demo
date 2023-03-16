package perczynski.kamil.evolution.gameservice.domain.rounds;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class RandomFactsTest {

    private static RandomFacts randomFacts = new RandomFacts();

    @ParameterizedTest(name = "Test {2} probability: {1}")
    @MethodSource("randomFacts")
    void name(BooleanSupplier randomFact, double expectedProbability, String factName) {
        // given:
        final int totalAttempts = 1000;
        final int acceptedDrift = 40;

        // when:
        final int totalOccurrences = IntStream.range(0, totalAttempts)
                .map((i) -> randomFact.getAsBoolean() ? 1 : 0)
                .sum();

        // then:
        final int expectedOccurrences = (int) (expectedProbability * totalAttempts);
        assertThat(totalOccurrences)
                .describedAs("A %s should have %s probability", factName, expectedProbability)
                .isCloseTo(expectedOccurrences, Offset.offset(acceptedDrift));
    }

    public static List<Arguments> randomFacts() {
        return List.of(
                Arguments.of((BooleanSupplier) randomFacts::drawMoneyWin, .3, "MoneyWin"),
                Arguments.of((BooleanSupplier) randomFacts::drawFreeNextRound, .1, "FreeNextRound"),
                Arguments.of((BooleanSupplier) randomFacts::drawBigWin, .05, "BigWin"),
                Arguments.of((BooleanSupplier) randomFacts::drawMediumWin, .1, "MediumWin")
        );
    }
}