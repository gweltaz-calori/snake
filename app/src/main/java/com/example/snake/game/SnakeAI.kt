package com.example.snake.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class SnakeAI(val _tiles: List<Tile>) : Snake(
    tiles = _tiles,
    direction = Direction.LEFT,
    color = Color.BLUE
) {

}