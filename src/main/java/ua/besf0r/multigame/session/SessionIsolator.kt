package ua.besf0r.multigame.session

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import ua.besf0r.multigame.GamePlugin
import ua.besf0r.multigame.GameSettings
import ua.besf0r.multigame.getSessionByPlayer

class SessionIsolator(
    private val gameSettings: GameSettings
): Listener {
    @EventHandler
    fun isolateChat(event: AsyncPlayerChatEvent) {
        val game = gameSettings.getSessionByPlayer(event.player)
        if (game != null) {
            event.recipients.removeIf { p -> !game.players.contains(p) }
        }
    }
    @EventHandler
    fun disableQuitMessages(event: PlayerQuitEvent) {
        event.quitMessage = ""

        val game = gameSettings.getSessionByPlayer(event.player)
        game?.players?.remove(event.player)
    }
    @EventHandler
    fun disableJoinMessages(event: PlayerJoinEvent) {
        event.joinMessage = ""
    }
    @EventHandler
    fun disableDeathMessages(event: PlayerDeathEvent) {
        event.deathMessage = ""
        val location = gameSettings.getSessionByPlayer(event.entity)?.map?: return
        event.entity.teleport(location)
    }
    @EventHandler
    fun isolatePlayerVisibility(event: PlayerJoinEvent) {
        val plugin = JavaPlugin.getProvidingPlugin(GamePlugin::class.java)

        val player = event.player
        val game = gameSettings.getSessionByPlayer(event.player)
        game?.playerInTurnList?.remove(event.player.name)
        Bukkit.getOnlinePlayers().forEach { otherPlayer ->
            if (game != null) {
                if (!game.players.contains(otherPlayer)) {
                    player.hidePlayer(plugin, otherPlayer)
                    otherPlayer.hidePlayer(plugin, player)
                }
            }
        }
    }
}