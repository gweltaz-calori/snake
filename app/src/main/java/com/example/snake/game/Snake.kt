package com.example.snake.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.snake.extensions.SuperRect

class Snake : DrawableInterface {

    companion object {
        var TILE_SIZE = 40
    }

    var tiles: List<Tile> = listOf(Tile(0, 0)) // list of each tile the snake is
    var direction : Direction = Direction.RIGHT // which direction the snake should go
    var head = tiles.first() //head is the first element
    var tail = tiles.subList(1,tiles.size) //remove first element for taik
    val size = Snake.TILE_SIZE // snake size
    var needCollect = false // do we need to increase snake size

    //called each frame
    override fun update() {
        head = tiles.first() // the new head
        tail = tiles.subList(1,tiles.size) // the new tail

        var newTiles = tiles.dropLast(1) //keep same size

        //grow by one
        if(needCollect) {
            newTiles = tiles
            needCollect = false
        }

        tiles = listOf(Tile(head.x + direction.x, head.y + direction.y)) + newTiles //the new tiles list
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        val newX = head.x
        val newY = head.y
        canvas.drawRect(Rect(newX,newY,newX + size,newY + size), paint) // draw the head

        tail.forEach { // draw each tile from the tail
            val newTailX = it.x
            val newTailY = it.y
            canvas.drawRect(Rect(newTailX ,newTailY,newTailX + size ,newTailY + size ), paint)
        }
    }

    //check if the snake intersects with the apple
    fun intersectsApple(apple: Apple): Boolean {
        return SuperRect.intersects(SuperRect(head.x,head.y,size,size),
            SuperRect(apple.position.x,apple.position.y,apple.size,apple.size)
        )
    }

    //check if the snake hit himself
    fun doesHitHimself(): Boolean {
        return tail.find { SuperRect.intersects(SuperRect(head.x,head.y,size,size),SuperRect(it.x,it.y,size,size)) } != null
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -Snake.TILE_SIZE),
    DOWN(0, Snake.TILE_SIZE),
    LEFT(-Snake.TILE_SIZE, 0),
    RIGHT(Snake.TILE_SIZE, 0);
}

data class Tile(var x :Int,var y: Int)