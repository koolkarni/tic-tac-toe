package com.example.gameengine.demo.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSimulationResult {
    private Long gameId;
    private Long player1Id;
    private Long player2Id;
    private List<MoveRecord> moves;
    private Long threadId;
    private String resultText;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MoveRecord {
        private int row;
        private int col;
        private Long playerId;
        private Long threadId;
    }
}