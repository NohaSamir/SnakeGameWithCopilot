package com.example.copilotexercise

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

class SnakeGameViewModel : ViewModel() {
    private val _state = MutableStateFlow(SnakeGameState())
    val state: StateFlow<SnakeGameState> = _state

    fun processIntent(intent: SnakeGameIntent) {
        when (intent) {
            is SnakeGameIntent.Tick -> moveSnake()
            is SnakeGameIntent.ChangeDirection -> changeDirection(intent.direction)
            is SnakeGameIntent.Restart -> restartGame()
            is SnakeGameIntent.Pause -> pauseGame()
            is SnakeGameIntent.Start -> startGame()
        }
    }

    private fun moveSnake() {
        val currentState = _state.value
        if (currentState.isGameOver || currentState.isSuccess) return
        val head = currentState.snake.first()
        val newHead = when (currentState.direction) {
            Direction.UP -> Offset(head.x, head.y - 1)
            Direction.DOWN -> Offset(head.x, head.y + 1)
            Direction.LEFT -> Offset(head.x - 1, head.y)
            Direction.RIGHT -> Offset(head.x + 1, head.y)
        }
        val newSnake = listOf(newHead) + currentState.snake.dropLast(1)
        val ateFood = newHead == currentState.food
        val newScore = if (ateFood) currentState.score + 1 else currentState.score
        val updatedSnake = if (ateFood) listOf(newHead) + currentState.snake else newSnake
        val newFood = if (ateFood) randomFood(updatedSnake) else currentState.food
        val isGameOver = checkGameOver(updatedSnake)
        val isSuccess = newScore >= 5
        _state.value = currentState.copy(
            snake = updatedSnake,
            food = newFood,
            score = newScore,
            isGameOver = isGameOver,
            isSuccess = isSuccess
        )
    }

    private fun changeDirection(direction: Direction) {
        val current = _state.value.direction
        // Prevent reversing direction
        if ((current == Direction.UP && direction == Direction.DOWN) ||
            (current == Direction.DOWN && direction == Direction.UP) ||
            (current == Direction.LEFT && direction == Direction.RIGHT) ||
            (current == Direction.RIGHT && direction == Direction.LEFT)) {
            return
        }
        _state.value = _state.value.copy(direction = direction)
    }

    private fun restartGame() {
        _state.value = SnakeGameState()
    }

    private fun pauseGame() {
        _state.value = _state.value.copy(isRunning = false)
    }

    private fun startGame() {
        _state.value = _state.value.copy(isRunning = true)
    }

    private fun randomFood(snake: List<Offset>): Offset {
        var food: Offset
        do {
            food = Offset(Random.nextInt(0, 20).toFloat(), Random.nextInt(0, 20).toFloat())
        } while (snake.contains(food))
        return food
    }

    private fun checkGameOver(snake: List<Offset>): Boolean {
        val head = snake.first()
        // Check wall collision
        if (head.x < 0 || head.x >= 20 || head.y < 0 || head.y >= 20) return true
        // Check self collision only if snake length > 1
        if (snake.size > 1 && snake.drop(1).contains(head)) return true
        return false
    }
}
