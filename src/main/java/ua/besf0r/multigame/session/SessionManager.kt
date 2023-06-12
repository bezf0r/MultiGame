package ua.besf0r.multigame.session

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ua.besf0r.multigame.GamePlugin
import ua.besf0r.multigame.GameSettings
import ua.besf0r.multigame.map.CopyUtil
import java.util.*

class SessionManager(
    private val gameSettings: GameSettings
) {
    fun createSession(sessionSettings: SessionSettings): SessionData? {
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
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val world = CopyUtil().copyWorld(gameSettings.gameWorld, uuid.toString())!!
            session.world = world
            session.map.world = world

            gameSettings.sessions.add(session)
        })

        return session
    }
}