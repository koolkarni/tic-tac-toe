package com.example.gameengine.demo.controller;

import com.example.gameengine.demo.model.Game;
import com.example.gameengine.demo.model.GameSimulationResult;
import com.example.gameengine.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class SimulationController {

    private final GameService gameService;
    private final Random random = new Random();

    @PostMapping("/games")
    public List<GameSimulationResult> simulateGames(@RequestBody Map<String, Integer> body) throws InterruptedException {
        int totalUsers = body.getOrDefault("totalUsers", 10);
        int totalGames = body.getOrDefault("totalGames", 5);

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(totalGames, 10));
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= totalUsers; i++) {
            userIds.add(i);
        }

        List<Future<GameSimulationResult>> futures = new ArrayList<>();

        for (int i = 0; i < totalGames; i++) {
            futures.add(executor.submit(() -> simulateGame(userIds)));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        List<GameSimulationResult> results = new ArrayList<>();
        for (Future<GameSimulationResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                log.error("Error during simulation: {}", e.getMessage(), e);
            }
        }

        return results;
    }

    private GameSimulationResult simulateGame(List<Long> userIds) {
        Collections.shuffle(userIds);
        Long p1 = userIds.get(0);
        Long p2 = userIds.get(1);

        Game game = gameService.createGame(p1);
        Long gameId = game.getId();
        gameService.joinGame(gameId, p2);

        int[][] grid = new int[3][3];
        Long current = p1;

        List<GameSimulationResult.MoveRecord> moves = new ArrayList<>();

        for (int move = 0; move < 9; move++) {
            List<int[]> empty = new ArrayList<>();
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (grid[r][c] == 0) empty.add(new int[]{r, c});

            if (empty.isEmpty()) break;

            int[] cell = empty.get(random.nextInt(empty.size()));
            int row = cell[0], col = cell[1];

            try {
                Game updated = gameService.makeMove(gameId, row, col, current);
                grid[row][col] = current.intValue();

                moves.add(GameSimulationResult.MoveRecord.builder()
                        .row(row)
                        .col(col)
                        .playerId(current)
                        .threadId(Thread.currentThread().getId())
                        .build());

                if (updated.getStatus().isComplete()) {
                    return GameSimulationResult.builder()
                            .gameId(gameId)
                            .player1Id(p1)
                            .player2Id(p2)
                            .moves(moves)
                            .threadId(Thread.currentThread().getId())
                            .resultText("Winner: " + (updated.getWinnerId() != null ? updated.getWinnerId() : "Draw"))
                            .build();
                }

                current = current.equals(p1) ? p2 : p1;

            } catch (Exception e) {
                log.warn("Move failed for player {} in game {}: {}", current, gameId, e.getMessage());
            }
        }

        return GameSimulationResult.builder()
                .gameId(gameId)
                .player1Id(p1)
                .player2Id(p2)
                .moves(moves)
                .threadId(Thread.currentThread().getId())
                .resultText("Draw")
                .build();
    }
}
