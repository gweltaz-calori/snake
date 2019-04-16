package com.example.snake.extensions

import java.util.*

// random between 2 numbers
fun random ( min:Int,  max : Int): Int {
    val r = Random()
    return r.nextInt(max - min) + min
}

// Rect helper class
data class SuperRect(val x: Int,val y:Int,val width:Int,val height:Int) {
    companion object {
        fun intersects(a:SuperRect,b:SuperRect): Boolean {
            return Math.abs(a.x - b.x) * 2 < a.width + b.width &&
                    Math.abs(a.y - b.y) * 2 < a.height + b.height
        }
    }
}