package com.example.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class Snake :DrawableInterface {

    var tiles: List<Tile> = listOf(Tile(4, 0),Tile(3, 0), Tile(2, 0), Tile(1, 0))
    var direction : Direction = Direction.RIGHT
    var head = tiles.first()
    var tail = tiles.subList(1,tiles.size) //remove first element for taik

    val size = 50

    override fun update() {
        head = tiles.first()
        head.x += direction.x
        head.y += direction.y
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        val newX = head.x + 1
        val newY = head.y + 1
        canvas.drawRect(Rect(newX,newY,newX + size,newY + size), paint)

        tail.forEach {
            val newTailX = it.x + 1
            val newTailY = it.y + 1
            canvas.drawRect(Rect(newTailX,newTailY,newTailX + size,newTailY + size), paint)
        }
    }

    fun intersectsApple(apple: Apple): Boolean {
        return apple.position === head
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);
}

data class Tile(var x :Int,var y: Int)