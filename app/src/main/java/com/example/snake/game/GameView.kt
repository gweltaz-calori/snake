package com.example.snake.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.snake.R

import com.example.snake.extensions.random

class GameView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet) {

    val WIDTH = resources.getDimension(R.dimen.game_size).toInt()
    val HEIGHT = resources.getDimension(R.dimen.game_size).toInt()
    val BORDER_PAINT = Paint()
    val bounds = Rect(0,0,WIDTH,HEIGHT)
    val limit1 = Tile(bounds.left, bounds.top)
    val limit2 = Tile(bounds.right, bounds.bottom)
    var score = 0

    var snakeAI: SnakeAI = SnakeAI(listOf(Tile(
        WIDTH - Snake.size, HEIGHT - Snake.size)))
    var snake: Snake = Snake()
    var apple: Apple =
        Apple(Tile(random(0, WIDTH), random(0, HEIGHT)))

    private var gameOverCallback : (() -> Unit)? = null
    private var gameWinCallback : (() -> Unit)? = null
    private var scoreChangedCallback : (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        BORDER_PAINT.color = Color.BLACK

        layoutParams.width = WIDTH
        layoutParams.height = HEIGHT

        super.onDraw(canvas)

        canvas.drawRect(bounds,BORDER_PAINT)

        val snakeHitBounds = snake.doesHitBounds(limit1, limit2)
        val snakeAIHitBounds = snakeAI.doesHitBounds(limit1, limit2)

        if (snakeHitBounds) {
            gameOverCallback?.let { it() }
        } else if (snakeAIHitBounds) {
            gameWinCallback?.let { it() }
        } else {
            snakeAI.update()
            snake.update()
            apple.update()

            snakeAI.draw(canvas)
            snake.draw(canvas)
            apple.draw(canvas)

            if (snake.intersectsApple(apple)) {
                onScoreChanged()
            }

            if (snake.doesHitHimself()) {
                gameOverCallback?.let { it() }
            }
            if (snakeAI.doesHitHimself()) {
                gameWinCallback?.let { it() }
            }
        }
    }

    private fun onScoreChanged() {
        apple.position.x = random(0,WIDTH)
        apple.position.y = random(0,HEIGHT)
        score++
        scoreChangedCallback?.invoke()
        snake.needCollect = true
    }

    fun onGameOver(onGameOver: () -> Unit) {
        gameOverCallback = onGameOver
    }

    fun onGameWin(onGameWin: () -> Unit) {
        gameWinCallback = onGameWin
    }

    fun onScoreChanged(onScoreChanged : () -> Unit) {
        scoreChangedCallback = onScoreChanged
    }

    fun setDirection(direction: Direction) {
        snake.direction = direction
    }

    fun resetGame() {
        snakeAI = SnakeAI(listOf(Tile(WIDTH - Snake.size, HEIGHT - Snake.size)))
        snake = Snake()
        apple = Apple(Tile(random(0, WIDTH), random(0, HEIGHT)))
        score = 0
    }
}