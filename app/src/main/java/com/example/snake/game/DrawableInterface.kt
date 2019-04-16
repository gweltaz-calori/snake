package com.example.snake.game

import android.graphics.Canvas

// Each canvas object must implement these 2 methods
interface DrawableInterface {
    fun draw(canvas: Canvas) //draw literally on the canvas
    fun update() // call the modification on the position etc...
}