# BattleShip Game

This repository contains the source code for a Java-based BattleShip game. The game is played on a 10x10 grid where players try to sink ships by guessing their locations.

## Features

- GUI implementation using Swing.
- Real-time feedback for hits and misses.
- Game status updates with number of guesses and ships remaining.
- Time tracking for game duration.
- Color-coded feedback for different ships and their statuses.

## Files

- **BattleShipPart.java**: Main game file that includes the JFrame implementation, game logic, and event handlers.
- **MyJButton.java**: Custom JButton class to manage grid cells.
- **Ship.java**: Ship class that manages ship data, hits, and sunk status.

## How to Play

1. Clone the repository to your local machine.
2. Ensure you have Java installed.
3. Compile the Java files using your preferred Java compiler.
4. Run the `BattleShipPart.java` file to start the game.
5. Click on the grid cells to make guesses and try to sink all ships.

## Game Rules

- The grid has a total of 6 ships with varying sizes.
- You are given 50 guesses to try and sink all the ships.
- A hit on a ship will be marked, and the ship's cell will change color based on its type.
- Once a ship is completely hit, it is marked as sunk.
- The game ends when all ships are sunk or guesses run out.
