package com.example.snakegamestorage.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snakegamestorage.model.Direction
import com.example.snakegamestorage.model.Point
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

@Composable
fun SnakeGame(factory: GameViewModelFactory) {
    val viewModel: GameViewModel = viewModel(factory = factory)
    val gridSize = 20
    val cellSize = 40f

    var snake by remember { mutableStateOf(listOf(Point(5, 5), Point(5, 6), Point(5, 7))) }
    var food by remember { mutableStateOf(Point(10, 10)) }
    var direction by remember { mutableStateOf(Direction.UP) }
    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    val topScores by viewModel.topScores.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTopScores()
    }

    LaunchedEffect(snake, direction, gameOver) {
        if (!gameOver) {
            delay(200L)
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Point(head.x, head.y - 1)
                Direction.DOWN -> Point(head.x, head.y + 1)
                Direction.LEFT -> Point(head.x - 1, head.y)
                Direction.RIGHT -> Point(head.x + 1, head.y)
            }

            val newSnake = listOf(newHead) + snake.dropLast(1)

            if (newHead.x < 0 || newHead.y < 0 ||
                newHead.x >= gridSize || newHead.y >= gridSize ||
                newHead in snake
            ) {
                gameOver = true
                viewModel.addScore(score)
            } else {
                snake = newSnake
                if (newHead == food) {
                    snake = snake + snake.last()
                    score = snake.size - 3
                    food = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size((gridSize * cellSize).dp)
                    .background(Color.Blue)
            ) {
                drawRect(
                    color = Color.Red,
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = 2.dp.toPx())
                )
                snake.forEach { point ->
                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(point.x * cellSize, point.y * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(food.x * cellSize, food.y * cellSize),
                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                )
            }

            if (gameOver) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Score: $score",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Top Scores:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    topScores.forEach { gameScore ->
                        Text(
                            "${gameScore.score} - ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(gameScore.timestamp))}",
                            color = Color.White
                        )
                    }
                    
                    val errorMessage by viewModel.errorMessage.collectAsState()
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = Color.Yellow, // Use Yellow to verify it's drawn
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        ControlButtons(
            onUp = { if (direction != Direction.DOWN) direction = Direction.UP },
            onDown = { if (direction != Direction.UP) direction = Direction.DOWN },
            onLeft = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
            onRight = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
            onRestart = {
                gameOver = false
                snake = listOf(Point(5, 5), Point(5, 6), Point(5, 7))
                direction = Direction.UP
                food = Point(10, 10)
                score = 0
            },
            gameOver = gameOver
        )
    }
}

@Composable
fun ControlButtons(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onRestart: () -> Unit,
    gameOver: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameOver) {
            Button(onClick = onRestart) {
                Text(text = "Перезапуск")
            }
        } else {
            Button(onClick = onUp) { Text("^") }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onLeft) { Text("<") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onRight) { Text(">") }
            }
            Button(onClick = onDown, modifier = Modifier.padding(top = 8.dp)) { Text("v") }
        }
    }
}
