# Mindbug Backend

## Quick start :

Import this project as Maven project in your preferred IDE. 

## Compile : 

```mvn clean install```

## Test only :

```mvn test```

## Available executable applications : 

### org.metacorp.mindbug.app.ManualApp

This application allows you to play a game through a terminal.
Follow the displayed instructions and enjoy.

### org.metacorp.mindbug.app.AutoApp

This application executes a game that will be automatically played by two virtual players.
This is useful to test a random game execution

### org.metacorp.mindbug.app.RestApp

This is the main application that exposes a REST API that allows to play the game and also sends WebSocket messages to update game state.
