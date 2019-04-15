package com.example.snake

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.snake.services.HighScoreService
import android.content.pm.PackageManager
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var menu: Menu? = null;
    private var isPlaying = false
    private var gameView : GameView? = null

    private var topButton: Button? = null
    private var leftButton: Button? = null
    private var rightButton: Button? = null
    private var bottomButton: Button? = null
    private var gameOverTextview: TextView? = null
    private var scoreTextView: TextView? = null
    private val PERMISSIONS_REQUEST_CODE: Int = 7


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.grid)
        topButton = findViewById(R.id.topButton)
        leftButton = findViewById(R.id.leftButton)
        bottomButton = findViewById(R.id.bottomButton)
        rightButton = findViewById(R.id.rightButton)
        gameOverTextview = findViewById(R.id.gameOverTextView)
        scoreTextView = findViewById(R.id.scoreTextView)

        topButton?.setOnClickListener {
            click(Direction.UP)
        }

        leftButton?.setOnClickListener {
            click(Direction.LEFT)
        }

        bottomButton?.setOnClickListener {
            click(Direction.DOWN)
        }

        rightButton?.setOnClickListener {
            click(Direction.RIGHT)
        }

        Render.start {
            onUpdate()
        }

        gameView?.onGameOver {
            gameOverTextview?.visibility = View.VISIBLE
            changeStatus()
        }

        gameView?.onScoreChanged {
            scoreTextView?.text = "Score : ${gameView?.score.toString()}"
        }
    }


    override fun onStart() {
        super.onStart()

        val permissions = listOf(android.Manifest.permission.WAKE_LOCK,android.Manifest.permission.INTERNET,android.Manifest.permission.ACCESS_NETWORK_STATE)

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(),PERMISSIONS_REQUEST_CODE)

        val intent = Intent(this,HighScoreService::class.java)
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           startForegroundService(intent)
        }
        else {
            startService(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (permissions.size === 3) {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED && grantResults[1] === PackageManager.PERMISSION_GRANTED && grantResults[2] === PackageManager.PERMISSION_GRANTED) {
                    Log.d("Snake", "Permissions granted")
                    val intent = Intent(this, HighScoreService::class.java)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                } else {
                    if (grantResults[0] !== PackageManager.PERMISSION_GRANTED) {
                        Log.d("Snake", "Wake Lock permission not granted")
                    }
                    if (grantResults[1] !== PackageManager.PERMISSION_GRANTED) {
                        Log.d("Snake", "Internet permission not granted")
                    }
                    if (grantResults[2] !== PackageManager.PERMISSION_GRANTED) {
                        Log.d("Snake", "Access network permission not granted")
                    }
                }
            }
            else -> {
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent(this,HighScoreService::class.java)
        stopService(intent)

    }

    private fun onUpdate() {
        if(isPlaying) {
            gameView?.invalidate()
        }
    }

    private fun click(direction: Direction) {
        gameView?.setDirection(direction)
    }

    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        menu = menuItem
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.start -> {
                changeStatus()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeStatus() {
        isPlaying = !isPlaying
        toggleIcon()
    }

    private fun toggleIcon() {
        if(isPlaying) {
            menu?.getItem(0)?.setIcon(R.drawable.ic_stop)
        }
        else {
            menu?.getItem(0)?.setIcon(R.drawable.ic_play)
            gameView?.resetGame()
        }
    }
}


