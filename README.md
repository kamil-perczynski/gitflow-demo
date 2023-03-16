# Game Service

## Starting the application

To start and stop the application you can use standard spring-boot maven goals: `mvn spring-boot:start`
and `mvn spring-boot:stop`

You can also run the project's main class `GameServiceApplication` directly from your IDE.

## Project structure

Project structure follows the Hexagonal Architecture rules.

```
package perczynski.kamil.evolution.gameservice
│
├── api (controllers, requests)
├── domain (domain logic, port definitions)
├── infra (classes needed to for spring integration, ports' implementations)
└── libs (shared classes, possibly between multiple projects, eligible for distrubution as the separate artifact)
```

## Limitations & design decisions

For handling the monetary operations there has been introduced a `Money` class which performs the math on the minor
units (eurocents in the case for euro).

The class also assumes the only currency in the application is Euro.

For readability when manually playing around with the API the `Money` class redundant field `formatted` which shows the
monetary amount in the human-readable format e.g. `€10.00`

## API endpoints

| HTTP method | Path                               | Description                                        |
|-------------|------------------------------------|----------------------------------------------------|
| POST        | `/players`                         | Register a player with the default initial balance |
| GET         | `/players/{playerId}`              | Read a player                                      |
| POST        | `/players/{playerId}/bets`         | Place a bet                                        |
| GET         | `/game-rounds?playerId={playerId}` | List all game rounds for the player                |
| GET         | `/game-rounds/{roundId}`           | Read a game round                                  |

## Usage manual

Please find full user journey documented in the form of the acceptance test in class `GameAcceptanceTests`.

Firstly a player needs to be registered with a call to `POST /players`

```json5
// POST /players
{
  "playerId": "21393719-4f01-40cb-b2d7-8c287900213e",
  "balance": {
    "amount": 500000,
    "formatted": "€5,000.00"
  },
  "freeRoundAvailable": false
}
```

```json5
// 200 OK
{
  "playerId": "2d04073d-cb7f-4c63-a57a-40e711948a36",
  "balance": {
    "amount": 500000,
    "formatted": "€5,000.00"
  },
  "freeRoundAvailable": false
}
```

Then the player is eligible for placing bets

```json5
// POST /players/21393719-4f01-40cb-b2d7-8c287900213e/bets
{
  "stake": 1000,
  "mode": "PLAY_FOR_CASH"
}
```

```json5
// 200 OK
{
  "id": "9292409a-a926-43a9-acf7-2296e75a55a3",
  "bet": {
    "mode": "PLAY_FOR_CASH",
    "stake": {
      "amount": 1000,
      "formatted": "€10.00"
    }
  },
  "win": {
    "payout": {
      "amount": 3000,
      "formatted": "€30.00"
    },
    "freeRound": false
  },
  "nextBalance": {
    "amount": 502000,
    "formatted": "€5,020.00"
  },
  "playerId": "21393719-4f01-40cb-b2d7-8c287900213e"
}
```

Then the player is eligible to read the game history at `GET /game-rounds?playerId=2d04073d-cb7f-4c63-a57a-40e711948a36`

```json5
{
  "rounds": [
    {
      "id": "164e9cf9-f369-4170-9987-638c93fde5fa",
      "bet": {
        "mode": "PLAY_FOR_CASH",
        "stake": {
          "amount": 1000,
          "formatted": "€10.00"
        }
      },
      "win": {
        "payout": {
          "amount": 3000,
          "formatted": "€30.00"
        },
        "freeRound": false
      },
      "nextBalance": {
        "amount": 502000,
        "formatted": "€5,020.00"
      },
      "playerId": "2d04073d-cb7f-4c63-a57a-40e711948a36"
    }
  ]
}
```