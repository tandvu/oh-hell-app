package com.example.ohhell.model

data class Player(
    val name: String,
    var totalScore: Int = 0
)

data class Round(
    val roundNumber: Int,
    val cardsDealt: Int,
    val playerScores: MutableMap<String, PlayerRoundScore> = mutableMapOf()
)

data class PlayerRoundScore(
    val playerName: String,
    var bid: Int? = null,
    var tricksWon: Int? = null
) {
    fun calculateScore(): Int {
        return if (bid != null && tricksWon != null) {
            if (bid == tricksWon) {
                // Made the bid: 10 points + number of tricks
                10 + tricksWon!!
            } else {
                // Missed the bid: 0 points
                0
            }
        } else {
            0
        }
    }
    
    fun isComplete(): Boolean = bid != null && tricksWon != null
}