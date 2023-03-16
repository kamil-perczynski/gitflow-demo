package perczynski.kamil.evolution.gameservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import perczynski.kamil.evolution.gameservice.domain.bets.BetMode;
import perczynski.kamil.evolution.gameservice.domain.rounds.TestRandomFacts;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameAcceptanceTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestRandomFacts testRandomFacts;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBetPlacingUserJourney() throws Exception {
        // given:
        testRandomFacts.fixFacts(
                TestRandomFacts.Config.builder()
                        .moneyWin(true)
                        .mediumWin(true)
                        .freeNextRound(false)
                        .build()
        );

        // when player is registered:
        final MvcResult playerRegistrationResult = mvc.perform(post("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").isString())
                .andExpect(jsonPath("$.balance.amount").value(5000_00))
                .andExpect(jsonPath("$.balance.formatted").value("€5,000.00"))
                .andExpect(jsonPath("$.freeRoundAvailable").value(false))
                .andReturn();

        final String playerId = objectMapper
                .readValue(playerRegistrationResult.getResponse().getContentAsByteArray(), JsonNode.class)
                .get("playerId")
                .asText();

        // and player can read his balance:
        mvc.perform(get("/players/{playerId}", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance.amount").value(5000_00));


        // and player places a bet for 10 eur:
        final PlaceBetRequest placeBetRequest = PlaceBetRequest.builder()
                .mode(BetMode.PLAY_FOR_CASH)
                .stake(1000)
                .build();
        mvc.perform(post("/players/{playerId}/bets", playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeBetRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextBalance.formatted").value("€5,090.00"))
                .andExpect(jsonPath("$.bet.stake.formatted").value("€10.00"))
                .andExpect(jsonPath("$.win.payout.formatted").value("€100.00"))
                .andExpect(jsonPath("$.win.freeRound").value(false));

        // and player checks the game history:
        mvc.perform(get("/game-rounds", playerId)
                        .queryParam("playerId", playerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds[0].nextBalance.formatted").value("€5,090.00"))
                .andExpect(jsonPath("$.rounds[0].bet.stake.formatted").value("€10.00"))
                .andExpect(jsonPath("$.rounds[0].win.payout.formatted").value("€100.00"))
                .andExpect(jsonPath("$.rounds", hasSize(1)));
    }
}
