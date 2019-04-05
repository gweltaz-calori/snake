package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.Toast

import com.example.snake.extensions.random

class GameView(context: Context,attributeSet: AttributeSet) : View(context,attributeSet) {

    val WIDTH = 800
    val HEIGHT = 800
    val BORDER_PAINT = Paint()
    val bounds = Rect(0,0,WIDTH,HEIGHT)
    var score = 0

    var snake: Snake = Snake()
    var apple: Apple = Apple(Tile(random(0,WIDTH),random(0,HEIGHT)))

    private var gameOverCallback : (() -> Unit)? = null
    private var scoreChangedCallback : (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        BORDER_PAINT.color = Color.BLACK

        layoutParams.width = WIDTH
        layoutParams.height = HEIGHT

        super.onDraw(canvas)

        canvas.drawRect(bounds,BORDER_PAINT)

        snake.tiles.find { (it.x < 0 || it.x > WIDTH - snake.size) || (it.y < 0 || it.y > HEIGHT - snake.size) }?.let {
            gameOverCallback?.let { it() }
        } ?: kotlin.run {
            snake.update()
            apple.update()

            snake.draw(canvas)
            apple.draw(canvas)

            if(snake.intersectsApple(apple)) {
                apple.position.x = random(0,WIDTH)
                apple.position.y = random(0,HEIGHT)
                score++
                scoreChangedCallback?.invoke()
                snake.needCollect = true
            }
        }
    }

    fun onGameOver(onGameOver: () -> Unit) {
        gameOverCallback = onGameOver
    }

    fun onScoreChanged(onScoreChanged : () -> Unit) {
        scoreChangedCallback = onScoreChanged
    }

    fun setDirection(direction: Direction) {
        snake.direction = direction
    }

    fun resetGame() {
        snake = Snake()
        apple = Apple(Tile(random(0,WIDTH),random(0,HEIGHT)))
        score = 0
    }
}