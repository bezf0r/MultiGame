package ua.besf0r.multigame.session.turn

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import ua.besf0r.multigame.GamePlugin
import ua.besf0r.multigame.GameSettings
import ua.besf0r.multigame.session.SessionData
import java.util.concurrent.CompletableFuture

class TurnListener(private val gameSettings: GameSettings): Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun registerOnTurn(event: PlayerJoinEvent){
        event.joinMessage = ""
        val playerTurn = gameSettings.sessions
            .find { it.playerInTurnList.contains(event.player.name) }
        if (playerTurn != null){
            addPlayer(playerTurn,event.player)
        }else{
            event.player.kickPlayer("§cYou are not registered in the queue")
            println("§c${event.player.name} are not registered in the queue")
        }
    }
    private fun addPlayer(session: SessionData, player: Player){
        player.gameMode = GameMode.SURVIVAL
        player.teleport(session.map)
        session.playerInTurnList.remove(player.name)

        session.players.add(player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage = ""
        val session = gameSettings.sessions.find {
            it.world == event.player.world } ?: return

        session.players.remove(event.player)
        session.playerInTurnList.remove(event.player.name)
        if (session.players.isEmpty()) {
            stopGame(session)
        }
    }
    private fun stopGame(session: SessionData){
        val plugin = JavaPlugin.getProvidingPlugin(GamePlugin::class.java)
        session.world.isAutoSave = false

        val scheduler = Bukkit.getScheduler()
        scheduler.cancelTask(session.state.taskId)

        Bukkit.getScheduler().runTask(plugin, Runnable{
            val server = Bukkit.getServer()
            server.unloadWorld(session.world, false)
            server.worlds.remove(session.world)
        })
        gameSettings.sessions.remove(session)
        CompletableFuture.runAsync {
            session.world.worldFolder.deleteRecursively()
        }

        println("§f[${session.world.name}]The game stopped due to zero online")
    }
}