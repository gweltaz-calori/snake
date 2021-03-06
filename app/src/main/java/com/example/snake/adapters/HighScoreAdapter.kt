package com.example.snake.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.snake.R
import com.example.snake.model.Score

// The adapter for the high score
class HighScoreAdapter(private val myDataset: ArrayList<Score>) :
    RecyclerView.Adapter<HighScoreAdapter.MyViewHolder>() {

    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HighScoreAdapter.MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_score, parent, false) as TextView

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = "player : ${myDataset[position].player}, score : ${myDataset[position].value}"
    }

    override fun getItemCount() = myDataset.size

    fun addAllScores(scores:List<Score>) {
        scores.forEach {
            addScore(it)
        }
    }

    fun addScore(score: Score) {
        myDataset.add(score);
        notifyItemInserted(myDataset.size - 1);
    }
 }