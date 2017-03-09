package ms.ralph.diffpacker.util.ext

import org.msgpack.core.MessageUnpacker

fun MessageUnpacker.unpackStringArray(): Array<String> {
    val size = unpackArrayHeader()
    val list = arrayListOf<String>()
    for (i in 0..size - 1) {
        list.add(unpackString())
    }
    return list.toTypedArray()
}
