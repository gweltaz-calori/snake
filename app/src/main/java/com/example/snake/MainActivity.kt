package com.example.snake

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView

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


