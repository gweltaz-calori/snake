package com.example.snake.activity

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
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection
import android.widget.EditText
import com.example.snake.game.Direction
import com.example.snake.game.GameView
import com.example.snake.R
import com.example.snake.game.Render
import android.widget.Toast
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var menu: Menu? = null;
    private var isPlaying = false
    private var gameView : GameView? = null

    private var topButton: Button? = null
    private var leftButton: Button? = null
    private var rightButton: Button? = null
    private var bottomButton: Button? = null
    private var scoreboardButton: Button? = null
    private var addHighScoreButton: Button? = null
    private var gameOverTextview: TextView? = null
    private var gameWinTextview: TextView? = null
    private var playerTextField: EditText? = null
    private var scoreTextView: TextView? = null
    private val PERMISSIONS_REQUEST_CODE: Int = 7

    private var highScoreService: HighScoreService? = null
    private var mIsBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            highScoreService = (service as HighScoreService.LocalBinder).service
            highScoreService?.login { doc, success ->
                if(success) {
                    Toast.makeText(applicationContext,"Connecté" , Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext,"Impossible de se connecter au serveur" , Toast.LENGTH_SHORT).show()
                }
            }
        }
        override fun onServiceDisconnected(className: ComponentName) {
            highScoreService = null
        }
    }

    private fun doBindService() {
        bindService(
            Intent(this, HighScoreService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    private fun doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.grid)
        topButton = findViewById(R.id.topButton)
        leftButton = findViewById(R.id.leftButton)
        bottomButton = findViewById(R.id.bottomButton)
        rightButton = findViewById(R.id.rightButton)
        scoreboardButton = findViewById(R.id.scoreboardButton)
        gameOverTextview = findViewById(R.id.gameOverTextView)
        gameWinTextview = findViewById(R.id.gameWinTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        playerTextField = findViewById(R.id.playerEditText)
        addHighScoreButton = findViewById(R.id.addHighScoreButton)

        gameView?.initGame()

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

        addHighScoreButton?.setOnClickListener {
            gameView?.score?.let {
                try {
                    println("score $it")
                    highScoreService?.add(playerTextField?.text.toString(),it) { doc, success ->
                        if(!success) {
                            Toast.makeText(applicationContext,"Ce nom d'utilisateur existe déja",Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(applicationContext,"Votre score a été enregistré",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e:Exception) {
                }
            }
        }

        scoreboardButton?.setOnClickListener {
            val intent = Intent(this,HighScoreActivity::class.java)
            startActivity(intent)
        }


        //start calling onUpdate each frame
        Render.start {
            onUpdate()
        }

        gameView?.onGameOver {
            gameOverTextview?.visibility = View.VISIBLE
            menu?.getItem(0)?.setIcon(R.drawable.ic_play)
            isPlaying = false
        }

        gameView?.onGameWin {
            gameWinTextview?.visibility = View.VISIBLE
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

        doBindService()

    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
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
            gameView?.resetGame()
            menu?.getItem(0)?.setIcon(R.drawable.ic_stop)
        }
        else {
            menu?.getItem(0)?.setIcon(R.drawable.ic_play)
        }
    }
}


