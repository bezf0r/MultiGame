package ua.besf0r.multigame

import io.nats.client.Connection
import io.nats.client.Nats
import io.nats.client.Options
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import ua.besf0r.multigame.session.SessionData
import ua.besf0r.multigame.session.SessionIsolator
import ua.besf0r.multigame.session.SessionManager
import ua.besf0r.multigame.session.turn.PlayerTurn
import ua.besf0r.multigame.session.turn.SideEnum
import ua.besf0r.multigame.session.turn.TurnListener
import ua.besf0r.multigame.session.turn.findSession

object CoordinatorClient {
    fun GameSettings.enableConnection(){
        val plugin = JavaPlugin.getProvidingPlugin(GamePlugin::class.java)
        plugin.server.pluginManager.registerEvents(TurnListener(this),plugin)
        plugin.server.pluginManager.registerEvents(SessionIsolator(this),plugin)

        val options = Options.Builder().
        server(natsAddress).
        maxReconnects(-1).build()

        val connection = Nats.connect(options)

        val dispatcher = connection.createDispatcher { message ->
            try {
                val playerTurn = Json.decodeFromString<PlayerTurn>(message.data.decodeToString())
                if (playerTurn.side == SideEnum.CLIENT) {
                    val session = playerTurn.findSession(sessions)
                    if (session == null) {
                        SessionManager(this).createSession(playerTurn.settings) {
                            registerPlayerInTurn(it, connection, "${gamePrefix}-out", playerTurn)
                        }
                    } else {
                        if (!session.playerInTurnList.contains(playerTurn.player)) {
                            registerPlayerInTurn(session, connection, "${gamePrefix}-out", playerTurn)
                        }
                    }
                }
                message.ack()
            } catch (_: IllegalArgumentException) {
                message.ack()
            }
        }
        dispatcher.subscribe("${gamePrefix}-in")
    }
    private fun registerPlayerInTurn(
        session: SessionData, connection: Connection,
        gamePrefix: String, playerTurn: PlayerTurn){
        session.playerInTurnList.add(playerTurn.player)
        connection.publish(
            gamePrefix,
            Json.encodeToString(playerTurn.apply { side = SideEnum.SERVER })
                .toByteArray(Charsets.UTF_8)
        )
    }
}