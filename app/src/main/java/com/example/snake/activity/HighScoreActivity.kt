package com.example.snake.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.snake.R
import com.example.snake.adapters.HighScoreAdapter
import com.example.snake.model.Score
import com.example.snake.services.HighScoreService



class HighScoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: HighScoreAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var highScoreService: HighScoreService? = null
    private var mIsBound = false

    private var scores: ArrayList<Score> = arrayListOf()

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            highScoreService = (service as HighScoreService.LocalBinder).service
            highScoreService?.getList {
                val scoresList = arrayListOf<Score>()
                val scoreListTag = it.getElementsByTagName("score")
                for (i in 0 until scoreListTag.length) {
                    val scoreTag = scoreListTag.item(i)
                    scoresList.add(Score(scoreTag.attributes.getNamedItem("player").nodeValue,scoreTag.attributes.getNamedItem("value").nodeValue.toInt()))
                }

                viewAdapter.addAllScores(scoresList)
            }

        }
        override fun onServiceDisconnected(className: ComponentName) {
            highScoreService = null
        }
    }

    fun doBindService() {
        bindService(
            Intent(this, HighScoreService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    fun doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.snake.R.layout.activity_high_score)

        doBindService()

        viewManager = LinearLayoutManager(this)
        viewAdapter = HighScoreAdapter(scores)

        recyclerView = findViewById<RecyclerView>(com.example.snake.R.id.score_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
