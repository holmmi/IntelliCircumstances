package fi.metropolia.intellicircumstances.extension

import java.nio.ByteBuffer

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

fun Long.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
    buffer.putLong(this)
    return buffer.array()
}

fun ByteArray.toInt32(): Int {
    if (this.size != 4) {
        throw IllegalAccessException("ByteArray should have 4 elements")
    }
    return ByteBuffer.wrap(this).int
}