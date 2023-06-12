package ua.besf0r.multigame.session

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import ua.besf0r.multigame.state.State

data class SessionData(
    var canPlayerJoin: Boolean,
    val settings: SessionSettings,
    val playerInTurnList: MutableList<String> = mutableListOf(),

    val players: MutableList<Player> = mutableListOf(),
    var world: World,
    var state: State,
    var map: Location
)