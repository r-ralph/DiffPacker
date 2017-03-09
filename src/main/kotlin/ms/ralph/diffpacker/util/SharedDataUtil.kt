package ms.ralph.diffpacker.util

import kotlinx.support.jdk7.use
import ms.ralph.diffpacker.util.ext.packStringArray
import ms.ralph.diffpacker.util.ext.unpackStringArray
import org.msgpack.core.MessagePack
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*
import java.util.*

object SharedDataUtil {

    const val DPF_EXTENSION = "dpf"
    const val DPF_EXTENSION_DOT = ".$DPF_EXTENSION"

    fun load(path: Path): HashSet<String> {
        if (!Files.exists(path)) {
            println("DPS file not found")
            return hashSetOf()
        }
        MessagePack.newDefaultUnpacker(Files.newByteChannel(path, READ)).use {
            return it.unpackStringArray().toHashSet()
        }
    }

    fun save(path: Path, set: HashSet<String>) {
        MessagePack.newDefaultPacker(Files.newByteChannel(path, CREATE, TRUNCATE_EXISTING, WRITE)).use {
            it.packStringArray(set.toTypedArray())
        }
    }

    fun getSharedDataPath(): Path {
        return File("shared.dps").toPath()
    }

    fun getSharedDirPath(): Path {
        return File("shared").toPath()
    }
}