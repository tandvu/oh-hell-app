package com.example.ohhell.model

class GameState {
    private val _players: MutableList<Player> = mutableListOf()
    private val _rounds: MutableList<Round> = mutableListOf()
    private var _currentRoundIndex: Int = 0
    
    val players: List<Player> get() = _players.toList()
    val rounds: List<Round> get() = _rounds.toList()
    val currentRound: Round? get() = if (_currentRoundIndex < _rounds.size) _rounds[_currentRoundIndex] else null
    val currentRoundIndex: Int get() = _currentRoundIndex
    val isGameFinished: Boolean get() = _currentRoundIndex >= _rounds.size
    
    fun addPlayer(playerName: String): Boolean {
        if (_players.any { it.name == playerName }) return false
        _players.add(Player(playerName))
        return true
    }
    
    fun removePlayer(playerName: String) {
        _players.removeAll { it.name == playerName }
    }
    
    fun startGame(maxCards: Int = 10) {
        _rounds.clear()
        _currentRoundIndex = 0
        
        // Reset player scores
        _players.forEach { it.totalScore = 0 }
        
        // Create rounds: go up to maxCards, then down to 1
        val roundsUp = (1..maxCards).toList()
        val roundsDown = (maxCards - 1 downTo 1).toList()
        val allRounds = roundsUp + roundsDown
        
        allRounds.forEachIndexed { index, cardsDealt ->
            val round = Round(index + 1, cardsDealt)
            _players.forEach { player ->
                round.playerScores[player.name] = PlayerRoundScore(player.name)
            }
            _rounds.add(round)
        }
    }
    
    fun setBid(playerName: String, bid: Int): Boolean {
        val round = currentRound ?: return false
        val playerScore = round.playerScores[playerName] ?: return false
        playerScore.bid = bid
        return true
    }
    
    fun setTricks(playerName: String, tricks: Int): Boolean {
        val round = currentRound ?: return false
        val playerScore = round.playerScores[playerName] ?: return false
        playerScore.tricksWon = tricks
        return true
    }
    
    fun canAdvanceToNextRound(): Boolean {
        val round = currentRound ?: return false
        return round.playerScores.values.all { it.isComplete() }
    }
    
    fun advanceToNextRound(): Boolean {
        if (!canAdvanceToNextRound()) return false
        
        // Calculate and update player scores
        val round = currentRound!!
        round.playerScores.values.forEach { playerRoundScore ->
            val player = _players.find { it.name == playerRoundScore.playerName }
            player?.let {
                it.totalScore += playerRoundScore.calculateScore()
            }
        }
        
        _currentRoundIndex++
        return true
    }
    
    fun getWinner(): Player? {
        if (!isGameFinished) return null
        return _players.maxByOrNull { it.totalScore }
    }
    
    fun getTotalBids(): Int {
        return currentRound?.playerScores?.values?.sumOf { it.bid ?: 0 } ?: 0
    }
    
    fun reset() {
        _players.clear()
        _rounds.clear()
        _currentRoundIndex = 0
    }
}