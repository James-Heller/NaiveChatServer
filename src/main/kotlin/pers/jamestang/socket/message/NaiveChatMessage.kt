package pers.jamestang.socket.message

/**
 * type:
 * 0x01 C2S_CHAT
 * 0x02 S2C_CHAT
 * 0x03 ACK
 * 0x04 PING
 * 0x05 PONG
 *
 * payload:
 * upload C2S_CHAT body：
 *
 * | toUser(4) | msgId(8) | dataLen(4) | data |
 *
 * download S2C_CHAT body：
 *
 * |  fromUser(4) | msgId(8) | dataLen(4) | data |
 */
data class NaiveChatMessage(
    val version: Byte,
    val type: Byte,
    val payload: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NaiveChatMessage

        if (version != other.version) return false
        if (type != other.type) return false

        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        return payload.contentHashCode()

    }


}
