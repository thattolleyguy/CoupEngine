# CoupEngine
Simulate different AIs for coup playing against each other

## Add Your AI
1. Extend PlayerHandler with you AIs logic.
2. Add your AI type to the `PlayerTypes` enum. This enum is used to build the players when the game is initialized

## Run
1. Change the game config in `Application.main` method to your desired simulation
2. Run `Application.main`
