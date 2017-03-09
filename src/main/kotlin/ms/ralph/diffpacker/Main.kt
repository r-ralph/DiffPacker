package ms.ralph.diffpacker

import ms.ralph.diffpacker.ui.CompressionWindow
import ms.ralph.diffpacker.ui.ExtractionWindow
import java.awt.EventQueue
import java.io.File

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        val window = if (File("EXTRACT").exists()) ExtractionWindow() else CompressionWindow()
        window.isVisible = true
    }
}
