package com.example.ohhell.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ohhell.databinding.ItemScoreBinding
import com.example.ohhell.model.PlayerRoundScore

class ScoresAdapter(
    private val onBidChanged: (String, Int?) -> Unit,
    private val onTricksChanged: (String, Int?) -> Unit
) : ListAdapter<PlayerRoundScore, ScoresAdapter.ScoreViewHolder>(ScoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreViewHolder(binding, onBidChanged, onTricksChanged)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScoreViewHolder(
        private val binding: ItemScoreBinding,
        private val onBidChanged: (String, Int?) -> Unit,
        private val onTricksChanged: (String, Int?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentPlayerName: String = ""

        init {
            binding.etBid.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val bid = s?.toString()?.toIntOrNull()
                    onBidChanged(currentPlayerName, bid)
                }
            })

            binding.etTricks.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val tricks = s?.toString()?.toIntOrNull()
                    onTricksChanged(currentPlayerName, tricks)
                }
            })
        }

        fun bind(playerRoundScore: PlayerRoundScore) {
            currentPlayerName = playerRoundScore.playerName
            
            binding.tvPlayerName.text = playerRoundScore.playerName
            
            // Set bid without triggering listener
            val bidText = playerRoundScore.bid?.toString() ?: ""
            if (binding.etBid.text.toString() != bidText) {
                binding.etBid.setText(bidText)
            }
            
            // Set tricks without triggering listener
            val tricksText = playerRoundScore.tricksWon?.toString() ?: ""
            if (binding.etTricks.text.toString() != tricksText) {
                binding.etTricks.setText(tricksText)
            }
            
            // Calculate and display scores
            val roundScore = playerRoundScore.calculateScore()
            binding.tvRoundScore.text = if (playerRoundScore.isComplete()) roundScore.toString() else "-"
            
            // Note: Total score needs to be passed from the game state
            // For now, we'll leave it as is and update it from the activity
        }
        
        fun updateTotalScore(totalScore: Int) {
            binding.tvTotalScore.text = totalScore.toString()
        }
    }

    private class ScoreDiffCallback : DiffUtil.ItemCallback<PlayerRoundScore>() {
        override fun areItemsTheSame(oldItem: PlayerRoundScore, newItem: PlayerRoundScore): Boolean {
            return oldItem.playerName == newItem.playerName
        }

        override fun areContentsTheSame(oldItem: PlayerRoundScore, newItem: PlayerRoundScore): Boolean {
            return oldItem == newItem
        }
    }
}