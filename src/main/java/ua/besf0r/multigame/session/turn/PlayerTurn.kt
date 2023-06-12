package ua.besf0r.multigame.session.turn

import kotlinx.serialization.Serializable
import ua.besf0r.multigame.session.SessionData
import ua.besf0r.multigame.session.SessionSettings

@Serializable
data class PlayerTurn(
    var side: SideEnum,
    val player: String,
    val settings: SessionSettings
)
fun PlayerTurn.findSession(sessions: MutableList<SessionData>): SessionData? {
    return sessions.find { it.settings == settings && it.canPlayerJoin
            && it.settings.maxPlayer > it.players.size}
}
