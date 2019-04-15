package com.example.snake.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import org.w3c.dom.Document
import java.lang.Exception
import java.net.URL

class HighScoreService : Service() {

    private val NOTIFICATION_CHANNEL_ID = 101
    private val NOTIFICATION_CHANNEL_NAME = "WEB_CHANNEL"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val userLoginTask = UserLoginTask(this)
        try {
            userLoginTask.execute(URL("http://snake.struct-it.fr/login.php?user=snake&pwd=test"))
        }
        catch (e : Exception) {

        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Integer.toString(NOTIFICATION_CHANNEL_ID),NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notificationBuilder = Notification.Builder(this,Integer.toString(NOTIFICATION_CHANNEL_ID))
            val notification = notificationBuilder.build()
            startForeground(NOTIFICATION_CHANNEL_ID,notification)
        }
    }

    fun onUserLogged(success: Boolean?, document: Document?) {
        println(success)
    }
}