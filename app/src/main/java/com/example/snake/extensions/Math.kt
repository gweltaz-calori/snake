package com.example.snake.extensions

import java.util.*

fun random ( min:Int,  max : Int): Int {
    val r = Random()
    return r.nextInt(max - min) + min
}