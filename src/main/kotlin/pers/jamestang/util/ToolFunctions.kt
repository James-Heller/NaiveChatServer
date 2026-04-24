package pers.jamestang.util

fun Int.toByteArray(): ByteArray {
    val bytes = ByteArray(4)
    bytes[0] = (this shr 24 and 0xff).toByte()
    bytes[1] = (this shr 16 and 0xff).toByte()
    bytes[2] = (this shr 8 and 0xff).toByte()
    bytes[3] = (this and 0xff).toByte()
    return bytes
}

fun ByteArray.toInt(): Int {
    if (this.size != 4) throw IllegalArgumentException("Byte array must be of length 4 to convert to Int")
    return ((this[0].toInt() and 0xff) shl 24) or
            ((this[1].toInt() and 0xff) shl 16) or
            ((this[2].toInt() and 0xff) shl 8) or
            (this[3].toInt() and 0xff)
}