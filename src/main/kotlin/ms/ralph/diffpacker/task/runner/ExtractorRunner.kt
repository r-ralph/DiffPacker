package ms.ralph.diffpacker.task.runner

import kotlinx.support.jdk7.use
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.Utils
import ms.ralph.diffpacker.util.ext.unpackStringArray
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

class ExtractorRunner(val sharedDir: Path, val file: Path) : TaskRunner() {

    override fun run() {
        val destDir = Utils.avoidConflict(file.parent.resolve(file.fileName.toString().removeSuffix(SharedDataUtil.DPF_EXTENSION_DOT)))
        Files.createDirectories(destDir)
        MessagePack.newDefaultUnpacker(Files.newByteChannel(file, READ)).use {
            while (it.hasNext()) {
                val filePathArray = it.unpackStringArray()
                val destFile = Utils.stringArray2Path(destDir, filePathArray)
                Files.createDirectories(destFile.parent)
                val isSharedFile = it.unpackBoolean()
                if (isSharedFile) {
                    processSharedFile(it, destFile)
                } else {
                    processUnSharedFile(it, destFile)
                }
            }
        }
        onFinishListener?.invoke()
    }

    private fun processSharedFile(unpacker: MessageUnpacker, destFile: Path) {
        val sha256 = unpacker.unpackString()
        val file = sharedDir.resolve(sha256)
        if (!Files.exists(file)) {
            onMessageListener?.invoke("File not found: $sha256")
            return
        }
        Files.copy(file, destFile)
    }

    private fun processUnSharedFile(unpacker: MessageUnpacker, destFile: Path) {
        val size = unpacker.unpackBinaryHeader()
        val bytes = unpacker.readPayload(size)
        Files.write(destFile, bytes, CREATE, TRUNCATE_EXISTING, WRITE)
    }
}
