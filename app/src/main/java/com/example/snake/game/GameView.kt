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

// That 's the main class that manage the game
class GameView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet) {

    //some constants
    val WIDTH = resources.getDimension(R.dimen.game_size).toInt()
    val HEIGHT = resources.getDimension(R.dimen.game_size).toInt()
    val BORDER_PAINT = Paint()

    val bounds = Rect(0,0,WIDTH,HEIGHT)
    val limit1 = Tile(bounds.left, bounds.top)
    val limit2 = Tile(bounds.right, bounds.bottom)
    var score = 0

    var snakeAI: SnakeAI = SnakeAI(
        listOf(Tile(
            WIDTH - Snake.TILE_SIZE, HEIGHT - Snake.TILE_SIZE)),
        _limit1 = limit1, _limit2 = limit2
    )
    var snake: Snake = Snake(limit1 = limit1, limit2 = limit2)
    var apple: Apple = getRandomApple()

    //these callback are called from the activity to know when the player has lost or the score has changed
    private var gameOverCallback : (() -> Unit)? = null
    private var gameWinCallback : (() -> Unit)? = null
    private var scoreChangedCallback : (() -> Unit)? = null


    override fun onDraw(canvas: Canvas) {
        BORDER_PAINT.color = Color.BLACK

        super.onDraw(canvas)

        canvas.drawRect(bounds,BORDER_PAINT)

        val snakeHitBounds = snake.doesHitBounds()
        val snakeAIHitBounds = snakeAI.doesHitBounds()

        if (snakeHitBounds) {
            gameOverCallback?.let { it() }
        } else if (snakeAIHitBounds) {
            gameWinCallback?.let { it() }
        } else {
            snakeAI.findApple(apple)

            snakeAI.update()
            snake.update()
            apple.update()

            snakeAI.draw(canvas)
            snake.draw(canvas)
            apple.draw(canvas)

            if (snake.intersectsApple(apple)) {
                onScoreChanged(1)
            }
            if (snakeAI.intersectsApple(apple)) {
                onScoreChanged(-1)
            }

            // the snake ate himself so we lost the game when can trigger the callback
            if (snake.doesHitHimself()) {
                gameOverCallback?.let { it() }
            }
            if (snakeAI.doesHitHimself()) {
                gameWinCallback?.let { it() }
            }
            // TODO: check that two snakes doesn't collide
        }
    }

    private fun onScoreChanged(inc: Int) {
        val randomApple = getRandomApple()

        apple.position.x = randomApple.position.x
        apple.position.y = randomApple.position.y

        if (inc > 0) {
            // if inc is positive, that means the player eat the apple
            score++
            scoreChangedCallback?.invoke()
            snake.needCollect = true
        } else {
            snakeAI.needCollect = true
        }
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

    fun getRandomApple(): Apple {
        return Apple(Tile((random(0, WIDTH / Snake.TILE_SIZE)) * Snake.TILE_SIZE, (random(0, HEIGHT / Snake.TILE_SIZE))* Snake.TILE_SIZE))
    }

    fun initGame() {
        layoutParams.width = WIDTH
        layoutParams.height = HEIGHT
    }

    fun resetGame() {
        snakeAI = SnakeAI(
            listOf(Tile(WIDTH - Snake.TILE_SIZE, HEIGHT - Snake.TILE_SIZE)),
            _limit1 = limit1, _limit2 = limit2
        )
        snake = Snake(limit1 = limit1, limit2 = limit2)
        apple = getRandomApple()
        score = 0
    }
}