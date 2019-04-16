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
    var score = 0

    var snake: Snake = Snake()
    var apple: Apple = getRandomApple()


    //these callback are called from the activity to know when the player has lost or the score has changed
    private var gameOverCallback : (() -> Unit)? = null
    private var scoreChangedCallback : (() -> Unit)? = null


    override fun onDraw(canvas: Canvas) {
        BORDER_PAINT.color = Color.BLACK

        super.onDraw(canvas)

        canvas.drawRect(bounds,BORDER_PAINT)

        // if we also hit the game bounds we lost the game
        doesSnakeHitGameBounds()?.let {
            gameOverCallback?.let { it() }
        } ?: kotlin.run { // otherwise we are inside the game bounds

            //update all the objects positions
            snake.update()
            apple.update()

            //draw each object onto the canvas
            snake.draw(canvas)
            apple.draw(canvas)

            // if our snake interesects the apple score change
            if(snake.intersectsApple(apple)) {
                onScoreChanged()
            }

            // the snake ate himself so we lost the game when can trigger the callback
            if (snake.doesHitHimself()) {
                gameOverCallback?.let { it() }
            }
        }
    }

    private fun doesSnakeHitGameBounds(): Tile? {
        return snake.tiles.find { (it.x < 0 || it.x > WIDTH - snake.size) || (it.y < 0 || it.y > HEIGHT - snake.size) }
    }

    private fun onScoreChanged() {
        //set the new position for the apple

        val randomApple = getRandomApple()

        apple.position.x = randomApple.position.x
        apple.position.y = randomApple.position.y
        score++ //increase the score
        scoreChangedCallback?.invoke() //call the callback
        snake.needCollect = true // we need to increase snake size
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

    fun getRandomApple(): Apple {
        return Apple(Tile((random(0, WIDTH / Snake.TILE_SIZE)) * Snake.TILE_SIZE, (random(0, HEIGHT / Snake.TILE_SIZE))* Snake.TILE_SIZE))
    }

    fun initGame() {
        layoutParams.width = WIDTH
        layoutParams.height = HEIGHT
    }

    fun resetGame() {
        snake = Snake()
        apple = getRandomApple()
        score = 0
    }
}