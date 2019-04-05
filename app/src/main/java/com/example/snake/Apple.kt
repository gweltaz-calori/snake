package com.example.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class Apple(tile: Tile) : DrawableInterface {

    val position:Tile = tile

    override fun update() {

    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.GREEN
        canvas.drawRect(Rect(position.x,position.y,position.x + 50,position.y + 50), paint)
    }

}