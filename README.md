# Oh Hell Card Game Scoring App

An Android application for keeping score in the "Oh Hell" card game, built with Kotlin and Material Design.

## Features

- **Player Management**: Add and manage multiple players
- **Game Setup**: Start games with 2 or more players
- **Score Tracking**: Track bids and tricks won for each round
- **Automatic Scoring**: Calculates scores based on Oh Hell rules
- **Round Management**: Handles the full game cycle (cards dealt go up then down)
- **Game History**: View total scores and round progression

## Oh Hell Game Rules

Oh Hell is a trick-taking card game where players must bid exactly how many tricks they will take. The game consists of multiple rounds:

1. **Round Structure**: Cards dealt increase from 1 to a maximum (usually 10), then decrease back to 1
2. **Bidding**: Each player bids how many tricks they expect to take
3. **Scoring**: 
   - Made bid exactly: 10 points + number of tricks taken
   - Missed bid: 0 points
4. **Winner**: Player with the highest total score at the end

## Technical Features

- **Modern Android Development**: Built with Kotlin and AndroidX
- **Material Design**: Clean, modern UI following Material Design principles  
- **ViewBinding**: Type-safe view binding for UI interactions
- **RecyclerView**: Efficient list displays for players and scores
- **MVVM Architecture**: Clean separation of concerns
- **Responsive Design**: Works on various screen sizes

## Project Structure

```
app/src/main/java/com/example/ohhell/
├── MainActivity.kt              # Player setup and game initialization
├── GameActivity.kt             # Main game scoring interface
├── model/
│   ├── GameModels.kt           # Data classes for Player, Round, PlayerRoundScore
│   └── GameState.kt            # Game logic and state management
└── adapter/
    ├── PlayersAdapter.kt       # RecyclerView adapter for player list
    └── ScoresAdapter.kt        # RecyclerView adapter for score tracking
```

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24 (Android 7.0) or higher
- Kotlin 1.9.10+

### Building the Project

1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Using the App

1. **Add Players**: Enter player names on the main screen
2. **Start Game**: Tap "Start Game" with 2+ players
3. **Enter Bids**: Each player enters their bid for the round
4. **Enter Tricks**: After playing, enter actual tricks won
5. **Next Round**: Advance to the next round when all scores are entered
6. **Game Over**: View final scores when all rounds are complete

## Development

### Key Components

- **GameState**: Manages all game logic, scoring, and round progression
- **MainActivity**: Handles player setup and game initialization
- **GameActivity**: Main scoring interface with real-time updates
- **ScoresAdapter**: Handles bid/trick input with automatic score calculation

### Customization

The app can be easily customized:
- Modify `maxCards` in `GameState.startGame()` to change game length
- Update scoring rules in `PlayerRoundScore.calculateScore()`
- Customize UI colors in `res/values/colors.xml`
- Add new features like game history or statistics

## Architecture

The app follows clean architecture principles:
- **Model**: Data classes and game logic (`GameModels.kt`, `GameState.kt`)
- **View**: Activities and layouts (XML layouts, Activities)
- **Adapter**: RecyclerView adapters for data presentation

## Future Enhancements

- Game history and statistics
- Player profiles and avatars
- Different game variants
- Export/import game data
- Network multiplayer support
- Tablet-optimized layouts

## License

This project is created for educational and personal use.