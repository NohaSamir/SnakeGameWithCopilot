package com.example.copilotexercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SnakeGameScreen(
    viewModel: SnakeGameViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(state.isGameOver, state.isRunning) {
        if (!state.isGameOver && state.isRunning) {
            while (state.isRunning && !state.isGameOver) {
                delay(200)
                viewModel.processIntent(SnakeGameIntent.Tick)
            }
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            kotlinx.coroutines.delay(2000)
            viewModel.processIntent(SnakeGameIntent.Restart)
        }
    }

    val headColor = animateColorAsState(
        targetValue = if (state.isGameOver) Color.Red else Color.Green,
        label = "SnakeHeadColor"
    ).value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Score: ${state.score}", color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(Color.DarkGray)
                .align(Alignment.CenterHorizontally)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val (dx, dy) = dragAmount
                        val direction = when {
                            Math.abs(dx) > Math.abs(dy) && dx > 0 -> Direction.RIGHT
                            Math.abs(dx) > Math.abs(dy) && dx < 0 -> Direction.LEFT
                            dy > 0 -> Direction.DOWN
                            dy < 0 -> Direction.UP
                            else -> null
                        }
                        direction?.let {
                            viewModel.processIntent(SnakeGameIntent.ChangeDirection(it))
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / 20f
                // Draw snake
                val snakeToDraw = if (state.isGameOver) {
                    state.snake.filter { it.x in 0f..19f && it.y in 0f..19f }
                } else {
                    state.snake
                }
                val head = snakeToDraw.firstOrNull()
                val body = if (snakeToDraw.size > 1) snakeToDraw.drop(1) else emptyList()
                head?.let {
                    drawRect(
                        color = headColor,
                        topLeft = androidx.compose.ui.geometry.Offset(it.x * cellSize, it.y * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
                body.forEach {
                    drawRect(
                        color = Color.Green,
                        topLeft = androidx.compose.ui.geometry.Offset(it.x * cellSize, it.y * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
                // Draw food
                drawRect(
                    color = Color.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(state.food.x * cellSize, state.food.y * cellSize),
                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Centered messages (fixed position, does not shift box)
        if (state.isGameOver) {
            Text("Game Over!", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.processIntent(SnakeGameIntent.Restart) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Restart")
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else if (state.isSuccess) {
            Text("Success!", color = Color.Yellow, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Centered buttons
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isRunning && !state.isGameOver && !state.isSuccess) {
                Button(onClick = { viewModel.processIntent(SnakeGameIntent.Pause) }) {
                    Text("Pause")
                }
            } else if (!state.isRunning && !state.isGameOver && !state.isSuccess) {
                Button(onClick = { viewModel.processIntent(SnakeGameIntent.Start) }) {
                    Text("Start")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
