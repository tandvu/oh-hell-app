package com.example.ohhell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ohhell.adapter.ScoresAdapter
import com.example.ohhell.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGameBinding
    private lateinit var scoresAdapter: ScoresAdapter
    private val gameState = MainActivity.gameState
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        updateUI()
    }
    
    private fun setupRecyclerView() {
        scoresAdapter = ScoresAdapter(
            onBidChanged = { playerName, bid ->
                bid?.let { gameState.setBid(playerName, it) }
                updateUI()
            },
            onTricksChanged = { playerName, tricks ->
                tricks?.let { gameState.setTricks(playerName, it) }
                updateUI()
            }
        )
        
        binding.rvScores.apply {
            adapter = scoresAdapter
            layoutManager = LinearLayoutManager(this@GameActivity)
        }
    }
    
    private fun setupClickListeners() {
        binding.btnNextRound.setOnClickListener {
            nextRound()
        }
        
        binding.btnNewGame.setOnClickListener {
            newGame()
        }
    }
    
    private fun nextRound() {
        if (!gameState.canAdvanceToNextRound()) {
            Toast.makeText(this, "Please enter bids and tricks for all players", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!gameState.advanceToNextRound()) {
            return
        }
        
        if (gameState.isGameFinished) {
            showGameOverDialog()
        } else {
            updateUI()
        }
    }
    
    private fun newGame() {
        AlertDialog.Builder(this)
            .setTitle("New Game")
            .setMessage("Are you sure you want to start a new game? This will reset all scores.")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    private fun showGameOverDialog() {
        val winner = gameState.getWinner()
        val message = if (winner != null) {
            getString(R.string.winner, winner.name)
        } else {
            getString(R.string.game_over)
        }
        
        AlertDialog.Builder(this)
            .setTitle(R.string.game_over)
            .setMessage(message)
            .setPositiveButton("New Game") { _, _ ->
                newGame()
            }
            .setNegativeButton("View Scores", null)
            .setCancelable(false)
            .show()
    }
    
    private fun updateUI() {
        val currentRound = gameState.currentRound
        
        if (currentRound != null) {
            binding.tvRoundNumber.text = "Round ${currentRound.roundNumber}"
            binding.tvCardsDealt.text = getString(R.string.cards_dealt, currentRound.cardsDealt)
            binding.tvTotalBids.text = getString(R.string.total_bids, gameState.getTotalBids())
            
            val playerScores = currentRound.playerScores.values.toList()
            scoresAdapter.submitList(playerScores)
            
            // Update total scores in adapter
            gameState.players.forEachIndexed { index, player ->
                val viewHolder = binding.rvScores.findViewHolderForAdapterPosition(index) as? ScoresAdapter.ScoreViewHolder
                viewHolder?.updateTotalScore(player.totalScore)
            }
            
            binding.btnNextRound.isEnabled = gameState.canAdvanceToNextRound()
        }
    }
}