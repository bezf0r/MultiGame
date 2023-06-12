package ua.besf0r.multigame.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionSettings (
    val playerToStart: Int,
    val maxPlayer: Int,
    val playerInTeam: Int
)