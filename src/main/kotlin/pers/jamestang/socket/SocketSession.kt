package pers.jamestang.socket

import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class SocketSession(
    val deviceId: String,
    val defaultWebSocketSession: DefaultWebSocketSession,
    val channel: Channel<String> = Channel(capacity = 100)
){
    private val logger = LoggerFactory.getLogger(SocketSession::class.java)
    private var activated: Boolean = false
    private var senderScope: Job? = null

    fun active(){
        if (activated) return
        activated = true
        senderScope = defaultWebSocketSession.launch {
            for (msg in channel) {
                try {
                    defaultWebSocketSession.outgoing.send(Frame.Text(msg))
                }catch (e: Exception) {
                    logger.error(e.message?: e.localizedMessage)
                    break

                }
            }
        }
    }
    suspend fun pushMessage(message: String) {
        channel.send(message)
    }

    fun inactive(){
        channel.close()
        senderScope?.cancel()
        activated = false
    }
}
