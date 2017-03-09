package ms.ralph.diffpacker.util.ext

import org.msgpack.core.MessagePacker

fun MessagePacker.packStringArray(array: Array<String>) {
    packArrayHeader(array.size)
    array.forEach { packString(it) }
}
