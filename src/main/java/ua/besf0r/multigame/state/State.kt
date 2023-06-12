package ua.besf0r.multigame.state

import ua.besf0r.multigame.session.SessionData

interface State {
    var duration: Long
    var taskId: Int

    fun enterState(session: SessionData)
}