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
        val size = 40
    }

    var head = tiles.first()
    var tail = tiles.subList(1,tiles.size) //remove first element for tail
    val size = Snake.size
    var needCollect = false

    override fun update() {
        head = tiles.first()
        tail = tiles.subList(1,tiles.size)

        var newTiles = tiles.dropLast(1) //keep same size

        //grow by one
        if (needCollect) {
            newTiles = tiles
            needCollect = false
        }

        tiles = listOf(Tile(head.x + direction.x, head.y + direction.y)) + newTiles
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = color
        val newX = head.x
        val newY = head.y
        canvas.drawRect(Rect(newX,newY,newX + size,newY + size), paint)

        tail.forEach {
            val newTailX = it.x
            val newTailY = it.y
            canvas.drawRect(Rect(newTailX ,newTailY,newTailX + size ,newTailY + size ), paint)
        }
    }

    fun intersectsApple(apple: Apple): Boolean {
        return SuperRect.intersects(SuperRect(head.x,head.y,size,size),
            SuperRect(apple.position.x,apple.position.y,apple.size,apple.size)
        )
    }

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
    UP(0, -40),
    DOWN(0, 40),
    LEFT(-40, 0),
    RIGHT(40, 0);
}

data class Tile(var x :Int,var y: Int)