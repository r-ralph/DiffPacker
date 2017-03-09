package ms.ralph.diffpacker.ui

import ms.ralph.diffpacker.task.Task
import ms.ralph.diffpacker.task.runner.CompressorRunner
import ms.ralph.diffpacker.util.DropFileHandler
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.ext.appendln
import java.awt.BorderLayout
import java.io.File
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

class CompressionWindow : JFrame() {

    private val dropFileHandler = DropFileHandler()

    private var textArea: JTextArea
    private var isRunning = false

    init {
        setSize(600, 400)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        dropFileHandler.setDropFilesListener {
            onFileDropped(it)
        }

        val scrollPane = JScrollPane()
        contentPane.add(scrollPane, BorderLayout.CENTER)

        textArea = JTextArea()
        scrollPane.setViewportView(textArea)
        textArea.isEditable = false
        textArea.transferHandler = dropFileHandler

        textArea.appendln("圧縮モード")
        textArea.appendln("ここに圧縮するフォルダをドラッグ＆ドロップしてください")
    }

    private fun onFileDropped(files: List<File>) {
        if (isRunning) {
            return
        }
        if (files.isEmpty()) {
            textArea.appendln("File not found")
            return
        }
        if (files.size > 1) {
            textArea.appendln("1つのフォルダのみをドラッグ＆ドロップしてください")
            return
        }
        val file = files[0]
        if (!file.isDirectory) {
            textArea.appendln("圧縮するフォルダをドラッグ＆ドロップしてください")
            return
        }
        textArea.appendln("Directory Loaded: " + file.canonicalPath)
        textArea.appendln("「完了しました」と表示されるまでウィンドウを閉じないでください")

        isRunning = true
        val task = Task(CompressorRunner(SharedDataUtil.getSharedDataPath(), file.toPath()))
        task.setOnMessageListener { SwingUtilities.invokeLater { textArea.appendln(it) } }
        task.setOnFinishListener {
            SwingUtilities.invokeLater {
                isRunning = false
                textArea.appendln("完了しました")
            }
        }
        task.start()
    }
}

