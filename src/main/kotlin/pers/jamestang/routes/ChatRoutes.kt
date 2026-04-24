package pers.jamestang.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import pers.jamestang.socket.SocketSession
import pers.jamestang.socket.WebSocketManager
import pers.jamestang.socket.message.NaiveChatMessage
import pers.jamestang.util.NaiveChatSession
import pers.jamestang.util.toInt

fun Route.chatRoutes() {
    authenticate {
        route("/chat") {
            webSocket("connect") {
                val principal = call.sessions.get<NaiveChatSession>()!!
                val socket = SocketSession(deviceId = principal.deviceId, this)
                socket.active()
                WebSocketManager.addSession(principal.username, socket)

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Binary){
                            val bytes = frame.readBytes()
                            if (bytes.size < 6){
                                continue
                            }
                            val version = bytes[0]
                            val type = bytes[1]
                            val length = bytes.sliceArray(2 until 6).toInt()
                            val payload = bytes.sliceArray(6 until 6 + length)
                            if ((length + 6) != bytes.size){
                                continue
                            }
                            val msg = NaiveChatMessage(version, type, payload)
                        }

                    }
                } finally {
                    WebSocketManager.removeSession(principal.username, socket)
                    socket.inactive()
                }
            }
        }
    }
}