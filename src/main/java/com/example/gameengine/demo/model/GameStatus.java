package com.example.gameengine.demo.model;

public enum GameStatus {
    WAITING,
    ONGOING,
    COMPLETE;

    public boolean isComplete() {
        return this == COMPLETE;
    }
}