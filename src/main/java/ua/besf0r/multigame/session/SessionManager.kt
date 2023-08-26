package ua.besf0r.multigame.session

import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import ua.besf0r.multigame.GamePlugin
import ua.besf0r.multigame.GameSettings
import ua.besf0r.multigame.map.CopyUtil
import ua.besf0r.multigame.session.event.SessionCreateEvent
import java.util.*

class SessionManager(
    private val gameSettings: GameSettings
) {
    fun createSession(sessionSettings: SessionSettings,  callback: (SessionData) -> Unit): SessionData? {
        if (!Bukkit.getWorlds().contains(gameSettings.gameWorld)){
            return null
        }
        val uuid = UUID.randomUUID()
        val plugin = JavaPlugin.getProvidingPlugin(GamePlugin::class.java)

        val session = SessionData(
            true,
            sessionSettings,
            mutableListOf(),
            mutableListOf(),
            Bukkit.getWorld("world")!!,
            gameSettings.standardState,
            gameSettings.spawnLocation
        )
        val originalWorld = gameSettings.gameWorld.worldFolder
        val worldContainer = Bukkit.getWorldContainer()

        val copyFuture = CopyUtil.copyWorld(originalWorld,uuid.toString(),worldContainer)

        copyFuture.whenComplete { _, throwable ->
            if (throwable != null) {
                println("Failed to copy directory: ${throwable.message}")
            } else {
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    val world = WorldCreator(uuid.toString()).createWorld()
                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        if (world != null) {
                            session.world = world
                            session.map.world = world

                            gameSettings.sessions.add(session)
                            callback(session)

                            plugin.server.pluginManager.callEvent(SessionCreateEvent(session))
                            println("Directory copied successfully")
                        }
                    },10)
                })
            }
        }

        copyFuture.join()

        return session
    }
}