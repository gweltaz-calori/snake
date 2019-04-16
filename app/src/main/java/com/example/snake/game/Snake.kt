package com.example.snake.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.snake.extensions.SuperRect

open class Snake(
    var tiles: List<Tile> = listOf(Tile(0, 0)),
    var direction: Direction = Direction.RIGHT,
    val color: Int = Color.RED,
    val limit1: Tile,
    val limit2: Tile
) : DrawableInterface {

    companion object {
        var TILE_SIZE = 40
    }
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
        if (needCollect) {
            newTiles = tiles
            needCollect = false
        }

        tiles = listOf(Tile(head.x + direction.x, head.y + direction.y)) + newTiles //the new tiles list
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = color
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

    fun doesHitBounds(): Boolean {
        tiles.find { (it.x < limit1.x || it.x > limit2.x - size) || (it.y < limit1.y || it.y > limit2.y - size) }
            ?: return false
        return true
    }
}

enum class Direction(val x: Int, val y: Int) {
    STOP(0, 0),
    UP(0, -Snake.TILE_SIZE),
    DOWN(0, Snake.TILE_SIZE),
    LEFT(-Snake.TILE_SIZE, 0),
    RIGHT(Snake.TILE_SIZE, 0);
}

data class Tile(var x :Int,var y: Int)