package perczynski.kamil.evolution.gameservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import perczynski.kamil.evolution.gameservice.domain.GameErrorCode;
import perczynski.kamil.evolution.gameservice.domain.Player;
import perczynski.kamil.evolution.gameservice.domain.PlayerRepository;
import perczynski.kamil.evolution.gameservice.domain.bets.Bet;
import perczynski.kamil.evolution.gameservice.domain.bets.BetMode;
import perczynski.kamil.evolution.gameservice.domain.bets.GameRound;
import perczynski.kamil.evolution.gameservice.domain.rounds.GameRoundRepository;
import perczynski.kamil.evolution.gameservice.domain.rounds.TestRandomFacts;
import perczynski.kamil.evolution.gameservice.domain.rounds.Win;
import perczynski.kamil.evolution.gameservice.libs.Money;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static perczynski.kamil.evolution.gameservice.libs.Uuids.nextUuid;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class GameControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestRandomFacts testRandomFacts;

    @Autowired
    private GameRoundRepository gameRoundRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterPlayer() throws Exception {
        // given & when & then:
        mvc.perform(post("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").isString())
                .andExpect(jsonPath("$.balance.amount").value(5000_00))
                .andExpect(jsonPath("$.balance.formatted").value("€5,000.00"))
                .andExpect(jsonPath("$.freeRoundAvailable").value(false));
    }

    @Test
    void testReadPlayer() throws Exception {
        // given:
        final Player player = playerRepository.save(
                Player.builder()
                        .playerId(nextUuid())
                        .balance(new Money(1000_00))
                        .freeRoundAvailable(true)
                        .build()
        );

        // when & then:
        mvc.perform(get("/players/{playerId}", player.playerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(player.playerId()))
                .andExpect(jsonPath("$.balance.amount").value(1000_00))
                .andExpect(jsonPath("$.balance.formatted").value("€1,000.00"))
                .andExpect(jsonPath("$.freeRoundAvailable").value(true));
    }

    @Test
    void testPlaceBet() throws Exception {
        // given:
        testRandomFacts.fixFacts(
                TestRandomFacts.Config.builder()
                        .moneyWin(true)
                        .mediumWin(true)
                        .freeNextRound(false)
                        .build()
        );

        final Player player = playerRepository.save(
                Player.builder()
                        .playerId(nextUuid())
                        .balance(new Money(1000_00))
                        .freeRoundAvailable(true)
                        .build()
        );
        final PlaceBetRequest placeBetRequest = PlaceBetRequest.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(1000)
                .build();

        // when & then:
        mvc.perform(post("/players/{playerId}/bets", player.playerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeBetRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextBalance.formatted").value("€1,100.00"))
                .andExpect(jsonPath("$.bet.stake.formatted").value("€10.00"))
                .andExpect(jsonPath("$.win.payout.formatted").value("€100.00"))
                .andExpect(jsonPath("$.win.freeRound").value(false))
        ;

        final List<GameRound> allRounds = gameRoundRepository.findByPlayerId(player.playerId());
        assertThat(allRounds).hasSize(1);
    }

    @Test
    void testPlaceBetWithInsufficientFunds() throws Exception {
        // given:
        final Player player = playerRepository.save(
                Player.builder()
                        .playerId(nextUuid())
                        .balance(Money.ZERO)
                        .freeRoundAvailable(false)
                        .build()
        );

        final PlaceBetRequest placeBetRequest = PlaceBetRequest.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(1000)
                .build();
        // when & then:
        mvc.perform(post("/players/{playerId}/bets", player.playerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeBetRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(GameErrorCode.INSUFFICIENT_FUNDS.getCode()))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testReadGameRound() throws Exception {
        // given:
        final GameRound gameRound = gameRoundRepository.save(
                GameRound.builder()
                        .id(nextUuid())
                        .playerId(nextUuid())
                        .bet(
                                Bet.builder()
                                        .stake(new Money(10_00))
                                        .mode(BetMode.PLAY_FOR_CASH)
                                        .build()
                        )
                        .win(new Win(new Money(50_00), false))
                        .nextBalance(new Money(50_00))
                        .build()
        );

        // when:
        mvc.perform(get("/game-rounds/{roundId}", gameRound.id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameRound.id()))
                .andExpect(jsonPath("$.bet.stake.formatted").value("€10.00"))
                .andExpect(jsonPath("$.win.payout.formatted").value("€50.00"))
                .andExpect(jsonPath("$.nextBalance.formatted").value("€50.00"));

    }
}
