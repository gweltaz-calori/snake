package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class SnakeView(context: Context,attrs: AttributeSet) : GridView(context,attrs) {
    override fun updateTiles() {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}