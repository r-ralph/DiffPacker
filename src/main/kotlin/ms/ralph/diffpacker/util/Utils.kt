package ms.ralph.diffpacker.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest

object Utils {
    private val ALGORITHM = "SHA-256"

    @Throws(Exception::class)
    fun calcSha256(path: Path): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        Files.newInputStream(path, StandardOpenOption.READ).use {
            var n = 0
            val buffer = ByteArray(8192)
            while (n != -1) {
                n = it.read(buffer)
                if (n > 0) {
                    digest.update(buffer, 0, n)
                }
            }
        }
        return toHexString(digest.digest())
    }

    private fun toHexString(digest: ByteArray): String {
        val buff = StringBuffer()
        for (b in digest) {
            buff.append(String.format("%1$02x", b))
        }
        return buff.toString()
    }

    fun path2StringArray(path: Path): Array<String> {
        val array = (0..path.nameCount - 1).map { path.getName(it).toString() }
        return array.toTypedArray()
    }

    fun stringArray2Path(base: Path, array: Array<String>): Path {
        var path = base
        array.forEach {
            path = path.resolve(it)
        }
        return path
    }

    fun avoidConflict(path: Path): Path {
        val dirName = path.fileName
        var retPath = path
        var i = 1
        while (Files.exists(retPath)) {
            retPath = retPath.parent.resolve("${dirName}_$i")
            i += 1
        }
        return retPath
    }
}