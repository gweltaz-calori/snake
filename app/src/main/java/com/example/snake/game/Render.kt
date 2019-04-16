package com.example.snake.game

import android.os.Handler
import java.util.*

// The Render singleton is responsible of calling a function each frame for a given interval
object Render {
    const val PERIOD = 1.toFloat()/10.toFloat() // how much we refresh
    fun start(onUpdate: () -> Unit) {
        Timer().scheduleAtFixedRate(object :TimerTask() {
            override fun run() {
                onUpdate() // call the callback each frame
            }
        },0,(PERIOD * 1000).toLong())
    }
}