package com.example.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.snake.extensions.SuperRect

class Snake :DrawableInterface {

    var tiles: List<Tile> = listOf(Tile(4, 0),Tile(3, 0), Tile(2, 0), Tile(1, 0))
    var direction : Direction = Direction.RIGHT
    var head = tiles.first()
    var tail = tiles.subList(1,tiles.size) //remove first element for taik
    val size = 40

    override fun update() {
        /*head = tiles.first()
        head.x += direction.x
        head.y += direction.y*/
        head = tiles.first()
        tail = tiles.subList(1,tiles.size)

        val newHead = Tile(head.x+direction.x,head.y + direction.y) // head after moving
        val newTail = tiles.dropLast(1)

        tiles = listOf(newHead) + newTail

    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
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
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -40),
    DOWN(0, 40),
    LEFT(-40, 0),
    RIGHT(40, 0);
}

data class Tile(var x :Int,var y: Int)