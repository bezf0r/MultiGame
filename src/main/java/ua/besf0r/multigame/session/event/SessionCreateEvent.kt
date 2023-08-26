package ua.besf0r.multigame.session.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ua.besf0r.multigame.session.SessionData

class SessionCreateEvent(
    private val session: SessionData
): Event() {
    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    fun getSession(): SessionData {
        return session
    }
}
