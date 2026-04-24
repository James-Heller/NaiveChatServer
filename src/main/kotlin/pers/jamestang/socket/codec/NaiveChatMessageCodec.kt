package pers.jamestang.socket.codec

import pers.jamestang.socket.message.NaiveChatMessage
import pers.jamestang.util.toInt
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object NaiveChatMessageCodec {

    fun decode(bytes: ByteArray): NaiveChatMessage? {
        if (bytes.size < 6){
            return null
        }
        val version = bytes[0]
        val type = bytes[1]
        val length = bytes.sliceArray(2 until 6).toInt()
        val payload = bytes.sliceArray(6 until 6 + length)
        if ((length + 6) != bytes.size){
            return null
        }
        val msg = NaiveChatMessage(version, type, payload)
        return msg
    }

    fun encode(message: NaiveChatMessage): ByteArray {
        val baos = ByteArrayOutputStream()
        val stream = DataOutputStream(baos)
        stream.writeByte(message.version.toInt())
        stream.writeByte(message.type.toInt())
        stream.writeInt(message.payload.size)
        stream.write(message.payload)
        return baos.toByteArray()
    }
}