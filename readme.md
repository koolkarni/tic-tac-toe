# Tic-Tac-Toe Game Simulation API

## About the Project

This project is a Java Spring Boot application that simulates concurrent Tic-Tac-Toe games between multiple players. It supports creating games, joining games, making valid moves, tracking wins and efficiency, and simulating bulk games using Java threads.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL (Docker)
- Maven

## Installation

### Prerequisites
- Java 17 JDK
- Maven
- Docker (for PostgreSQL)

### How to Start PostgreSQL with Docker
#### this step is optional you can directly run the project from intelliJ 
#### as the ide will create the database for you.
```bash
docker run --name game_postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=game_db \
  -p 5432:5432 \
  -d postgres
```


### How to Run the Spring Boot App

1. Clone the repository:
```bash
git clone git@github.com:koolkarni/tic-tac-toe.git
cd tic-tac-toe
```

2. Configure database connection in `application.yml` or `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/game_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### 1. Create Game
```
POST /api/games/create?playerId={playerId}
```
Creates a new game with a single player.

### 2. Join Game
```
POST /api/games/{gameId}/join?playerId={playerId}
```
Second player joins the specified game.

### 3. Make Move
```
POST /api/games/{gameId}/move
```
Request Body:
```json
{
  "playerId": 1,
  "row": 0,
  "col": 1
}
```

### 4. View Game
```
GET /api/games/{gameId}
```
Returns game state and board.

### 5. Leaderboard
```
GET /api/games/leaderboard
```
Returns top players by wins and efficiency.

### 6. Simulate Games (Concurrent)
```
POST /api/simulate/games
```
Request Body:
```json
{
  "totalUsers": 10,
  "totalGames": 5
}
```
Simulates multiple games using threads and returns results.

## Features

- All game state is persisted in PostgreSQL
- `makeMove()` is synchronized per game to ensure consistency
- Simulations are handled with native Java threads for concurrency
