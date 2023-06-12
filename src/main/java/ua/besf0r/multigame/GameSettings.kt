package ua.besf0r.multigame

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import ua.besf0r.multigame.session.SessionData
import ua.besf0r.multigame.state.State
import java.util.*

data class GameSettings (
    var natsAddress: String,

    var gamePrefix: String,
    var standardState: State,
    var gameWorld: World,
    var spawnLocation: Location,
    var sessions: MutableList<SessionData> = mutableListOf()
)
fun GameSettings.getSessionByPlayer(player: Player): SessionData? {
    return this.sessions.find {
        it.players.contains(player) }
}