package ms.ralph.diffpacker.task.runner

import kotlinx.support.jdk7.use
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.Utils
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class RegistrarRunner(val sharedDataPath: Path, val sharedDir: Path, val files: List<Path>) : TaskRunner() {

    private lateinit var sharedData: HashSet<String>

    override fun run() {
        sharedData = SharedDataUtil.load(sharedDataPath).toHashSet()
        Files.createDirectories(sharedDir)
        files.forEach { path ->
            if (Files.isRegularFile(path)) {
                processFile(path)
            } else {
                Files.walk(path).use {
                    it.filter { Files.isRegularFile(it) }
                            .forEach { processFile(it) }
                }
            }
        }
        SharedDataUtil.save(sharedDataPath, sharedData)
        onFinishListener?.invoke()
    }

    private fun processFile(file: Path) {
        onMessageListener?.invoke(file.toString())
        val sha256 = Utils.calcSha256(file)
        onMessageListener?.invoke("SHA-256: $sha256")
        if (sharedData.contains(sha256)) {
            onMessageListener?.invoke("Already registered!")
            return
        }
        val destFile = sharedDir.resolve(sha256)
        if (!Files.exists(destFile)) {
            Files.copy(file, destFile)
        }
        sharedData.add(sha256)
    }
}
