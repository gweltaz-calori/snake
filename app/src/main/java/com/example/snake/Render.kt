package com.example.snake

import java.util.*

object Render {
    const val PERIOD = 1.toFloat()/10.toFloat()
    fun start(onUpdate: () -> Unit) {
         Timer().scheduleAtFixedRate(object :TimerTask() {
             override fun run() {
                 onUpdate()
             }
         },0,(PERIOD * 1000).toLong())
    }
}