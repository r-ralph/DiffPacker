package ms.ralph.diffpacker.ui

import ms.ralph.diffpacker.task.runner.RegistrarRunner
import ms.ralph.diffpacker.task.Task
import ms.ralph.diffpacker.util.DropFileHandler
import ms.ralph.diffpacker.util.SharedDataUtil
import ms.ralph.diffpacker.util.ext.appendln
import java.awt.BorderLayout
import java.io.File
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

class RegistrationWindow : JFrame() {

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

        textArea.appendln("登録モード")
        textArea.appendln("ここに登録するファイルをドラッグ＆ドロップしてください")
    }

    private fun onFileDropped(files: List<File>) {
        if (isRunning) {
            return
        }
        textArea.appendln("「完了しました」と表示されるまで次のファイルを入れないでください")

        isRunning = true
        val task = Task(RegistrarRunner(SharedDataUtil.getSharedDataPath(), SharedDataUtil.getSharedDirPath(), files.map(File::toPath)))
        task.setOnMessageListener { SwingUtilities.invokeLater { textArea.appendln(it) } }
        task.setOnFinishListener {
            SwingUtilities.invokeLater {
                textArea.appendln("完了しました")
                isRunning = false
            }
        }
        task.start()
    }
}