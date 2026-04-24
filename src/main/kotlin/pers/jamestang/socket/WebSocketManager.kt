package pers.jamestang.socket

import java.util.concurrent.ConcurrentHashMap

object WebSocketManager {

//    Map<username, Map<deviceId, WebSocketSession>>
    private val sessions = ConcurrentHashMap<String, ConcurrentHashMap<String, SocketSession>>()


    fun addSession(username: String, session: SocketSession){
        val userSessions = sessions.computeIfAbsent(username) { ConcurrentHashMap() }
        userSessions[session.deviceId] = session
    }

    fun removeSession(username: String, session: SocketSession) {
        sessions[username]?.remove(session.deviceId)
        if (sessions[username]?.size == 0) {
            sessions.remove(username)
        }
    }
}