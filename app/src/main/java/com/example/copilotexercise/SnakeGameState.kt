package com.example.copilotexercise

import androidx.compose.ui.geometry.Offset

// Represents the direction of the snake
public enum class Direction { UP, DOWN, LEFT, RIGHT }

// Represents the game state
public data class SnakeGameState(
    val snake: List<Offset> = listOf(Offset(5f, 5f)),
    val direction: Direction = Direction.RIGHT,
    val food: Offset = Offset(10f, 10f),
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val isRunning: Boolean = true,
    val isSuccess: Boolean = false
)
