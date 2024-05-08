package com.example.snake

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.DateTimePatternGenerator.DisplayWidth
import android.media.Image
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.snake.ui.theme.Blue
import com.example.snake.ui.theme.Gray
import com.example.snake.ui.theme.Neongreen

@Composable
fun SnakeScreen(
    state: SnakeState,
    onEvent: (SnakeEvent) -> Unit
) {
    //save score
    val sharedPreferences = LocalContext.current.getSharedPreferences("SnakeGamePrefs", Context.MODE_PRIVATE)
    var isNewHighScore by remember { mutableStateOf(false) }


    if (state.isGameOver) {
        val currentScore = state.snake.size - 1
        val previousScore = sharedPreferences.getInt("last_score", 0)

        if (currentScore > previousScore) {
            saveScoreToSharedPreferences(sharedPreferences, currentScore)
            isNewHighScore = true
        }
    }
    val highScore = sharedPreferences.getInt("last_score", 0)


    val foodImageBitmap = ImageBitmap.imageResource(id = R.drawable.ball)
    val snakeHeadImageBitmap = when(state.direction) {
        Direction.UP -> ImageBitmap.imageResource(id = R.drawable.snakeupcopy)
        Direction.DOWN -> ImageBitmap.imageResource(id = R.drawable.snakedowncopy)
        Direction.LEFT -> ImageBitmap.imageResource(id = R.drawable.snakeleftcopy)
        Direction.RIGHT -> ImageBitmap.imageResource(id = R.drawable.snakerightcopyy)
    }

    val context = LocalContext.current
    //remember value through app
    val foodSoundMP = remember { MediaPlayer.create(context, R.raw.points) }
    val overSoundMP = remember { MediaPlayer.create(context, R.raw.over) }
    val highSoundMP = remember { MediaPlayer.create(context, R.raw.high) }


    LaunchedEffect(key1 = state.snake.size) {
        if (state.snake.size != 1) {
            foodSoundMP?.start()
        }
    }

    LaunchedEffect(key1 = state.isGameOver&& !isNewHighScore) {
        if (state.isGameOver) {
            overSoundMP?.start()
        }
    }

    LaunchedEffect(key1 =  isNewHighScore && state.isGameOver) {
        if (state.isGameOver) {
            highSoundMP?.start()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), //take whole phone screen
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Card(
                modifier = Modifier
                    .padding(8.dp) //card padding
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp), //text padding
                        text = "Score: ${state.snake.size - 1}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        modifier = Modifier.padding(16.dp), //text padding
                        text = "High Score: ${highScore}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            Canvas( //draw shapes
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 2 / 3f) // 2:3 -> width:height
                    .pointerInput(state.gameState) {
                        if (state.gameState != GameState.STARTED) {
                            return@pointerInput
                        }
                        detectTapGestures { offset ->
                            onEvent(SnakeEvent.UpdateDirection(offset, size.width))
                        }
                    }

            ) {
                val cellSize = size.width / 20 //whole size of screen / 20
                drawGameBoard(
                    cellSize = cellSize,
                    cellColor = Gray,
                    borderCellColors = Neongreen,
                    gridWidth = state.xAxisGridSize,
                    gridHeight = state.yAxisGridSize
                )
                drawFood(
                    foodImage = foodImageBitmap,
                    cellSize = cellSize.toInt(),
                    coordinate = state.food

                )
                drawSnake(
                    snakeHeadImage = snakeHeadImageBitmap,
                    cellSize = cellSize,
                    snake = state.snake
                )
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f), //eqaul spaces
                    onClick = {
                        onEvent(SnakeEvent.ResetGame)
                        isNewHighScore = false
                              },
                    enabled = state.gameState == GameState.PAUSED || state.isGameOver
                ) {
                    Text(text = if(state.isGameOver) "Reset" else "New Game")
                }
                Spacer(modifier = Modifier.width(10.dp)) //space between two
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when(state.gameState) {
                            GameState.IDLE,GameState.PAUSED  -> onEvent(SnakeEvent.StartGame)
                            GameState.STARTED -> onEvent(SnakeEvent.PauseGame)
                        }
                    },
                    enabled = !state.isGameOver
                ) {
                    Text(
                        text = when(state.gameState) {
                            GameState.IDLE -> "Start"
                            GameState.STARTED -> "Pause"
                            GameState.PAUSED -> "Resume"
                        })
                }

            }

        }
        if (isNewHighScore && state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Adjust bottom padding as needed
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    modifier = Modifier.padding(16.dp), //text padding
                    text = "Congratulations!\nNew high score!",
                    style = MaterialTheme.typography.displayMedium // Larger font size and line height
                )
            }
        }
        AnimatedVisibility(visible = state.isGameOver && !isNewHighScore) {
            Text(
                modifier = Modifier.padding(16.dp), //text padding
                text = "Game Over",
                style = MaterialTheme.typography.displayMedium
            )
        }

    }

    
}

//score
private fun saveScoreToSharedPreferences(sharedPreferences: SharedPreferences, score: Int) {
    with(sharedPreferences.edit()) {
        putInt("last_score", score)
        apply()
    }
}

private fun DrawScope.drawGameBoard(
    cellSize: Float,
    cellColor: Color,
    borderCellColors: Color,
    gridWidth: Int,
    gridHeight: Int
){
    for (i in 0 until gridWidth) {
        for (j in 0 until gridHeight) {
            val isBorderCell = i == 0 || j == 0 || i == gridWidth -1 || j == gridHeight - 1
            drawRect(
                color = if(isBorderCell) borderCellColors
                else if ((i + j) % 2 == 0) cellColor
                else cellColor.copy(alpha = 0.5f), //transpatent 0.5
                topLeft = Offset(x = i * cellSize, y = j*cellSize ),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawFood(
    foodImage: ImageBitmap,
    cellSize: Int,
    coordinate: Coordinate
){
    drawImage(
        image = foodImage,
        dstOffset = IntOffset(
            x = (coordinate.x * cellSize),
            y = (coordinate.y * cellSize)
        ),
        dstSize = IntSize(cellSize, cellSize)
    )
}

private fun DrawScope.drawSnake(
    snakeHeadImage: ImageBitmap,
    cellSize: Float,
    snake: List<Coordinate>
){
    val cellSizeInt = cellSize.toInt()
    snake.forEachIndexed { index, coordinate ->
        val radius = if (index == snake.lastIndex) cellSize / 2.5f else cellSize/2
        if (index == 0) {
            drawImage(
                image = snakeHeadImage,
                dstOffset = IntOffset(
                    x = (coordinate.x * cellSizeInt),
                    y = (coordinate.y * cellSizeInt)
                ),
                dstSize = IntSize(cellSizeInt, cellSizeInt)
            )
        } else {
            drawCircle(
                color = Blue,
                center = Offset(
                    x = (coordinate.x * cellSize) + radius,
                    y = (coordinate.y * cellSize) + radius
                ),
                radius = radius
            )
        }
    }

}