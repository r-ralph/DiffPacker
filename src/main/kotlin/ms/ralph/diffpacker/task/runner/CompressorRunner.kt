package ms.ralph.diffpacker.task.runner

import kotlinx.support.jdk7.use
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.Utils
import ms.ralph.diffpacker.util.ext.packStringArray
import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*
import java.util.*

class CompressorRunner(val sharedDataPath: Path, val dir: Path) : TaskRunner() {

    private lateinit var sharedData: HashSet<String>

    override fun run() {
        sharedData = SharedDataUtil.load(sharedDataPath).toHashSet()
        val destFile = dir.parent.resolve(dir.fileName.toString() + ".dpf")
        MessagePack.newDefaultPacker(Files.newByteChannel(destFile, CREATE, TRUNCATE_EXISTING, WRITE)).use { packer ->
            Files.walk(dir).use {
                it.filter { Files.isRegularFile(it) }
                        .forEach { processFile(packer, it) }
            }
        }
        onFinishListener?.invoke()
    }

    fun processFile(packer: MessagePacker, path: Path) {
        val sha256 = Utils.calcSha256(path)
        val relativePath = dir.relativize(path)
        onMessageListener?.invoke("$sha256 : $relativePath")
        val pathArray = Utils.path2StringArray(relativePath)
        packer.packStringArray(pathArray)
        if (sharedData.contains(sha256)) {
            onMessageListener?.invoke("Shared SHA-256 data found. Storing SHA-256 only.")
            packer.packBoolean(true)
            packer.packString(sha256)
        } else {
            packer.packBoolean(false)
            packer.packBinaryHeader(Files.size(path).toInt())
            packer.writePayload(Files.readAllBytes(path))
            onMessageListener?.invoke("Shared file not found. Storing binary.")
        }
    }
}
