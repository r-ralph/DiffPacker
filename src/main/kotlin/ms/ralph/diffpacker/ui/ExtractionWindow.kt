package ms.ralph.diffpacker.ui

import ms.ralph.diffpacker.task.Task
import ms.ralph.diffpacker.task.runner.ExtractorRunner
import ms.ralph.diffpacker.util.DropFileHandler
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.ext.appendln
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class ExtractionWindow : JFrame() {
    private val dropFileHandler = DropFileHandler()

    private var textArea: JTextArea
    private var button: JButton
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

        button = JButton()
        button.text = "登録"
        button.addActionListener {
            val window = RegistrationWindow()
            window.isVisible = true
            isVisible = false
        }
        contentPane.add(button, BorderLayout.SOUTH)

        textArea.appendln("解凍モード")
        textArea.appendln("ここにdpfファイルをドラッグ＆ドロップしてください")
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
            textArea.appendln("dpfファイルのみをドラッグ＆ドロップしてください")
            return
        }
        val file = files[0]
        if (file.isDirectory || file.extension != SharedDataUtil.DPF_EXTENSION) {
            textArea.appendln("dpfファイルをドラッグ＆ドロップしてください")
            return
        }
        textArea.appendln("DPF file Loaded: " + file.canonicalPath)
        textArea.appendln("「完了しました」と表示されるまでウィンドウを閉じないでください")

        isRunning = true
        val task = Task(ExtractorRunner(SharedDataUtil.getSharedDirPath(), file.toPath()))
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