package com.example.snake.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snake.R
import com.example.snake.adapters.HighScoreAdapter
import com.example.snake.model.Score
import com.example.snake.services.HighScoreService
import kotlinx.android.synthetic.main.fragment_leader_board.*

class LeaderBoardFragment : Fragment() {

    private lateinit var viewAdapter: HighScoreAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var highScoreService: HighScoreService? = null
    private var mIsBound = false

    private var scores: ArrayList<Score> = arrayListOf()

    //retrieve the service
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            highScoreService = (service as HighScoreService.LocalBinder).service
            highScoreService?.getList { doc, success ->
                doc?.let {
                    val scoresList = arrayListOf<Score>()
                    val scoreListTag = doc.getElementsByTagName("score")
                    for (i in 0 until scoreListTag.length) {
                        val scoreTag = scoreListTag.item(i)
                        scoresList.add(Score(scoreTag.attributes.getNamedItem("player").nodeValue,scoreTag.attributes.getNamedItem("value").nodeValue.toInt()))
                    }

                    viewAdapter.addAllScores(scoresList) // update the scores of the recycler view
                }
            }

        }
        override fun onServiceDisconnected(className: ComponentName) {
            highScoreService = null
        }
    }

    fun doBindService() {
        activity?.bindService(
            Intent(activity, HighScoreService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    fun doUnbindService() {
        if (mIsBound) {
            activity?.unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doBindService()

        viewManager = LinearLayoutManager(activity?.applicationContext)
        viewAdapter = HighScoreAdapter(scores)

        scoreRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LeaderBoardFragment().apply {

            }
    }
}
