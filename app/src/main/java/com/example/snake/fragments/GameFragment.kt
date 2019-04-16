package com.example.snake.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.snake.R
import com.example.snake.game.Direction
import com.example.snake.game.Render
import com.example.snake.services.HighScoreService
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private var isPlaying = false
    private var mIsBound = false
    private var highScoreService: HighScoreService? = null
    private val PERMISSIONS_REQUEST_CODE: Int = 7

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            highScoreService = (service as HighScoreService.LocalBinder).service
            highScoreService?.login { doc, success ->
                if(success) {
                    activity?.let {
                        val snackBar = Snackbar.make(
                            it.findViewById(android.R.id.content),
                            "Connecté", Snackbar.LENGTH_SHORT
                        )
                        snackBar.show()
                    }

                }
                else {
                    activity?.let {
                        val snackBar = Snackbar.make(
                            it.findViewById(android.R.id.content),
                            "Impossible de se connecter au serveur", Snackbar.LENGTH_SHORT
                        )
                        snackBar.show()
                    }

                }
            }
        }
        override fun onServiceDisconnected(className: ComponentName) {
            highScoreService = null
        }
    }

    override fun onStart() {
        super.onStart()

        val permissions = listOf(android.Manifest.permission.WAKE_LOCK,android.Manifest.permission.INTERNET,android.Manifest.permission.ACCESS_NETWORK_STATE)

        requestPermissions(permissions.toTypedArray(),PERMISSIONS_REQUEST_CODE)

        doBindService()
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent(activity?.applicationContext,HighScoreService::class.java)
        activity?.stopService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    private fun doBindService() {
        activity?.bindService(
            Intent(activity?.applicationContext, HighScoreService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    private fun doUnbindService() {
        if (mIsBound) {
            activity?.unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameView?.initGame()
        playGameButton.setOnClickListener {
            launchGame()
        }

        playAgainButton.setOnClickListener {
            launchGame()
        }

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

        sendHighscoreButton?.setOnClickListener {
            gameView?.score?.let {
                try {
                    highScoreService?.add(playerNameEditText?.text.toString(),it) { doc, success ->
                        if(!success) {
                            activity?.let {
                                val snackBar = Snackbar.make(
                                    it.findViewById(android.R.id.content),
                                    "Ce nom d'utilisateur existe déja", Snackbar.LENGTH_SHORT
                                )
                                snackBar.show()
                            }

                            val v = activity?.window?.currentFocus
                            if (v != null) {
                                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(v.windowToken, 0)
                            }
                        }
                        else {
                            activity?.let {
                                val snackBar = Snackbar.make(
                                    it.findViewById(android.R.id.content),
                                    "Votre score a été enregistré", Snackbar.LENGTH_SHORT
                                )
                                snackBar.show()
                            }
                        }
                    }
                }
                catch (e: Exception) {
                }
            }
        }


        Render.start {
            onUpdate()
        }

        gameView?.onGameOver {
            isPlaying = false
            gameLayout.visibility = View.GONE
            gameOverLayout.visibility = View.VISIBLE
        }

        gameView?.onGameWin {
            gameLayout.visibility = View.GONE
            gameOverText.text = getString(R.string.win_against_ia)

            gameOverLayout.visibility = View.VISIBLE
        }

        gameView?.onScoreChanged {
            setScore()
        }
    }

    private fun launchGame() {
        gameIntroLayout.visibility = View.GONE
        gameLayout.visibility = View.VISIBLE
        gameOverLayout.visibility = View.GONE
        gameOverText.text = getString(R.string.game_over)

        setScore()
        gameView?.resetGame()
        isPlaying = true
    }

    private fun setScore() {
        scoreValue?.text = "Score : ${gameView?.score.toString()}"
    }

    private fun onUpdate() {
        if(isPlaying) {
            gameView?.invalidate()
        }
    }

    private fun click(direction: Direction) {
        gameView?.setDirection(direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.snake.R.layout.fragment_game, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GameFragment().apply {
            }
    }
}
